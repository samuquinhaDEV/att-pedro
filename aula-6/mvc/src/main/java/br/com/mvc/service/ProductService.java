package br.com.mvc.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mvc.exception.RegraNegocioException;
import br.com.mvc.model.PerfilUsuario;
import br.com.mvc.model.Product;
import br.com.mvc.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;

	public List<Product> listarTodos() {
		return productRepository.findAll();
	}

	public Product buscarPorId(Long id) {
		return productRepository.findById(id)
			.orElseThrow(() -> new RegraNegocioException("Produto não localizado: id=" + id));
	}

	@Transactional
	public Product cadastrar(String nome, BigDecimal preco) {
		Product novoProduto = Product.novo(nome, preco);
		return productRepository.save(novoProduto);
	}

	@Transactional
	public Product descontinuarProduto(Long productId, PerfilUsuario perfil) {
		Product produto = buscarPorId(productId);
		produto.descontinuar(perfil);
		return productRepository.save(produto);
	}
}
