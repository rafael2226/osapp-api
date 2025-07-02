package com.lp3.osapp.api.dto;

import com.lp3.osapp.model.entity.Funcionario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioDTO {


    private Long id;


    private PerfilDTO perfil;


    public static FuncionarioDTO create(Funcionario funcionario) {
        FuncionarioDTO dto = new FuncionarioDTO();
        dto.setId(funcionario.getId());


        if (funcionario.getPerfil() != null) {
            dto.setPerfil(PerfilDTO.create(funcionario.getPerfil()));
        }

        return dto;
    }
}