package br.com.mvc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.mvc.model.LancamentoEstoque;

public interface LancamentoEstoqueRepository extends JpaRepository<LancamentoEstoque, Long> {
	List<LancamentoEstoque> findAllByOrderByRegistradoEmDesc();
}
