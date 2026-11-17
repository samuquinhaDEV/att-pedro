package br.com.mvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.com.mvc.exception.RegraNegocioException;
import br.com.mvc.model.LancamentoEstoque;
import br.com.mvc.model.PerfilUsuario;
import br.com.mvc.model.Product;
import br.com.mvc.model.StatusProduto;
import br.com.mvc.model.TipoLancamento;
import br.com.mvc.service.EstoqueService;
import br.com.mvc.service.ProductService;

@SpringBootTest
@Transactional
class EstoqueServiceTest {

	@Autowired
	private ProductService productService;

	@Autowired
	private EstoqueService estoqueService;

	@Test
	void deveRegistrarEntradaESaidaAtualizandoStatusESaldo() {
		Product criado = productService.cadastrar("Cadeira Gamer", new BigDecimal("950.00"));
		assertEquals(StatusProduto.INDISPONIVEL, criado.getStatus());

		LancamentoEstoque entrada = estoqueService.darEntrada(criado.getId(), 3, "Lote inicial");
		assertEquals(TipoLancamento.ENTRADA, entrada.getTipo());
		assertEquals(StatusProduto.DISPONIVEL, productService.buscarPorId(criado.getId()).getStatus());

		estoqueService.darSaida(criado.getId(), 3, "Venda total");
		Product zerado = productService.buscarPorId(criado.getId());
		assertEquals(0, zerado.getSaldoEstoque());
		assertEquals(StatusProduto.INDISPONIVEL, zerado.getStatus());

		assertThrows(RegraNegocioException.class, () -> estoqueService.darSaida(criado.getId(), 1));
	}

	@Test
	void deveBloquearMovimentacaoDeProdutoDescontinuado() {
		Product produto = productService.cadastrar("Impressora", new BigDecimal("780.00"));
		estoqueService.darEntrada(produto.getId(), 5);
		productService.descontinuarProduto(produto.getId(), PerfilUsuario.ADM);

		assertThrows(RegraNegocioException.class, () -> estoqueService.darEntrada(produto.getId(), 2));
		assertThrows(RegraNegocioException.class, () -> estoqueService.darSaida(produto.getId(), 1));
	}
}
