package br.com.hexagonal.adapter.in.rest;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.hexagonal.adapter.in.rest.dto.CriarFaturaRequest;
import br.com.hexagonal.adapter.in.rest.dto.CriarFaturaResponse;
import br.com.hexagonal.domain.port.in.FaturarPedidoUseCase;
import br.com.hexagonal.domain.port.in.FaturarPedidoUseCase.FaturarPedidoCommand;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/fatura")
@RequiredArgsConstructor
public class FaturaRestController {

	private final FaturarPedidoUseCase faturarPedidoUseCase;

	@PostMapping
	public ResponseEntity<CriarFaturaResponse> criarFatura(@RequestBody CriarFaturaRequest request) {
		UUID faturaId = faturarPedidoUseCase.executar(
			new FaturarPedidoCommand(request.pedidoId(), request.valor())
		);
		return ResponseEntity
			.created(URI.create("/fatura/" + faturaId))
			.body(new CriarFaturaResponse(faturaId.toString()));
	}
}
