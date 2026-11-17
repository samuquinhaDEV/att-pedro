package br.com.hexagonal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.hexagonal.adapter.in.rest.FaturaRestController;
import br.com.hexagonal.domain.exception.FaturaInvalidaException;
import br.com.hexagonal.domain.port.in.FaturarPedidoUseCase;

@WebMvcTest(FaturaRestController.class)
class FaturaRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private FaturarPedidoUseCase faturarPedidoUseCase;

	@Test
	void deveRetornar201ComIdDaFatura() throws Exception {
		UUID idEsperado = UUID.randomUUID();
		when(faturarPedidoUseCase.executar(any())).thenReturn(idEsperado);

		mockMvc.perform(
			post("/fatura")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"pedidoId\":15,\"valor\":180.50}")
		)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.faturaId").value(idEsperado.toString()));
	}

	@Test
	void deveRetornar400QuandoPedidoForInvalido() throws Exception {
		when(faturarPedidoUseCase.executar(any()))
			.thenThrow(new FaturaInvalidaException("O valor da fatura deve ser maior que zero."));

		mockMvc.perform(
			post("/fatura")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"pedidoId\":15,\"valor\":0}")
		)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.erro").exists());
	}
}
