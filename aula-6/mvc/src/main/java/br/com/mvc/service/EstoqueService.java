package br.com.mvc.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mvc.exception.RegraNegocioException;
import br.com.mvc.model.LancamentoEstoque;
import br.com.mvc.model.Product;
import br.com.mvc.model.TipoLancamento;
import br.com.mvc.repository.LancamentoEstoqueRepository;
import br.com.mvc.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EstoqueService {

	private final ProductRepository productRepository;
	private final LancamentoEstoqueRepository lancamentoRepository;

	public List<LancamentoEstoque> listarHistorico() {
		return lancamentoRepository.findAllByOrderByRegistradoEmDesc();
	}

	@Transactional
	public LancamentoEstoque darEntrada(Long productId, Integer quantidade) {
		return darEntrada(productId, quantidade, "Entrada manual");
	}

	@Transactional
	public LancamentoEstoque darEntrada(Long productId, Integer quantidade, String observacao) {
		return movimentar(productId, quantidade, TipoLancamento.ENTRADA, observacao);
	}

	@Transactional
	public LancamentoEstoque darSaida(Long productId, Integer quantidade) {
		return darSaida(productId, quantidade, "Saída manual");
	}

	@Transactional
	public LancamentoEstoque darSaida(Long productId, Integer quantidade, String observacao) {
		return movimentar(productId, quantidade, TipoLancamento.SAIDA, observacao);
	}

	private LancamentoEstoque movimentar(
		Long productId,
		Integer quantidade,
		TipoLancamento tipo,
		String observacao
	) {
		if (quantidade == null || quantidade <= 0) {
			throw new RegraNegocioException("Informe uma quantidade válida maior que zero.");
		}

		Product produto = productRepository.findById(productId)
			.orElseThrow(() -> new RegraNegocioException("Produto não localizado: id=" + productId));

		if (tipo == TipoLancamento.ENTRADA) {
			produto.adicionarEstoque(quantidade);
		} else {
			produto.baixarEstoque(quantidade);
		}

		productRepository.save(produto);
		LancamentoEstoque lancamento = LancamentoEstoque.registrar(produto, tipo, quantidade, observacao);
		return lancamentoRepository.save(lancamento);
	}
}
