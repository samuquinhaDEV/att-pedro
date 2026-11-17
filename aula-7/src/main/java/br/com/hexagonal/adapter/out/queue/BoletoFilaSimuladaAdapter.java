package br.com.hexagonal.adapter.out.queue;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

import br.com.hexagonal.domain.model.Fatura;
import br.com.hexagonal.domain.port.out.EmissorBoletoFilaPort;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BoletoFilaSimuladaAdapter implements EmissorBoletoFilaPort {

	@Override
	public String publicarSolicitacaoBoleto(Fatura fatura) {
		String mensagemFila = String.format(
			"{\"faturaId\":\"%s\",\"pedidoId\":%d,\"valor\":%s}",
			fatura.getId(),
			fatura.getPedidoId(),
			fatura.getValor()
		);
		log.info("[FILA-BOLETOS] Publicando mensagem na fila: {}", mensagemFila);

		// Simula linha digitável de boleto bancário (47 dígitos, banco 001)
		StringBuilder codigoBarras = new StringBuilder("00190");
		while (codigoBarras.length() < 47) {
			codigoBarras.append(ThreadLocalRandom.current().nextInt(10));
		}

		String gerado = codigoBarras.toString();
		log.info("[FILA-BOLETOS] Código de barras gerado para fatura {}: {}", fatura.getId(), gerado);
		return gerado;
	}
}
