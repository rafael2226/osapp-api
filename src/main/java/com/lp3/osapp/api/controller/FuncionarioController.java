package com.lp3.osapp.api.controller;

import com.lp3.osapp.api.dto.FuncionarioDTO;
import com.lp3.osapp.exception.RegraNegocioException;
import com.lp3.osapp.model.entity.Funcionario;
import com.lp3.osapp.model.entity.Perfil;
import com.lp3.osapp.service.FuncionarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/funcionarios")
@RequiredArgsConstructor
public class FuncionarioController {

    private final FuncionarioService service;

    @GetMapping
    public ResponseEntity get() {
        List<Funcionario> funcionarios = service.getFuncionarios();
        return ResponseEntity.ok(funcionarios.stream().map(FuncionarioDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<Funcionario> funcionario = service.getFuncionarioById(id);
        if (funcionario.isEmpty()) {
            return new ResponseEntity("Funcionário não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(funcionario.map(FuncionarioDTO::create));
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @Valid @RequestBody FuncionarioDTO dto) {
        Optional<Funcionario> funcionarioOptional = service.getFuncionarioById(id);
        if (funcionarioOptional.isEmpty()) {
            return new ResponseEntity("Perfil de Funcionário não encontrado.", HttpStatus.NOT_FOUND);
        }
        try {
            Funcionario funcionarioExistente = funcionarioOptional.get();
            Perfil perfilParaAtualizar = funcionarioExistente.getPerfil();


            Perfil novosDadosDoPerfil = dto.getPerfil().toEntity();


            perfilParaAtualizar.setNome(novosDadosDoPerfil.getNome());
            perfilParaAtualizar.setSobrenome(novosDadosDoPerfil.getSobrenome());
            perfilParaAtualizar.setCpf(novosDadosDoPerfil.getCpf());


            perfilParaAtualizar.setDataNascimento(novosDadosDoPerfil.getDataNascimento());
            perfilParaAtualizar.setTelefone(novosDadosDoPerfil.getTelefone());
            perfilParaAtualizar.setWhatsapp(novosDadosDoPerfil.getWhatsapp());


            Funcionario funcionarioSalvo = service.salvar(funcionarioExistente);
            return ResponseEntity.ok(FuncionarioDTO.create(funcionarioSalvo));
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Funcionario> funcionario = service.getFuncionarioById(id);
        if (funcionario.isEmpty()) {
            return new ResponseEntity("Funcionário não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(funcionario.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}