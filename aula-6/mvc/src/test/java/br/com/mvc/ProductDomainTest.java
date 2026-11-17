package br.com.mvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import br.com.mvc.exception.RegraNegocioException;
import br.com.mvc.model.PerfilUsuario;
import br.com.mvc.model.Product;
import br.com.mvc.model.StatusProduto;

class ProductDomainTest {

	@Test
	void produtoNovoComecaIndisponivelEAlternaStatusConformesaldo() {
		Product produto = Product.novo("Headset", new BigDecimal("149.90"));

		assertEquals(StatusProduto.INDISPONIVEL, produto.getStatus());
		assertEquals(0, produto.getSaldoEstoque());

		produto.adicionarEstoque(4);
		assertEquals(4, produto.getSaldoEstoque());
		assertEquals(StatusProduto.DISPONIVEL, produto.getStatus());

		produto.baixarEstoque(1);
		assertEquals(3, produto.getSaldoEstoque());
		assertEquals(StatusProduto.DISPONIVEL, produto.getStatus());

		produto.baixarEstoque(3);
		assertEquals(0, produto.getSaldoEstoque());
		assertEquals(StatusProduto.INDISPONIVEL, produto.getStatus());
	}

	@Test
	void apenasAdmDescontinuaEProdutoDescontinuadoBloqueiaMovimentacao() {
		Product produto = Product.novo("Webcam", new BigDecimal("220.00"));
		produto.adicionarEstoque(2);

		assertThrows(RegraNegocioException.class, () -> produto.descontinuar(PerfilUsuario.VENDEDOR));
		assertThrows(RegraNegocioException.class, () -> produto.descontinuar(PerfilUsuario.ESTOQUE));

		produto.descontinuar(PerfilUsuario.ADM);
		assertEquals(StatusProduto.DESCONTINUADO, produto.getStatus());

		assertThrows(RegraNegocioException.class, () -> produto.adicionarEstoque(5));
		assertThrows(RegraNegocioException.class, () -> produto.baixarEstoque(1));
	}
}
