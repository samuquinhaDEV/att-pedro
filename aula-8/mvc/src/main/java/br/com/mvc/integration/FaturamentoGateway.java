package br.com.mvc.integration;

import java.math.BigDecimal;

public interface FaturamentoGateway {
	String solicitarFaturamento(Long pedidoId, BigDecimal valorTotal);
}
