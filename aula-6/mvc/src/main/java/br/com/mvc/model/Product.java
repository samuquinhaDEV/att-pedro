package br.com.mvc.model;

import java.math.BigDecimal;

import br.com.mvc.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "produto")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nome;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal preco;

	@Column(name = "saldo_estoque", nullable = false)
	private Integer saldoEstoque = 0;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusProduto status = StatusProduto.INDISPONIVEL;

	public static Product novo(String nome, BigDecimal preco) {
		if (nome == null || nome.isBlank()) {
			throw new RegraNegocioException("Informe o nome do produto.");
		}
		if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
			throw new RegraNegocioException("O preço deve ser maior que zero.");
		}

		Product produto = new Product();
		produto.nome = nome.trim();
		produto.preco = preco;
		produto.saldoEstoque = 0;
		produto.status = StatusProduto.INDISPONIVEL;
		return produto;
	}

	public void adicionarEstoque(int quantidade) {
		if (quantidade <= 0) {
			throw new RegraNegocioException("A quantidade de entrada deve ser positiva.");
		}
		if (this.status == StatusProduto.DESCONTINUADO) {
			throw new RegraNegocioException("Produto descontinuado não pode ser manipulado no estoque.");
		}

		this.saldoEstoque += quantidade;
		if (this.saldoEstoque > 0) {
			this.status = StatusProduto.DISPONIVEL;
		}
	}

	public void baixarEstoque(int quantidade) {
		if (quantidade <= 0) {
			throw new RegraNegocioException("A quantidade de saída deve ser positiva.");
		}
		if (this.status == StatusProduto.DESCONTINUADO) {
			throw new RegraNegocioException("Produto descontinuado não pode ser manipulado no estoque.");
		}
		if (this.status == StatusProduto.INDISPONIVEL || this.saldoEstoque <= 0) {
			throw new RegraNegocioException("Produto sem estoque disponível para saída.");
		}
		if (this.saldoEstoque < quantidade) {
			throw new RegraNegocioException("Saldo insuficiente em estoque. Disponível: " + this.saldoEstoque);
		}

		this.saldoEstoque -= quantidade;
		if (this.saldoEstoque == 0) {
			this.status = StatusProduto.INDISPONIVEL;
		}
	}

	public void descontinuar(PerfilUsuario perfil) {
		if (perfil != PerfilUsuario.ADM) {
			throw new RegraNegocioException("Somente o perfil ADM pode descontinuar um produto.");
		}
		this.status = StatusProduto.DESCONTINUADO;
	}
}
