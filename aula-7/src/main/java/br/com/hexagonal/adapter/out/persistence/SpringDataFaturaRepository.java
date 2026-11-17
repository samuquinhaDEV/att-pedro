package br.com.hexagonal.adapter.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataFaturaRepository extends JpaRepository<FaturaJpaEntity, UUID> {
}
