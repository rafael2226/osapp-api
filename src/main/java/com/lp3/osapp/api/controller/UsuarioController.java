package com.lp3.osapp.api.controller;

import com.lp3.osapp.api.dto.UsuarioDTO;
import com.lp3.osapp.exception.RegraNegocioException;
import com.lp3.osapp.model.entity.Usuario;
import com.lp3.osapp.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping
    public ResponseEntity get() {
        List<Usuario> usuarios = service.getUsuarios();
        return ResponseEntity.ok(usuarios.stream().map(UsuarioDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<Usuario> usuario = service.getUsuarioById(id);
        if (usuario.isEmpty()) {
            return new ResponseEntity(Map.of("erro", "Usuário não encontrado."), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(usuario.map(UsuarioDTO::create));
    }

    @PostMapping()
    public ResponseEntity post(@Valid @RequestBody UsuarioDTO dto) {
        try {
            Usuario usuarioSalvo = service.salvar(dto);
            return new ResponseEntity(UsuarioDTO.create(usuarioSalvo), HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }


    @PutMapping("{id}")
    public ResponseEntity put(@PathVariable("id") Long id, @Valid @RequestBody UsuarioDTO dto) {
        try {
            dto.setId(id);
            Usuario usuarioAtualizado = service.salvar(dto);
            return ResponseEntity.ok(UsuarioDTO.create(usuarioAtualizado));
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Usuario> usuario = service.getUsuarioById(id);
        if (usuario.isEmpty()) {
            return new ResponseEntity(Map.of("erro", "Usuário não encontrado."), HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(usuario.get());
            return ResponseEntity.ok(Map.of("mensagem", "Usuário com ID " + id + " foi excluído com sucesso."));
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }
}