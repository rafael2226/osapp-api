package com.lp3.osapp.api.controller;

import com.lp3.osapp.api.dto.ItemOrdemServicoDTO;
import com.lp3.osapp.api.dto.OrdemServicoDTO;
import com.lp3.osapp.exception.RegraNegocioException;
import com.lp3.osapp.model.entity.*;
import com.lp3.osapp.model.enums.StatusOrdemServico;
import com.lp3.osapp.model.repository.specification.OrdemServicoSpecification;
import com.lp3.osapp.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/ordens-servico")
@RequiredArgsConstructor
public class OrdemServicoController {

    private final OrdemServicoService service;
    private final ClienteService clienteService;
    private final FuncionarioService funcionarioService;
    private final ServicoService servicoService;
    private final EnderecoService enderecoService;

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<OrdemServico> os = service.getOrdemServicoById(id);
        if (os.isEmpty()) {
            return new ResponseEntity(Map.of("erro", "Ordem de Serviço não encontrada."), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(os.map(OrdemServicoDTO::create));
    }

    @GetMapping
    public ResponseEntity get(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String clienteNome,
            @RequestParam(required = false) String clienteCpf,
            @RequestParam(required = false) Long funcionarioId,
            @RequestParam(required = false) String funcionarioNome,
            @RequestParam(required = false) String funcionarioCpf,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Pageable pageable
    ) {
        Specification<OrdemServico> spec = null;
        if (clienteId != null) spec = combinarComAnd(spec, OrdemServicoSpecification.porClienteId(clienteId));
        if (clienteNome != null) spec = combinarComAnd(spec, OrdemServicoSpecification.porClienteNome(clienteNome));
        if (clienteCpf != null) spec = combinarComAnd(spec, OrdemServicoSpecification.porClienteCpf(clienteCpf));
        if (funcionarioId != null) spec = combinarComAnd(spec, OrdemServicoSpecification.porFuncionarioId(funcionarioId));
        if (funcionarioNome != null) spec = combinarComAnd(spec, OrdemServicoSpecification.porFuncionarioNome(funcionarioNome));
        if (funcionarioCpf != null) spec = combinarComAnd(spec, OrdemServicoSpecification.porFuncionarioCpf(funcionarioCpf));
        if (dataInicio != null && dataFim != null) spec = combinarComAnd(spec, OrdemServicoSpecification.porPeriodo(dataInicio, dataFim));

        Page<OrdemServico> osPage = service.buscar(spec, pageable);
        List<OrdemServicoDTO> dtos = osPage.getContent().stream()
                .map(OrdemServicoDTO::create)
                .collect(Collectors.toList());

        Page<OrdemServicoDTO> dtoPage = new PageImpl<>(dtos, pageable, osPage.getTotalElements());
        return ResponseEntity.ok(dtoPage);
    }

    @PostMapping
    public ResponseEntity post(@Valid @RequestBody OrdemServicoDTO dto) {
        try {
            OrdemServico os = converter(dto);
            os = service.salvar(os);
            return new ResponseEntity(OrdemServicoDTO.create(os), HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity excluir(@PathVariable("id") Long id) {
        Optional<OrdemServico> os = service.getOrdemServicoById(id);
        if (os.isEmpty()) {
            return new ResponseEntity(Map.of("erro", "Ordem de Serviço não encontrada."), HttpStatus.NOT_FOUND);
        }
        try {
            service.excluir(os.get());
            return ResponseEntity.ok(Map.of("mensagem", "Ordem de Serviço com ID " + id + " foi deletada com sucesso."));
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity mudarStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusDTO statusDto) {
        try {
            OrdemServico osAtualizada = service.mudarStatus(id, statusDto.getNovoStatus());
            return ResponseEntity.ok(OrdemServicoDTO.create(osAtualizada));
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/{osId}/clone")
    public ResponseEntity clonarOrdemServico(@PathVariable Long osId) {
        try {
            OrdemServico osClonada = service.clonarOrdemServico(osId);
            return new ResponseEntity(OrdemServicoDTO.create(osClonada), HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/{osId}/itens")
    public ResponseEntity adicionarItem(@PathVariable Long osId, @Valid @RequestBody ItemOrdemServicoDTO dto) {
        try {
            OrdemServico osAtualizada = service.adicionarItem(osId, dto);
            return new ResponseEntity(OrdemServicoDTO.create(osAtualizada), HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/{osId}/itens/{itemId}")
    public ResponseEntity atualizarItem(@PathVariable Long osId, @PathVariable Long itemId, @Valid @RequestBody ItemOrdemServicoDTO dto) {
        try {
            OrdemServico osAtualizada = service.atualizarItem(osId, itemId, dto);
            return ResponseEntity.ok(OrdemServicoDTO.create(osAtualizada));
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @DeleteMapping("/{osId}/itens/{itemId}")
    public ResponseEntity removerItem(@PathVariable Long osId, @PathVariable Long itemId) {
        try {
            service.removerItem(osId, itemId);
            return ResponseEntity.ok(Map.of("mensagem", "Item com ID " + itemId + " foi removido da OS " + osId + " com sucesso."));
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    private OrdemServico converter(OrdemServicoDTO dto) {
        OrdemServico os = new OrdemServico();
        os.setObservacoes(dto.getObservacoes());
        os.setPrazoConclusao(dto.getPrazoConclusao());

        Cliente cliente = clienteService.getClienteById(dto.getClienteId())
                .orElseThrow(() -> new RegraNegocioException("Cliente não encontrado para o ID " + dto.getClienteId()));
        os.setCliente(cliente);

        if (dto.getFuncionarioId() != null) {
            Funcionario funcionario = funcionarioService.getFuncionarioById(dto.getFuncionarioId())
                    .orElseThrow(() -> new RegraNegocioException("Funcionário não encontrado para o ID " + dto.getFuncionarioId()));
            os.setFuncionario(funcionario);
        }

        if (dto.getEnderecoDeServicoId() != null) {
            Endereco endereco = enderecoService.getEnderecoById(dto.getEnderecoDeServicoId())
                    .orElseThrow(() -> new RegraNegocioException("Endereço de Serviço com ID " + dto.getEnderecoDeServicoId() + " não encontrado."));
            os.setEnderecoDeServico(endereco);
        }

        if (dto.getItens() != null) {
            List<ItensOrdemServicos> itens = dto.getItens().stream().map(itemDto -> {
                Servico servico = servicoService.getServicoById(itemDto.getServicoId())
                        .orElseThrow(() -> new RegraNegocioException("Serviço não encontrado para o ID " + itemDto.getServicoId()));
                ItensOrdemServicos item = new ItensOrdemServicos();
                item.setServico(servico);
                item.setQuantidade(itemDto.getQuantidade());
                item.setDuracaoEstimada(itemDto.getDuracaoEstimada());
                item.setPrioridade(itemDto.getPrioridade());
                item.setDescontoAplicado(itemDto.getDescontoAplicado());
                item.setOrdemServico(os);
                return item;
            }).collect(Collectors.toList());
            os.setItens(itens);
        }
        return os;
    }

    private Specification<OrdemServico> combinarComAnd(Specification<OrdemServico> spec, Specification<OrdemServico> novaSpec) {
        return spec == null ? novaSpec : spec.and(novaSpec);
    }

    @Data
    private static class UpdateStatusDTO {
        @NotNull(message = "O novoStatus não pode ser nulo.")
        private StatusOrdemServico novoStatus;
    }
}