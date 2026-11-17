package br.com.hexagonal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.hexagonal.adapter.out.queue.BoletoFilaSimuladaAdapter;
import br.com.hexagonal.adapter.out.smtp.EmailSmtpSimuladoAdapter;
import br.com.hexagonal.adapter.out.soap.NotaFiscalSoapSimuladaAdapter;
import br.com.hexagonal.application.service.FaturarPedidoService;
import br.com.hexagonal.domain.exception.FaturaInvalidaException;
import br.com.hexagonal.domain.model.Fatura;
import br.com.hexagonal.domain.port.in.FaturarPedidoUseCase.FaturarPedidoCommand;
import br.com.hexagonal.domain.port.out.FaturaRepositorioPort;

class FaturarPedidoServiceTest {

	@Test
	void deveCriarPersistirFaturaComBoletoNotaFiscalEEmail() {
		Map<UUID, Fatura> bancoEmMemoria = new LinkedHashMap<>();
		FaturaRepositorioPort repositorioFake = fatura -> {
			bancoEmMemoria.put(fatura.getId(), fatura);
			return fatura;
		};

		FaturarPedidoService service = new FaturarPedidoService(
			repositorioFake,
			new BoletoFilaSimuladaAdapter(),
			new NotaFiscalSoapSimuladaAdapter(),
			new EmailSmtpSimuladoAdapter()
		);

		UUID idGerado = service.executar(new FaturarPedidoCommand(42L, new BigDecimal("399.90")));

		assertNotNull(idGerado);
		assertEquals(1, bancoEmMemoria.size());

		Fatura faturaSalva = bancoEmMemoria.get(idGerado);
		assertNotNull(faturaSalva);
		assertEquals(42L, faturaSalva.getPedidoId());
		assertNotNull(faturaSalva.getCodigoBarrasBoleto());
		assertEquals(47, faturaSalva.getCodigoBarrasBoleto().length());
		assertNotNull(faturaSalva.getChaveAcessoNfe());
		assertTrue(faturaSalva.getChaveAcessoNfe().matches("\\d{44}"));
		assertNotNull(faturaSalva.getHashNotificacaoEmail());
		assertEquals(64, faturaSalva.getHashNotificacaoEmail().length());
	}

	@Test
	void deveValidarRegrasDeDominioDaFatura() {
		assertThrows(FaturaInvalidaException.class, () -> Fatura.novaParaPedido(0L, new BigDecimal("10.00")));
		assertThrows(FaturaInvalidaException.class, () -> Fatura.novaParaPedido(1L, BigDecimal.ZERO));

		Fatura fatura = Fatura.novaParaPedido(1L, new BigDecimal("50.00"));
		assertThrows(FaturaInvalidaException.class, () -> fatura.registrarNotaFiscal("123"));
		assertThrows(FaturaInvalidaException.class, () -> fatura.registrarBoleto(" "));
	}
}
