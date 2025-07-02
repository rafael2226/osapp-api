package com.lp3.osapp.api.controller;

import com.lp3.osapp.api.dto.EnderecoDTO;
import com.lp3.osapp.exception.RegraNegocioException;
import com.lp3.osapp.model.entity.Endereco;
import com.lp3.osapp.service.EnderecoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/enderecos")
@RequiredArgsConstructor
public class EnderecoController {

    private final EnderecoService service;

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<Endereco> endereco = service.getEnderecoById(id);
        if (endereco.isEmpty()) {
            return new ResponseEntity(
                    Map.of("erro", "Endereço não encontrado"),
                    HttpStatus.NOT_FOUND
            );
        }
        return ResponseEntity.ok(EnderecoDTO.create(endereco.get()));
    }

    @PostMapping
    public ResponseEntity post(@Valid @RequestBody EnderecoDTO dto) {
        try {
            Endereco entidade = service.salvar(dto);
            return new ResponseEntity(EnderecoDTO.create(entidade), HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @Valid @RequestBody EnderecoDTO dto) {
        try {
            Endereco entidade = service.atualizar(id, dto);
            return ResponseEntity.ok(EnderecoDTO.create(entidade));
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Endereco> endereco = service.getEnderecoById(id);
        if (endereco.isEmpty()) {
            return new ResponseEntity(
                    Map.of("erro", "Endereço não encontrado"),
                    HttpStatus.NOT_FOUND
            );
        }
        try {
            service.excluir(endereco.get());

            return ResponseEntity.ok(Map.of("mensagem", "Endereço com ID " + id + " foi apagado com sucesso."));
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }
}