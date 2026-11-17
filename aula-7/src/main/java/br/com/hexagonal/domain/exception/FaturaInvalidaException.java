package br.com.hexagonal.domain.exception;

public class FaturaInvalidaException extends RuntimeException {

	public FaturaInvalidaException(String mensagem) {
		super(mensagem);
	}
}
