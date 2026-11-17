package br.com.mvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import br.com.mvc.exception.IntegracaoFaturamentoException;
import br.com.mvc.integration.FaturamentoGateway;
import br.com.mvc.model.Pedido;
import br.com.mvc.model.Product;
import br.com.mvc.model.StatusPedido;
import br.com.mvc.repository.PedidoRepository;
import br.com.mvc.service.EstoqueService;
import br.com.mvc.service.PedidoService;
import br.com.mvc.service.ProductService;

@SpringBootTest
@Transactional
class PedidoFaturamentoIntegrationTest {

	@Autowired
	private ProductService productService;

	@Autowired
	private EstoqueService estoqueService;

	@Autowired
	private PedidoService pedidoService;

	@Autowired
	private PedidoRepository pedidoRepository;

	@MockitoBean
	private FaturamentoGateway faturamentoGateway;

	@Test
	void deveFecharPedidoESalvarIdDaFaturaRetornadoPeloHub() {
		Product notebook = productService.cadastrar("Notebook", new BigDecimal("3500.00"));
		estoqueService.darEntrada(notebook.getId(), 4);
		Pedido pedido = pedidoService.abrirPedido(notebook.getId(), 2);

		when(faturamentoGateway.solicitarFaturamento(eq(pedido.getId()), any(BigDecimal.class)))
			.thenReturn("9c7f1a10-2b3d-4c5e-9f12-001122334455");

		Pedido fechado = pedidoService.finalizarPedido(pedido.getId());

		assertEquals(StatusPedido.FECHADO, fechado.getStatus());
		assertEquals("9c7f1a10-2b3d-4c5e-9f12-001122334455", fechado.getFaturaId());
		assertEquals(2, productService.buscarPorId(notebook.getId()).getSaldoEstoque());
	}

	@Test
	void devePropagarErroQuandoIntegracaoComHubFalharSemFecharPedido() {
		Product ssd = productService.cadastrar("SSD 1TB", new BigDecimal("420.00"));
		estoqueService.darEntrada(ssd.getId(), 5);
		Pedido pedido = pedidoService.abrirPedido(ssd.getId(), 1);

		when(faturamentoGateway.solicitarFaturamento(eq(pedido.getId()), any(BigDecimal.class)))
			.thenThrow(new IntegracaoFaturamentoException("Hub indisponível"));

		assertThrows(
			IntegracaoFaturamentoException.class,
			() -> pedidoService.finalizarPedido(pedido.getId())
		);

		Pedido persistido = pedidoRepository.findById(pedido.getId()).orElseThrow();
		assertEquals(StatusPedido.ABERTO, persistido.getStatus());
		assertNull(persistido.getFaturaId());
	}
}
