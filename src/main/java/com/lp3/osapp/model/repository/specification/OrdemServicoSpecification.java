package com.lp3.osapp.model.repository.specification;

import com.lp3.osapp.model.entity.Cliente;
import com.lp3.osapp.model.entity.Funcionario;
import com.lp3.osapp.model.entity.OrdemServico;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.time.LocalTime;

public class OrdemServicoSpecification {


    public static Specification<OrdemServico> porPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("dataAbertura"), dataInicio.atStartOfDay(), dataFim.atTime(LocalTime.MAX));
    }


    public static Specification<OrdemServico> porClienteId(Long clienteId) {
        return (root, query, criteriaBuilder) -> {
            Join<OrdemServico, Cliente> clienteJoin = root.join("cliente");
            return criteriaBuilder.equal(clienteJoin.get("id"), clienteId);
        };
    }


    public static Specification<OrdemServico> porClienteNome(String nomeCliente) {
        return (root, query, criteriaBuilder) -> {
            Join<OrdemServico, Cliente> clienteJoin = root.join("cliente");
            return criteriaBuilder.like(criteriaBuilder.lower(clienteJoin.get("nome")), "%" + nomeCliente.toLowerCase() + "%");
        };
    }


    public static Specification<OrdemServico> porClienteCpf(String cpf) {
        return (root, query, criteriaBuilder) -> {
            Join<OrdemServico, Cliente> clienteJoin = root.join("cliente");
            return criteriaBuilder.equal(clienteJoin.get("cpf"), cpf);
        };
    }


    public static Specification<OrdemServico> porFuncionarioId(Long funcionarioId) {
        return (root, query, criteriaBuilder) -> {
            Join<OrdemServico, Funcionario> funcionarioJoin = root.join("funcionario");
            return criteriaBuilder.equal(funcionarioJoin.get("id"), funcionarioId);
        };
    }


    public static Specification<OrdemServico> porFuncionarioNome(String nomeFuncionario) {
        return (root, query, criteriaBuilder) -> {
            Join<OrdemServico, Funcionario> funcionarioJoin = root.join("funcionario");
            return criteriaBuilder.like(criteriaBuilder.lower(funcionarioJoin.get("nome")), "%" + nomeFuncionario.toLowerCase() + "%");
        };
    }


    public static Specification<OrdemServico> porFuncionarioCpf(String cpf) {
        return (root, query, criteriaBuilder) -> {
            Join<OrdemServico, Funcionario> funcionarioJoin = root.join("funcionario");
            return criteriaBuilder.equal(funcionarioJoin.get("cpf"), cpf);
        };
    }
}