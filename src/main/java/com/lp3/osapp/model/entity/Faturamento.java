package com.lp3.osapp.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lp3.osapp.model.enums.FormaPagamento;
import com.lp3.osapp.model.enums.StatusFatura;
import com.lp3.osapp.model.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "faturamentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Faturamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorFatura;

    private LocalDateTime dataRegistro;

    @Enumerated(EnumType.STRING)
    private FormaPagamento formaPagamento;

    @Enumerated(EnumType.STRING)
    private StatusPagamento statusPagamento;

    private LocalDateTime dataVencimento;

    @Enumerated(EnumType.STRING)
    private StatusFatura statusFatura;

    @Column(columnDefinition = "TEXT")
    private String descricaoFatura;

    private LocalDateTime dataAtualizacao;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnore
    private Cliente cliente;

    @OneToMany(mappedBy = "faturamento")
    private List<OrdemServico> ordensDeServico = new ArrayList<>();
}