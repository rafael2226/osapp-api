package com.lp3.osapp.model.entity;

import com.lp3.osapp.model.enums.TipoServico;
import com.lp3.osapp.model.enums.UnidadeMedidaServico;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "servicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nomeServico;

    @Column(columnDefinition = "TEXT")
    private String descricaoServico;

    @Enumerated(EnumType.STRING)
    private TipoServico tipoServico;

    @Enumerated(EnumType.STRING)
    private UnidadeMedidaServico unidadeMedidaServico;

    @Column(precision = 10, scale = 2)
    private BigDecimal custoPadrao;

    @Column(precision = 10, scale = 2)
    private BigDecimal margemLucroPadrao;

    @Column(precision = 10, scale = 2)
    private BigDecimal precoMinimoVenda;

    @Column(precision = 10, scale = 2)
    private BigDecimal precoSugeridoVenda;
}