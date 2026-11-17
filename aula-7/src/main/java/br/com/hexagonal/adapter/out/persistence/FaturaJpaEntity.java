package br.com.hexagonal.adapter.out.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.hexagonal.domain.model.Fatura;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fatura")
public class FaturaJpaEntity {

	@Id
	@Column(name = "id", nullable = false)
	private UUID id;

	@Column(name = "pedido_id", nullable = false)
	private Long pedidoId;

	@Column(name = "valor", nullable = false, precision = 12, scale = 2)
	private BigDecimal valor;

	@Column(name = "data_criacao", nullable = false)
	private LocalDateTime dataCriacao;

	@Column(name = "codigo_barras_boleto", length = 60)
	private String codigoBarrasBoleto;

	@Column(name = "chave_acesso_nfe", length = 44)
	private String chaveAcessoNfe;

	@Column(name = "hash_notificacao_email", length = 64)
	private String hashNotificacaoEmail;

	public static FaturaJpaEntity deDominio(Fatura fatura) {
		return new FaturaJpaEntity(
			fatura.getId(),
			fatura.getPedidoId(),
			fatura.getValor(),
			fatura.getDataCriacao(),
			fatura.getCodigoBarrasBoleto(),
			fatura.getChaveAcessoNfe(),
			fatura.getHashNotificacaoEmail()
		);
	}

	public Fatura paraDominio() {
		return Fatura.reconstruir(
			id,
			pedidoId,
			valor,
			dataCriacao,
			codigoBarrasBoleto,
			chaveAcessoNfe,
			hashNotificacaoEmail
		);
	}
}
