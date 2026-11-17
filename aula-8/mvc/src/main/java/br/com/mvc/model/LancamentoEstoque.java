package br.com.mvc.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lancamento_estoque")
public class LancamentoEstoque {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "produto_id")
	private Product product;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TipoLancamento tipo;

	@Column(nullable = false)
	private Integer quantidade;

	@Column(length = 180)
	private String observacao;

	@Column(name = "registrado_em", nullable = false)
	private LocalDateTime registradoEm;

	public static LancamentoEstoque registrar(
		Product product,
		TipoLancamento tipo,
		Integer quantidade,
		String observacao
	) {
		LancamentoEstoque lancamento = new LancamentoEstoque();
		lancamento.product = product;
		lancamento.tipo = tipo;
		lancamento.quantidade = quantidade;
		lancamento.observacao = (observacao == null || observacao.isBlank()) ? "-" : observacao.trim();
		lancamento.registradoEm = LocalDateTime.now();
		return lancamento;
	}
}
