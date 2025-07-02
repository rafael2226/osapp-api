package com.lp3.osapp.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lp3.osapp.model.enums.Prioridade;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_ordem_servicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItensOrdemServicos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantidade;

    private BigDecimal duracaoEstimada;

    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorUnitarioCobrado;

    @Column(precision = 10, scale = 2)
    private BigDecimal custoEfetivo;

    @Column(precision = 10, scale = 2)
    private BigDecimal descontoAplicado;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorTotalItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    @JsonIgnore
    private OrdemServico ordemServico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;
}