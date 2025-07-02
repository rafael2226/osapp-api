package com.lp3.osapp.model.repository;

import com.lp3.osapp.model.entity.ItensOrdemServicos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItensOrdemServicosRepository extends JpaRepository<ItensOrdemServicos, Long> {
}