package com.lp3.osapp.api.dto;

import com.lp3.osapp.model.entity.Endereco;
import com.lp3.osapp.model.entity.Perfil;
import com.lp3.osapp.model.enums.TipoEndereco;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoDTO {


    private Long id;

    private String nomeDonoPerfil;


    @NotNull(message = "O tipo de endereço é obrigatório.")
    private TipoEndereco tipoEndereco;

    private boolean enderecoPadrao;

    @NotBlank(message = "O logradouro é obrigatório.")
    private String logradouro;

    private String numero;
    private String complemento;

    @NotBlank(message = "O bairro é obrigatório.")
    private String bairro;

    @NotBlank(message = "A cidade é obrigatória.")
    private String cidade;

    @NotBlank(message = "O estado é obrigatório.")
    private String estado;

    @NotBlank(message = "O país é obrigatório.")
    private String pais;

    @NotBlank(message = "O CEP é obrigatório.")
    private String cep;

    private String apelidoEndereco;


    @NotNull(message = "O ID do perfil é obrigatório para criar um endereço.")
    private Long perfilId;


    public static EnderecoDTO create(Endereco endereco) {
        EnderecoDTO dto = new EnderecoDTO();
        dto.setId(endereco.getId());
        dto.setTipoEndereco(endereco.getTipoEndereco());
        dto.setEnderecoPadrao(endereco.isEnderecoPadrao());
        dto.setLogradouro(endereco.getLogradouro());
        dto.setNumero(endereco.getNumero());
        dto.setComplemento(endereco.getComplemento());
        dto.setBairro(endereco.getBairro());
        dto.setCidade(endereco.getCidade());
        dto.setEstado(endereco.getEstado());
        dto.setPais(endereco.getPais());
        dto.setCep(endereco.getCep());
        dto.setApelidoEndereco(endereco.getApelidoEndereco());


        if (endereco.getPerfil() != null) {
            Perfil perfil = endereco.getPerfil();
            dto.setPerfilId(perfil.getId());
            String nome = perfil.getNome() != null ? perfil.getNome() : "";
            String sobrenome = perfil.getSobrenome() != null ? perfil.getSobrenome() : "";
            dto.setNomeDonoPerfil((nome + " " + sobrenome).trim());
        }

        return dto;
    }
}