package com.lp3.osapp.service;

import com.lp3.osapp.api.dto.EnderecoDTO;
import com.lp3.osapp.exception.RegraNegocioException;
import com.lp3.osapp.model.entity.Endereco;
import com.lp3.osapp.model.entity.Perfil;
import com.lp3.osapp.model.repository.EnderecoRepository;
import com.lp3.osapp.model.repository.PerfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnderecoService {

    private final EnderecoRepository repository;
    private final PerfilRepository perfilRepository;

    @Transactional(readOnly = true)
    public Optional<Endereco> getEnderecoById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Endereco salvar(EnderecoDTO dto) {
        if (dto.getPerfilId() == null) {
            throw new RegraNegocioException("O ID do Perfil é obrigatório para criar um endereço.");
        }

        Perfil perfil = perfilRepository.findById(dto.getPerfilId())
                .orElseThrow(() -> new RegraNegocioException("Perfil com ID " + dto.getPerfilId() + " não encontrado."));


        Endereco endereco = new Endereco();
        endereco.setTipoEndereco(dto.getTipoEndereco());
        endereco.setEnderecoPadrao(dto.isEnderecoPadrao());
        endereco.setLogradouro(dto.getLogradouro());
        endereco.setNumero(dto.getNumero());
        endereco.setComplemento(dto.getComplemento());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setEstado(dto.getEstado());
        endereco.setCep(dto.getCep());
        endereco.setApelidoEndereco(dto.getApelidoEndereco());
        endereco.setPais(dto.getPais());

        endereco.setPerfil(perfil);

        return repository.save(endereco);
    }

    @Transactional
    public Endereco atualizar(Long id, EnderecoDTO dto) {
        Endereco enderecoExistente = getEnderecoById(id)
                .orElseThrow(() -> new RegraNegocioException("Endereço com ID " + id + " não encontrado."));


        enderecoExistente.setTipoEndereco(dto.getTipoEndereco());
        enderecoExistente.setEnderecoPadrao(dto.isEnderecoPadrao());
        enderecoExistente.setLogradouro(dto.getLogradouro());
        enderecoExistente.setNumero(dto.getNumero());
        enderecoExistente.setComplemento(dto.getComplemento());
        enderecoExistente.setBairro(dto.getBairro());
        enderecoExistente.setCidade(dto.getCidade());
        enderecoExistente.setEstado(dto.getEstado());
        enderecoExistente.setCep(dto.getCep());
        enderecoExistente.setApelidoEndereco(dto.getApelidoEndereco());
        enderecoExistente.setPais(dto.getPais());

        return repository.save(enderecoExistente);
    }

    @Transactional
    public void excluir(Endereco endereco) {
        if (endereco == null || endereco.getId() == null) {
            throw new RegraNegocioException("Endereço a ser excluído não pode ser nulo.");
        }
        repository.delete(endereco);
    }
}