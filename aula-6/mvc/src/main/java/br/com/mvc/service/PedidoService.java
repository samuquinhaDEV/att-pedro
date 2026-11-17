package br.com.mvc.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mvc.exception.RegraNegocioException;
import br.com.mvc.model.Pedido;
import br.com.mvc.model.Product;
import br.com.mvc.model.StatusPedido;
import br.com.mvc.model.StatusProduto;
import br.com.mvc.repository.PedidoRepository;
import br.com.mvc.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {

	private final PedidoRepository pedidoRepository;
	private final ProductRepository productRepository;
	private final EstoqueService estoqueService;

	public List<Pedido> listarTodos() {
		return pedidoRepository.findAll();
	}

	@Transactional
	public Pedido abrirPedido(Long productId, Integer quantidade) {
		if (quantidade == null || quantidade <= 0) {
			throw new RegraNegocioException("A quantidade do pedido deve ser maior que zero.");
		}

		Product produto = productRepository.findById(productId)
			.orElseThrow(() -> new RegraNegocioException("Produto não localizado: id=" + productId));

		if (produto.getStatus() != StatusProduto.DISPONIVEL) {
			throw new RegraNegocioException("Só é possível abrir pedido para produtos com status DISPONIVEL.");
		}

		return pedidoRepository.save(Pedido.abrir(produto, quantidade));
	}

	@Transactional
	public Pedido finalizarPedido(Long pedidoId) {
		Pedido pedido = pedidoRepository.findById(pedidoId)
			.orElseThrow(() -> new RegraNegocioException("Pedido não localizado: id=" + pedidoId));

		if (pedido.getStatus() != StatusPedido.ABERTO) {
			throw new RegraNegocioException("Somente pedidos em aberto podem ser fechados.");
		}

		estoqueService.darSaida(
			pedido.getProduct().getId(),
			pedido.getQuantidade(),
			"Venda pedido #" + pedido.getId()
		);
		pedido.setStatus(StatusPedido.FECHADO);
		return pedidoRepository.save(pedido);
	}
}
