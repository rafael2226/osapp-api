package com.lp3.osapp.service;

import com.lp3.osapp.exception.RegraNegocioException;
import com.lp3.osapp.model.entity.Funcionario;
import com.lp3.osapp.model.entity.Perfil;
import com.lp3.osapp.model.repository.FuncionarioRepository;
import com.lp3.osapp.model.repository.PerfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FuncionarioService {

    private final FuncionarioRepository repository;
    private final PerfilRepository perfilRepository;

    @Transactional(readOnly = true)
    public List<Funcionario> getFuncionarios() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Funcionario> getFuncionarioById(Long id) {
        return repository.findById(id);
    }


    @Transactional
    public Funcionario salvar(Funcionario funcionario) {
        if (funcionario.getPerfil() == null) {
            throw new RegraNegocioException("O perfil do funcionário não pode ser nulo.");
        }


        final String cpf = funcionario.getPerfil().getCpf();
        if (cpf != null && !cpf.isEmpty()) {
            Optional<Perfil> perfilExistente = perfilRepository.findByCpf(cpf);
            if (perfilExistente.isPresent() && !perfilExistente.get().getId().equals(funcionario.getPerfil().getId())) {
                throw new RegraNegocioException("O CPF informado já está cadastrado.");
            }
        } else {
            throw new RegraNegocioException("O campo CPF é obrigatório para o perfil do funcionário.");
        }


        if (funcionario.getPerfil().getEnderecos() != null) {
            funcionario.getPerfil().getEnderecos().forEach(endereco -> {
                endereco.setPerfil(funcionario.getPerfil());
            });
        }

        return repository.save(funcionario);
    }

    @Transactional
    public void excluir(Funcionario funcionario) {
        if (funcionario == null || funcionario.getId() == null) {
            throw new NullPointerException("Funcionário a ser excluído não pode ser nulo.");
        }

        repository.delete(funcionario);
    }
}