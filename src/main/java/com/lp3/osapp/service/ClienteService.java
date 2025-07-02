package com.lp3.osapp.service;

import com.lp3.osapp.exception.RegraNegocioException;
import com.lp3.osapp.model.entity.Cliente;
import com.lp3.osapp.model.entity.Perfil;
import com.lp3.osapp.model.repository.ClienteRepository;
import com.lp3.osapp.model.repository.PerfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;
    private final PerfilRepository perfilRepository;

    @Transactional(readOnly = true)
    public List<Cliente> getClientes() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> getClienteById(Long id) {
        return repository.findById(id);
    }


    @Transactional
    public Cliente salvar(Cliente cliente) {
        if (cliente.getPerfil() == null) {
            throw new RegraNegocioException("O perfil do cliente não pode ser nulo.");
        }


        final String cpf = cliente.getPerfil().getCpf();
        if (cpf != null && !cpf.isEmpty()) {
            Optional<Perfil> perfilExistente = perfilRepository.findByCpf(cpf);
            if (perfilExistente.isPresent() && !perfilExistente.get().getId().equals(cliente.getPerfil().getId())) {
                throw new RegraNegocioException("O CPF informado já está cadastrado.");
            }
        } else {
            throw new RegraNegocioException("O campo CPF é obrigatório para o perfil do cliente.");
        }


        if (cliente.getPerfil().getEnderecos() != null) {
            cliente.getPerfil().getEnderecos().forEach(endereco -> {
                endereco.setPerfil(cliente.getPerfil());
            });
        }

        return repository.save(cliente);
    }

    @Transactional
    public void excluir(Cliente cliente) {
        if (cliente == null || cliente.getId() == null) {
            throw new NullPointerException("Cliente a ser excluído não pode ser nulo.");
        }


        repository.delete(cliente);
    }
}