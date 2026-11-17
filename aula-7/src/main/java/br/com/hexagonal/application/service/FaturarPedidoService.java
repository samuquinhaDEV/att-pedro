package br.com.hexagonal.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.hexagonal.domain.exception.FaturaInvalidaException;
import br.com.hexagonal.domain.model.Fatura;
import br.com.hexagonal.domain.port.in.FaturarPedidoUseCase;
import br.com.hexagonal.domain.port.out.EmissorBoletoFilaPort;
import br.com.hexagonal.domain.port.out.EmissorNotaFiscalSoapPort;
import br.com.hexagonal.domain.port.out.FaturaRepositorioPort;
import br.com.hexagonal.domain.port.out.NotificadorEmailSmtpPort;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FaturarPedidoService implements FaturarPedidoUseCase {

	private final FaturaRepositorioPort faturaRepositorio;
	private final EmissorBoletoFilaPort emissorBoleto;
	private final EmissorNotaFiscalSoapPort emissorNotaFiscal;
	private final NotificadorEmailSmtpPort notificadorEmail;

	@Override
	@Transactional
	public UUID executar(FaturarPedidoCommand comando) {
		if (comando == null) {
			throw new FaturaInvalidaException("Os dados do pedido são obrigatórios para faturamento.");
		}

		Fatura fatura = Fatura.novaParaPedido(comando.pedidoId(), comando.valor());
		faturaRepositorio.salvar(fatura);

		String codigoBarras = emissorBoleto.publicarSolicitacaoBoleto(fatura);
		fatura.registrarBoleto(codigoBarras);

		String chaveAcesso = emissorNotaFiscal.solicitarEmissaoNfe(fatura);
		fatura.registrarNotaFiscal(chaveAcesso);

		String hashEmail = notificadorEmail.enviarConfirmacaoFatura(fatura);
		fatura.registrarNotificacaoEmail(hashEmail);

		faturaRepositorio.salvar(fatura);
		return fatura.getId();
	}
}
