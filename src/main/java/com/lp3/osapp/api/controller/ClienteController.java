package com.lp3.osapp.api.controller;

import com.lp3.osapp.api.dto.ClienteDTO;
import com.lp3.osapp.exception.RegraNegocioException;
import com.lp3.osapp.model.entity.Cliente;
import com.lp3.osapp.model.entity.Perfil;
import com.lp3.osapp.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService service;

    @GetMapping
    public ResponseEntity get() {
        List<Cliente> clientes = service.getClientes();
        return ResponseEntity.ok(clientes.stream().map(ClienteDTO::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<Cliente> cliente = service.getClienteById(id);
        if (cliente.isEmpty()) {
            return new ResponseEntity("Cliente não encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(cliente.map(ClienteDTO::create));
    }

    @PutMapping("{id}")

    public ResponseEntity atualizar(@PathVariable("id") Long id, @Valid @RequestBody ClienteDTO dto) {
        Optional<Cliente> clienteOptional = service.getClienteById(id);
        if (clienteOptional.isEmpty()) {
            return new ResponseEntity("Perfil de Cliente não encontrado para o ID fornecido.", HttpStatus.NOT_FOUND);
        }

        try {
            Cliente clienteExistente = clienteOptional.get();
            Perfil perfilParaAtualizar = clienteExistente.getPerfil();


            Perfil novosDadosDoPerfil = dto.getPerfil().toEntity();
            perfilParaAtualizar.setNome(novosDadosDoPerfil.getNome());
            perfilParaAtualizar.setSobrenome(novosDadosDoPerfil.getSobrenome());
            perfilParaAtualizar.setCpf(novosDadosDoPerfil.getCpf());
            perfilParaAtualizar.setDataNascimento(novosDadosDoPerfil.getDataNascimento());
            perfilParaAtualizar.setTelefone(novosDadosDoPerfil.getTelefone());
            perfilParaAtualizar.setWhatsapp(novosDadosDoPerfil.getWhatsapp());

            Cliente clienteSalvo = service.salvar(clienteExistente);
            return ResponseEntity.ok(ClienteDTO.create(clienteSalvo));
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<Cliente> cliente = service.getClienteById(id);
        if (cliente.isEmpty()) {
            return new ResponseEntity("Cliente não encontrado", HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(cliente.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}