package com.lp3.osapp.api.dto;

import com.lp3.osapp.model.entity.Perfil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilDTO {

    private Long id;

    @NotBlank(message = "O campo CPF é obrigatório.")

    private String cpf;

    @NotBlank(message = "O campo Nome é obrigatório.")
    private String nome;

    private String sobrenome;

    @NotNull(message = "A data de nascimento é obrigatória.")
    @Past(message = "A data de nascimento deve ser uma data no passado.")
    private LocalDate dataNascimento;

    private String telefone;

    private String whatsapp;


    @Valid
    private List<EnderecoDTO> enderecos;

    public static PerfilDTO create(Perfil perfil) {
        ModelMapper modelMapper = new ModelMapper();
        PerfilDTO dto = modelMapper.map(perfil, PerfilDTO.class);

        if (perfil.getEnderecos() != null) {
            dto.enderecos = perfil.getEnderecos().stream()
                    .map(EnderecoDTO::create)
                    .collect(Collectors.toList());
        }

        return dto;
    }

    public Perfil toEntity() {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(this, Perfil.class);
    }
}