package com.lp3.osapp.api.controller;

import com.lp3.osapp.api.dto.ServicoDTO;
import com.lp3.osapp.exception.RegraNegocioException;
import com.lp3.osapp.model.entity.Servico;
import com.lp3.osapp.service.ServicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/servicos")
@RequiredArgsConstructor
public class ServicoController {

    private final ServicoService service;

    @GetMapping
    public ResponseEntity get() {
        List<Servico> servicos = service.getServicos();
        return ResponseEntity.ok(servicos.stream().map(ServicoDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<Servico> servico = service.getServicoById(id);
        if (servico.isEmpty()) {
            return new ResponseEntity(Map.of("erro", "Serviço não encontrado."), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(servico.map(ServicoDTO::create));
    }

    @PostMapping
    public ResponseEntity post(@Valid @RequestBody ServicoDTO dto) {
        try {
            Servico servico = converter(dto);
            servico = service.salvar(servico);
            return new ResponseEntity(ServicoDTO.create(servico), HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @Valid @RequestBody ServicoDTO dto) {
        if (service.getServicoById(id).isEmpty()) {
            return new ResponseEntity(Map.of("erro", "Serviço não encontrado."), HttpStatus.NOT_FOUND);
        }
        try {
            Servico servico = converter(dto);
            servico.setId(id);
            servico = service.salvar(servico);
            return ResponseEntity.ok(ServicoDTO.create(servico));
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Servico> servico = service.getServicoById(id);
        if (servico.isEmpty()) {
            return new ResponseEntity(Map.of("erro", "Serviço não encontrado."), HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(servico.get());

            return ResponseEntity.ok(Map.of("mensagem", "Serviço com ID " + id + " foi excluído com sucesso."));
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    private Servico converter(ServicoDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Servico.class);
    }
}