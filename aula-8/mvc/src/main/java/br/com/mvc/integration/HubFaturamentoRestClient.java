package br.com.mvc.integration;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import br.com.mvc.exception.IntegracaoFaturamentoException;

@Component
public class HubFaturamentoRestClient implements FaturamentoGateway {

	private final RestClient restClient;

	public HubFaturamentoRestClient(@Value("${integracao.hub-faturamento.url}") String hubBaseUrl) {
		HttpClient httpClient = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(4))
			.build();

		JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
		requestFactory.setReadTimeout(Duration.ofSeconds(4));

		this.restClient = RestClient.builder()
			.baseUrl(hubBaseUrl)
			.requestFactory(requestFactory)
			.build();
	}

	@Override
	public String solicitarFaturamento(Long pedidoId, BigDecimal valorTotal) {
		try {
			FaturaCriadaResponse resposta = restClient.post()
				.uri("/fatura")
				.contentType(MediaType.APPLICATION_JSON)
				.body(new FaturarPedidoPayload(pedidoId, valorTotal))
				.retrieve()
				.body(FaturaCriadaResponse.class);

			if (resposta == null || resposta.faturaId() == null || resposta.faturaId().isBlank()) {
				throw new IntegracaoFaturamentoException(
					"Hub de faturamento respondeu sem o identificador da fatura."
				);
			}
			return resposta.faturaId();
		} catch (RestClientException ex) {
			throw new IntegracaoFaturamentoException(
				"Falha ao comunicar com o hub de faturamento para o pedido " + pedidoId,
				ex
			);
		}
	}

	public record FaturarPedidoPayload(Long pedidoId, BigDecimal valor) {
	}

	public record FaturaCriadaResponse(String faturaId) {
	}
}
