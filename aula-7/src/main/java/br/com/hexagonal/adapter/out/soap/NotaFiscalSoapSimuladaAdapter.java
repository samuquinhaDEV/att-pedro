package br.com.hexagonal.adapter.out.soap;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

import br.com.hexagonal.domain.model.Fatura;
import br.com.hexagonal.domain.port.out.EmissorNotaFiscalSoapPort;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NotaFiscalSoapSimuladaAdapter implements EmissorNotaFiscalSoapPort {

	@Override
	public String solicitarEmissaoNfe(Fatura fatura) {
		String envelopeXml = String.format(
			"<soapenv:Envelope><soapenv:Body><EmitirNFe><pedidoId>%d</pedidoId><valor>%s</valor></EmitirNFe></soapenv:Body></soapenv:Envelope>",
			fatura.getPedidoId(),
			fatura.getValor()
		);
		log.info("[SOAP-NFE] Enviando requisição XML: {}", envelopeXml);

		// Simula chave de acesso NF-e padrão SEFAZ com exatamente 44 dígitos numéricos
		StringBuilder chave = new StringBuilder("3526");
		while (chave.length() < 44) {
			chave.append(ThreadLocalRandom.current().nextInt(10));
		}

		String chaveAcesso = chave.toString();
		log.info("[SOAP-NFE] NF-e autorizada com chave de acesso: {}", chaveAcesso);
		return chaveAcesso;
	}
}
