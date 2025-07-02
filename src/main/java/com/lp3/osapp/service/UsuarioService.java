package com.lp3.osapp.service;

import com.lp3.osapp.api.dto.UsuarioDTO;
import com.lp3.osapp.exception.RegraNegocioException;
import com.lp3.osapp.model.entity.Cliente;
import com.lp3.osapp.model.entity.Funcionario;
import com.lp3.osapp.model.entity.Perfil;
import com.lp3.osapp.model.entity.Usuario;
import com.lp3.osapp.model.enums.GrupoPermissao;
import com.lp3.osapp.model.repository.ClienteRepository;
import com.lp3.osapp.model.repository.FuncionarioRepository;
import com.lp3.osapp.model.repository.PerfilRepository;
import com.lp3.osapp.model.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final ClienteRepository clienteRepository;
    private final FuncionarioRepository funcionarioRepository;

    @Transactional
    public Usuario salvar(UsuarioDTO dto) {

        if (dto.getId() == null && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RegraNegocioException("O e-mail informado já está em uso.");
        }

        Usuario usuario;

        if (dto.getId() != null) {
            usuario = getUsuarioById(dto.getId()).orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o ID " + dto.getId()));

            usuario.setEmail(dto.getEmail());
            usuario.setGrupoPermissao(dto.getGrupoPermissao());
            if(dto.getSenha() != null && !dto.getSenha().isEmpty()){

                usuario.setSenha(dto.getSenha());
            }
        } else {

            usuario = new Usuario();
            usuario.setEmail(dto.getEmail());
            usuario.setSenha(dto.getSenha());
            usuario.setGrupoPermissao(dto.getGrupoPermissao());
            usuario.setDataRegistro(LocalDateTime.now());

            Perfil perfil = new Perfil();
            perfilRepository.save(perfil);
            usuario.setPerfil(perfil);

            if (usuario.getGrupoPermissao() == GrupoPermissao.CLIENTE) {
                Cliente cliente = new Cliente();
                cliente.setPerfil(perfil);
                clienteRepository.save(cliente);
            } else {
                Funcionario funcionario = new Funcionario();
                funcionario.setPerfil(perfil);
                funcionarioRepository.save(funcionario);
            }
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void excluir(Usuario usuario) {
        if (usuario == null || usuario.getId() == null) {
            throw new RegraNegocioException("Usuário a ser excluído não pode ser nulo.");
        }
        usuarioRepository.delete(usuario);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios() {
        return usuarioRepository.findAll();
    }
}