package com.lp3.osapp.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "funcionarios")
@Data
@NoArgsConstructor
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(name = "perfil_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Perfil perfil;

    @OneToMany(mappedBy = "funcionario", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OrdemServico> ordensDeServicoAtribuidas = new ArrayList<>();
}