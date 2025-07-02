package com.lp3.osapp.model.entity;

import com.lp3.osapp.model.enums.StatusOrdemServico;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordens_servico")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOrdemServico status;

    @Column(nullable = false)
    private LocalDateTime dataAbertura;

    private LocalDateTime prazoConclusao;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id")
    private Funcionario funcionario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faturamento_id")
    private Faturamento faturamento;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_servico_id")
    private Endereco enderecoDeServico;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItensOrdemServicos> itens = new ArrayList<>();
}