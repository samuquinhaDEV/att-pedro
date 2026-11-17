package br.com.hexagonal.adapter.in.rest.dto;

import java.math.BigDecimal;

public record CriarFaturaRequest(Long pedidoId, BigDecimal valor) {
}
