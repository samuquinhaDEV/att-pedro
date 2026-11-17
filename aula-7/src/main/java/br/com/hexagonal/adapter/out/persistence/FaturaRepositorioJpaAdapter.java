package br.com.hexagonal.adapter.out.persistence;

import org.springframework.stereotype.Repository;

import br.com.hexagonal.domain.model.Fatura;
import br.com.hexagonal.domain.port.out.FaturaRepositorioPort;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FaturaRepositorioJpaAdapter implements FaturaRepositorioPort {

	private final SpringDataFaturaRepository jpaRepository;

	@Override
	public Fatura salvar(Fatura fatura) {
		FaturaJpaEntity salvo = jpaRepository.save(FaturaJpaEntity.deDominio(fatura));
		return salvo.paraDominio();
	}
}
