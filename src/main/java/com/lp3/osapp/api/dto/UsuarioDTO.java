package com.lp3.osapp.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lp3.osapp.model.entity.Usuario;
import com.lp3.osapp.model.enums.GrupoPermissao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private Long id;

    @NotBlank(message = "O campo e-mail é obrigatório.")
    @Email(message = "O formato do e-mail é inválido.")
    private String email;

    @NotNull(message = "O grupo de permissão é obrigatório.")
    private GrupoPermissao grupoPermissao;

    private LocalDateTime dataRegistro;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;


    @Valid
    private PerfilDTO perfil;


    public static UsuarioDTO create(Usuario usuario) {

        ModelMapper modelMapper = new ModelMapper();
        UsuarioDTO dto = modelMapper.map(usuario, UsuarioDTO.class);


        if (usuario.getPerfil() != null) {
            dto.setPerfil(PerfilDTO.create(usuario.getPerfil()));
        }

        return dto;
    }
}