package br.com.hexagonal.adapter.in.rest;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.hexagonal.domain.exception.FaturaInvalidaException;

@RestControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler(FaturaInvalidaException.class)
	public ResponseEntity<Map<String, String>> tratarFaturaInvalida(FaturaInvalidaException ex) {
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(Map.of("erro", ex.getMessage()));
	}
}
