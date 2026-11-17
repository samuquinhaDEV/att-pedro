package br.com.hexagonal.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.hexagonal.domain.exception.FaturaInvalidaException;
import lombok.Getter;

@Getter
public class Fatura {

	private final UUID id;
	private final Long pedidoId;
	private final BigDecimal valor;
	private final LocalDateTime dataCriacao;
	private String codigoBarrasBoleto;
	private String chaveAcessoNfe;
	private String hashNotificacaoEmail;

	private Fatura(
		UUID id,
		Long pedidoId,
		BigDecimal valor,
		LocalDateTime dataCriacao,
		String codigoBarrasBoleto,
		String chaveAcessoNfe,
		String hashNotificacaoEmail
	) {
		if (id == null) {
			throw new FaturaInvalidaException("O identificador da fatura é obrigatório.");
		}
		if (pedidoId == null || pedidoId <= 0) {
			throw new FaturaInvalidaException("O ID do pedido deve ser positivo.");
		}
		if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
			throw new FaturaInvalidaException("O valor da fatura deve ser maior que zero.");
		}

		this.id = id;
		this.pedidoId = pedidoId;
		this.valor = valor;
		this.dataCriacao = dataCriacao != null ? dataCriacao : LocalDateTime.now();
		this.codigoBarrasBoleto = codigoBarrasBoleto;
		this.chaveAcessoNfe = chaveAcessoNfe;
		this.hashNotificacaoEmail = hashNotificacaoEmail;
	}

	public static Fatura novaParaPedido(Long pedidoId, BigDecimal valor) {
		return new Fatura(UUID.randomUUID(), pedidoId, valor, LocalDateTime.now(), null, null, null);
	}

	public static Fatura reconstruir(
		UUID id,
		Long pedidoId,
		BigDecimal valor,
		LocalDateTime dataCriacao,
		String codigoBarrasBoleto,
		String chaveAcessoNfe,
		String hashNotificacaoEmail
	) {
		return new Fatura(
			id,
			pedidoId,
			valor,
			dataCriacao,
			codigoBarrasBoleto,
			chaveAcessoNfe,
			hashNotificacaoEmail
		);
	}

	public void registrarBoleto(String codigoBarras) {
		if (codigoBarras == null || codigoBarras.isBlank()) {
			throw new FaturaInvalidaException("O código de barras do boleto não pode ser vazio.");
		}
		this.codigoBarrasBoleto = codigoBarras.trim();
	}

	public void registrarNotaFiscal(String chaveAcesso) {
		if (chaveAcesso == null || !chaveAcesso.matches("\\d{44}")) {
			throw new FaturaInvalidaException("A chave de acesso da NF-e deve conter exatamente 44 dígitos.");
		}
		this.chaveAcessoNfe = chaveAcesso;
	}

	public void registrarNotificacaoEmail(String hashConteudo) {
		if (hashConteudo == null || hashConteudo.isBlank()) {
			throw new FaturaInvalidaException("O hash do conteúdo do e-mail é obrigatório.");
		}
		this.hashNotificacaoEmail = hashConteudo.trim();
	}
}
