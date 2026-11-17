package br.com.mvc.exception;

public class IntegracaoFaturamentoException extends RuntimeException {

	public IntegracaoFaturamentoException(String mensagem) {
		super(mensagem);
	}

	public IntegracaoFaturamentoException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
}
