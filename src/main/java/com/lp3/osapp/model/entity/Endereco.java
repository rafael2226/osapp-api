package com.lp3.osapp.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lp3.osapp.model.enums.TipoEndereco;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "enderecos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoEndereco tipoEndereco;

    private boolean enderecoPadrao;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String pais;
    private String cep;
    private String apelidoEndereco;




    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_id", nullable = false)
    @JsonIgnore
    private Perfil perfil;
}