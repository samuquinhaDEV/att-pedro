package br.com.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.mvc.exception.IntegracaoFaturamentoException;
import br.com.mvc.exception.RegraNegocioException;
import br.com.mvc.service.PedidoService;
import br.com.mvc.service.ProductService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

	private final PedidoService pedidoService;
	private final ProductService productService;

	@GetMapping
	public String paginaPedidos(Model model) {
		model.addAttribute("pedidos", pedidoService.listarTodos());
		model.addAttribute("produtos", productService.listarTodos());
		return "pedidos";
	}

	@PostMapping
	public String abrirPedido(
		@RequestParam Long productId,
		@RequestParam Integer quantidade,
		RedirectAttributes redirect
	) {
		try {
			pedidoService.abrirPedido(productId, quantidade);
			redirect.addFlashAttribute("mensagemSucesso", "Pedido criado em aberto.");
		} catch (RegraNegocioException ex) {
			redirect.addFlashAttribute("mensagemErro", ex.getMessage());
		}
		return "redirect:/pedidos";
	}

	@PostMapping("/{id}/fechar")
	public String fecharPedido(@PathVariable Long id, RedirectAttributes redirect) {
		try {
			pedidoService.finalizarPedido(id);
			redirect.addFlashAttribute("mensagemSucesso", "Pedido fechado e faturado com sucesso.");
		} catch (RegraNegocioException | IntegracaoFaturamentoException ex) {
			redirect.addFlashAttribute("mensagemErro", ex.getMessage());
		}
		return "redirect:/pedidos";
	}
}
