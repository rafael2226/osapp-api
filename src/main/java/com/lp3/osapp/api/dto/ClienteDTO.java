package com.lp3.osapp.api.dto;

import com.lp3.osapp.model.entity.Cliente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {


    private Long id;


    private PerfilDTO perfil;


    public static ClienteDTO create(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());


        if (cliente.getPerfil() != null) {
            dto.setPerfil(PerfilDTO.create(cliente.getPerfil()));
        }

        return dto;
    }
}