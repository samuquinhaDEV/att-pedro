package br.com.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.mvc.exception.RegraNegocioException;
import br.com.mvc.service.EstoqueService;
import br.com.mvc.service.ProductService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/estoque")
@RequiredArgsConstructor
public class EstoqueController {

	private final EstoqueService estoqueService;
	private final ProductService productService;

	@GetMapping
	public String paginaEstoque(Model model) {
		model.addAttribute("produtos", productService.listarTodos());
		model.addAttribute("historico", estoqueService.listarHistorico());
		return "estoque";
	}

	@PostMapping("/entrada")
	public String processarEntrada(
		@RequestParam Long productId,
		@RequestParam Integer quantidade,
		@RequestParam(required = false) String observacao,
		RedirectAttributes redirect
	) {
		try {
			estoqueService.darEntrada(productId, quantidade, observacao);
			redirect.addFlashAttribute("mensagemSucesso", "Entrada registrada no estoque.");
		} catch (RegraNegocioException ex) {
			redirect.addFlashAttribute("mensagemErro", ex.getMessage());
		}
		return "redirect:/estoque";
	}

	@PostMapping("/saida")
	public String processarSaida(
		@RequestParam Long productId,
		@RequestParam Integer quantidade,
		@RequestParam(required = false) String observacao,
		RedirectAttributes redirect
	) {
		try {
			estoqueService.darSaida(productId, quantidade, observacao);
			redirect.addFlashAttribute("mensagemSucesso", "Saída registrada no estoque.");
		} catch (RegraNegocioException ex) {
			redirect.addFlashAttribute("mensagemErro", ex.getMessage());
		}
		return "redirect:/estoque";
	}
}
