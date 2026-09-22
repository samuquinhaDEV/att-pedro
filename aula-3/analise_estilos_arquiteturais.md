# Análise de Estilos Arquiteturais

**Disciplina:** Arquitetura de Software

**Atividade:** Destrinchando os estilos

## 1. Arquitetura Cliente-Servidor

### Conceito

A arquitetura Cliente-Servidor organiza o sistema em duas partes principais: o **cliente**, responsável por fazer solicitações, e o **servidor**, responsável por receber essas solicitações, realizar o processamento necessário e retornar uma resposta.

Nesse modelo, o cliente normalmente representa a aplicação utilizada pelo usuário, enquanto o servidor concentra serviços, regras de negócio e, muitas vezes, o acesso aos dados. A comunicação acontece por meio de requisições e respostas entre as duas partes.

### Exemplos de utilização

Esse modelo está presente em diversas aplicações utilizadas diariamente, principalmente em sistemas que precisam disponibilizar informações ou serviços para vários usuários.

- **Acesso a sites:** quando uma pessoa entra em um site utilizando um navegador, o navegador funciona como cliente. Ele solicita os conteúdos ao servidor, que processa a requisição e envia as informações de volta.

- **Serviço de DNS:** quando um usuário digita o endereço de um site, o computador pode consultar um servidor DNS para descobrir qual endereço IP está relacionado àquele domínio. Depois de receber a resposta, o dispositivo consegue localizar o servidor desejado.

### Vantagens

- **Facilidade de organização:** a divisão entre cliente e servidor torna mais clara a responsabilidade de cada parte do sistema.

- **Manutenção centralizada:** alterações realizadas no servidor podem beneficiar todos os clientes que utilizam o serviço, sem que seja necessário modificar cada dispositivo individualmente.

- **Compartilhamento de recursos:** diversos clientes podem utilizar simultaneamente os mesmos serviços e informações disponibilizados pelo servidor.

### Desvantagens

- **Possibilidade de sobrecarga:** uma quantidade muito grande de requisições simultâneas pode sobrecarregar o servidor e causar lentidão.

- **Dependência do servidor:** caso o servidor principal apresente uma falha, os clientes podem ficar impossibilitados de acessar os serviços oferecidos.

- **Dependência da comunicação:** problemas na rede também podem impedir ou prejudicar a troca de informações entre cliente e servidor.

---

## 2. Arquitetura Publicador/Assinante (Pub/Sub)

### Conceito

O modelo **Publicador/Assinante**, também conhecido como **Pub/Sub**, utiliza uma comunicação baseada em eventos e mensagens. Nesse formato, um componente chamado **publicador** produz uma informação e envia essa mensagem para um intermediário, geralmente chamado de *broker*.

Os **assinantes** demonstram interesse em determinados tipos de mensagens e recebem os eventos correspondentes. Dessa forma, o sistema não precisa estabelecer uma comunicação direta entre quem envia e quem recebe a informação.

Um dos principais objetivos desse modelo é diminuir a dependência entre os componentes, permitindo que eles funcionem de maneira mais independente.

### Exemplos de utilização

- **Sistemas de comércio eletrônico:** depois que um cliente finaliza uma compra, um evento pode ser criado informando que o pedido foi realizado. Serviços diferentes podem receber essa informação para atualizar o estoque, enviar um e-mail ao cliente e registrar os dados para análise.

- **Envio automático de e-mails:** após o cadastro de um usuário em uma plataforma, o sistema pode publicar um evento de novo cadastro. Um serviço responsável pelos e-mails recebe essa mensagem e envia a confirmação sem que o processo principal precise esperar o envio terminar.

### Vantagens

- **Menor dependência entre componentes:** o produtor da mensagem não precisa conhecer diretamente os serviços que irão utilizá-la.

- **Facilidade para adicionar novos serviços:** novos consumidores podem ser incluídos posteriormente sem necessidade de modificar o componente responsável pela publicação do evento.

- **Processamento assíncrono:** tarefas que demoram mais tempo podem ser executadas em segundo plano, evitando que o usuário precise esperar todo o processamento.

- **Maior flexibilidade:** diferentes serviços podem reagir ao mesmo evento de acordo com suas próprias necessidades.

### Desvantagens

- **Maior dificuldade para acompanhar o fluxo:** como as mensagens podem ser processadas por vários serviços de maneira independente, pode ser complicado identificar exatamente onde ocorreu um problema.

- **Tratamento de mensagens:** é necessário definir o que acontecerá caso um consumidor esteja indisponível ou apresente algum erro durante o processamento.

- **Maior complexidade:** a utilização de componentes intermediários, como brokers e filas, exige uma configuração e um monitoramento mais cuidadosos.

## Conclusão

Os estilos **Cliente-Servidor** e **Publicador/Assinante** possuem características diferentes e podem ser utilizados de acordo com as necessidades de cada sistema.

O Cliente-Servidor apresenta uma comunicação mais direta, sendo bastante comum em aplicações web e sistemas tradicionais. Já o Pub/Sub trabalha com eventos e mensagens, sendo útil quando é necessário reduzir o acoplamento entre serviços e permitir que diferentes componentes processem uma mesma informação de forma independente.

A escolha do estilo arquitetural deve considerar fatores como desempenho, facilidade de manutenção, escalabilidade, complexidade e o tipo de comunicação necessário para a aplicação.
