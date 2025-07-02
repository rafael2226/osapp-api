package com.lp3.osapp.model.repository.specification;

import com.lp3.osapp.model.entity.Cliente;
import com.lp3.osapp.model.entity.Faturamento;
import com.lp3.osapp.model.entity.OrdemServico;
import com.lp3.osapp.model.enums.StatusFatura;
import com.lp3.osapp.model.enums.StatusPagamento;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;

public class FaturamentoSpecification {


    public static Specification<Faturamento> porStatusFatura(StatusFatura status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("statusFatura"), status);
    }


    public static Specification<Faturamento> porStatusPagamento(StatusPagamento status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("statusPagamento"), status);
    }


    public static Specification<Faturamento> porPeriodoVencimento(LocalDate dataInicio, LocalDate dataFim) {
        return (root, query, criteriaBuilder) ->

                criteriaBuilder.between(root.get("dataVencimento"), dataInicio.atStartOfDay(), dataFim.atTime(LocalTime.MAX));
    }


    public static Specification<Faturamento> porClienteId(Long clienteId) {
        return (root, query, criteriaBuilder) -> {

            query.distinct(true);
            Join<Faturamento, OrdemServico> osJoin = root.join("ordensDeServico");

            Join<OrdemServico, Cliente> clienteJoin = osJoin.join("cliente");

            return criteriaBuilder.equal(clienteJoin.get("id"), clienteId);
        };
    }
}