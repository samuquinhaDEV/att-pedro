package br.com.hexagonal.domain.port.out;

import br.com.hexagonal.domain.model.Fatura;

public interface NotificadorEmailSmtpPort {
	String enviarConfirmacaoFatura(Fatura fatura);
}
