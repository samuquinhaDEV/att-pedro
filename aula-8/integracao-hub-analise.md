# Análise de Problemas e Soluções na Integração MVC ↔ Hub de Faturamento

## Contexto da Arquitetura

No fechamento de um pedido (`PedidoService.finalizarPedido`), a aplicação de vendas em **MVC** realiza a baixa no estoque e envia uma requisição HTTP síncrona (`POST /fatura`) por meio de `HubFaturamentoRestClient` para o **Hub de Faturamento** (estruturado em **Arquitetura Hexagonal**). Por sua vez, o Hub cria a fatura, emite boleto via fila, emite a nota fiscal via SOAP, envia o e-mail de notificação via SMTP e devolve o `faturaId` para ser salvo no pedido.

Por envolver dois sistemas independentes comunicando-se via rede sem transação distribuída compartilhada, essa integração está exposta a falhas arquiteturais relevantes. A seguir são analisados dois problemas e suas respectivas soluções.

---

## Problema 1: Acoplamento Temporal Síncrono (Indisponibilidade do Hub ou de Serviços Externos Bloqueando a Venda)

### O que acontece com o pedido
Como `PedidoService.finalizarPedido` executa a chamada REST ao Hub dentro de uma transação síncrona, a conclusão do pedido depende da disponibilidade imediata de toda a cadeia do faturamento (o próprio Hub, o serviço SOAP da nota fiscal e o servidor SMTP). Caso o Hub esteja reiniciando ou o serviço SOAP da prefeitura/SEFAZ saia do ar temporariamente, o `HubFaturamentoRestClient` recebe erro HTTP 5xx ou falha de conexão e lança `IntegracaoFaturamentoException`. Com isso, a transação local sofre *rollback*: a baixa de estoque é desfeita e o pedido permanece com status `ABERTO` e sem `faturaId`.

### O que o negócio percebe
Mesmo com a loja funcionando normalmente e havendo produto em estoque, o vendedor não consegue fechar nenhuma venda enquanto o faturamento ou algum parceiro externo estiver instável. O problema em um serviço acessório (como o envio de e-mail ou SOAP) paralisa diretamente o faturamento no caixa, gerando perda de vendas e frustração para clientes e atendentes.

### Solução indicada: Comunicação Assíncrona com Fila de Mensagens, Retry e Dead Letter Queue (DLQ)
- **Onde atua:** Na **fronteira de integração entre o MVC e o Hub de Faturamento** — substituindo (ou complementando) o disparo síncrono de `FaturamentoGateway` por um **publicador de eventos no MVC** e criando um **novo adaptador de entrada por mensageria no Hub** (que consome a fila e chama a porta de entrada `FaturarPedidoUseCase`).
- **Justificativa:** Desacoplando o fechamento comercial da execução imediata das integrações externas, o pedido é fechado localmente como "aguardando faturamento" e o evento segue para uma fila persistente. Se o SOAP ou o SMTP falharem momentaneamente, o consumidor do Hub aplica políticas de **retry com backoff exponencial** até conseguir processar e devolver o `faturaId` via evento/callback; se o erro persistir após o limite de tentativas, a mensagem vai para uma **DLQ** para tratamento sem travar as vendas da loja.

---

## Problema 2: Inconsistência de Dados Entre Bancos (Falha Local Após Faturamento Remoto)

### O que acontece com o pedido
No fluxo atual, o MVC executa `estoqueService.darSaida(...)`, chama `POST /fatura` no Hub e só depois tenta realizar o *commit* da transação no banco local com o `faturaId` e o status `FECHADO`. Se o Hub concluir todo o processo (salvar a fatura, emitir boleto, autorizar NF-e via SOAP e enviar e-mail via SMTP) e devolver `201 Created`, mas imediatamente depois ocorrer um erro de banco de dados, queda de energia ou *deadlock* no MVC antes do *commit* local, o banco do MVC desfaz a transação. O resultado é uma divergência grave: para o MVC, o pedido continua `ABERTO`, sem `faturaId` e com o estoque intacto; para o Hub, a fatura já foi criada e os documentos externos já foram emitidos.

### O que o negócio percebe
O cliente recebe por e-mail o boleto e a nota fiscal referentes a uma compra que, no sistema de vendas da empresa, aparece como não concluída. Além da confusão no atendimento e risco de cobrança/entrega duplicada, o estoque não foi abatido no MVC, permitindo que outro vendedor comercialize o mesmo item físico que já havia sido faturado.

### Solução indicada: Transactional Outbox aliado ao Padrão Saga (Compensação)
- **Onde atua:**
  - O **Transactional Outbox** atua dentro da **camada de aplicação e persistência do MVC (`PedidoService`)**, gravando o fechamento do pedido e o registro da solicitação de faturamento na mesma transação ACID local antes de qualquer chamada externa.
  - O **Padrão Saga** atua na **coordenação entre o MVC e o Hub de Faturamento**, exigindo a criação de operações compensatórias (por exemplo, um endpoint/caso de uso no Hub para cancelar/estornar fatura e uma ação no MVC para estornar o estoque reservado caso o faturamento seja recusado em definitivo).
- **Justificativa:** Com o **Outbox**, elimina-se a janela de erro entre o sucesso da chamada remota e o *commit* local, garantindo que todo pedido fechado gere exatamente uma intenção persistida de faturamento. Complementarmente, caso uma etapa posterior da **Saga** falhe por regra de negócio ou erro definitivo, a transação compensatória desfaz os efeitos anteriores de forma controlada, mantendo os dois sistemas consistentes sem depender de transações distribuídas (2PC).
