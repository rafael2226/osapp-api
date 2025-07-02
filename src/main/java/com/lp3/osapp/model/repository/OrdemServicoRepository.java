package com.lp3.osapp.model.repository;

import com.lp3.osapp.model.entity.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long>, JpaSpecificationExecutor<OrdemServico> {
}