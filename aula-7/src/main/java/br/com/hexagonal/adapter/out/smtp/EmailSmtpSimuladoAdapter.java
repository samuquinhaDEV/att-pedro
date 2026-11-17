package br.com.hexagonal.adapter.out.smtp;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.stereotype.Component;

import br.com.hexagonal.domain.model.Fatura;
import br.com.hexagonal.domain.port.out.NotificadorEmailSmtpPort;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EmailSmtpSimuladoAdapter implements NotificadorEmailSmtpPort {

	@Override
	public String enviarConfirmacaoFatura(Fatura fatura) {
		String corpoEmail = String.format(
			"Olá! Sua fatura %s do pedido #%d no valor de R$ %s foi gerada. Boleto: %s | Chave NF-e: %s",
			fatura.getId(),
			fatura.getPedidoId(),
			fatura.getValor(),
			fatura.getCodigoBarrasBoleto(),
			fatura.getChaveAcessoNfe()
		);

		log.info("[SMTP] Disparando notificação ao cliente: {}", corpoEmail);
		String hash = calcularSha256(corpoEmail);
		log.info("[SMTP] E-mail registrado com hash SHA-256: {}", hash);
		return hash;
	}

	private String calcularSha256(String conteudo) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(conteudo.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(hashBytes);
		} catch (NoSuchAlgorithmException ex) {
			throw new IllegalStateException("Algoritmo SHA-256 indisponível", ex);
		}
	}
}
