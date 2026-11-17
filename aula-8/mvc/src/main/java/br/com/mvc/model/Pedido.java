package br.com.mvc.model;

import java.math.BigDecimal;
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
@Table(name = "pedido")
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "produto_id")
	private Product product;

	@Column(nullable = false)
	private Integer quantidade;

	@Column(name = "valor_total", nullable = false, precision = 12, scale = 2)
	private BigDecimal valorTotal;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusPedido status = StatusPedido.ABERTO;

	@Column(name = "fatura_id", length = 64)
	private String faturaId;

	@Column(name = "criado_em", nullable = false)
	private LocalDateTime criadoEm;

	public static Pedido abrir(Product product, Integer quantidade) {
		Pedido pedido = new Pedido();
		pedido.product = product;
		pedido.quantidade = quantidade;
		pedido.valorTotal = product.getPreco().multiply(BigDecimal.valueOf(quantidade));
		pedido.status = StatusPedido.ABERTO;
		pedido.criadoEm = LocalDateTime.now();
		return pedido;
	}
}
