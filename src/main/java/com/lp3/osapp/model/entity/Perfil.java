package com.lp3.osapp.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "perfis")
@Data
@NoArgsConstructor
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 14)
    private String cpf;
    @Column(length = 100)
    private String nome;
    @Column(length = 150)
    private String sobrenome;
    private LocalDate dataNascimento;
    @Column(length = 20)
    private String telefone;
    @Column(length = 20)
    private String whatsapp;


    @OneToOne(mappedBy = "perfil")
    @JsonIgnore
    private Usuario usuario;


    @OneToOne(mappedBy = "perfil", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Cliente cliente;


    @OneToOne(mappedBy = "perfil", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Funcionario funcionario;

    @OneToMany(mappedBy = "perfil", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Endereco> enderecos = new ArrayList<>();
}