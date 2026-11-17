package br.com.mvc.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.mvc.exception.RegraNegocioException;
import br.com.mvc.model.PerfilUsuario;
import br.com.mvc.service.ProductService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@GetMapping
	public String paginaProdutos(Model model) {
		model.addAttribute("produtos", productService.listarTodos());
		model.addAttribute("perfis", PerfilUsuario.values());
		return "products";
	}

	@PostMapping
	public String cadastrarProduto(
		@RequestParam String nome,
		@RequestParam BigDecimal preco,
		RedirectAttributes redirect
	) {
		try {
			productService.cadastrar(nome, preco);
			redirect.addFlashAttribute("mensagemSucesso", "Produto cadastrado inicialmente como INDISPONIVEL.");
		} catch (RegraNegocioException ex) {
			redirect.addFlashAttribute("mensagemErro", ex.getMessage());
		}
		return "redirect:/products";
	}

	@PostMapping("/{id}/descontinuar")
	public String descontinuarProduto(
		@PathVariable Long id,
		@RequestParam(defaultValue = "VENDEDOR") PerfilUsuario perfil,
		RedirectAttributes redirect
	) {
		try {
			productService.descontinuarProduto(id, perfil);
			redirect.addFlashAttribute("mensagemSucesso", "Produto marcado como DESCONTINUADO.");
		} catch (RegraNegocioException ex) {
			redirect.addFlashAttribute("mensagemErro", ex.getMessage());
		}
		return "redirect:/products";
	}
}
