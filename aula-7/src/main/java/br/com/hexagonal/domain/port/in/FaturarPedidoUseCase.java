package br.com.hexagonal.domain.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public interface FaturarPedidoUseCase {

	UUID executar(FaturarPedidoCommand comando);

	record FaturarPedidoCommand(Long pedidoId, BigDecimal valor) {
	}
}
