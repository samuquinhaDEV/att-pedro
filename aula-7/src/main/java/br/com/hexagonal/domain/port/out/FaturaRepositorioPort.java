package br.com.hexagonal.domain.port.out;

import br.com.hexagonal.domain.model.Fatura;

public interface FaturaRepositorioPort {
	Fatura salvar(Fatura fatura);
}
