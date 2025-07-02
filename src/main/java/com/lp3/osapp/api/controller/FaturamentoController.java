package com.lp3.osapp.api.controller;

import com.lp3.osapp.api.dto.FaturamentoDTO;
import com.lp3.osapp.exception.RegraNegocioException;
import com.lp3.osapp.model.entity.Faturamento;
import com.lp3.osapp.model.enums.StatusFatura;
import com.lp3.osapp.model.enums.StatusPagamento;
import com.lp3.osapp.model.repository.specification.FaturamentoSpecification;
import com.lp3.osapp.service.FaturamentoService;
import jakarta.validation.Valid;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/faturamentos")
@RequiredArgsConstructor
public class FaturamentoController {

    private final FaturamentoService service;

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<Faturamento> faturamento = service.getFaturamentoById(id);
        if (faturamento.isEmpty()) {
            return new ResponseEntity(Map.of("erro", "Faturamento não encontrado."), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(faturamento.map(FaturamentoDTO::create));
    }

    @GetMapping
    public ResponseEntity get(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) StatusFatura statusFatura,
            @RequestParam(required = false) StatusPagamento statusPagamento,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataVencimentoInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataVencimentoFim,
            Pageable pageable
    ) {
        Specification<Faturamento> spec = null;
        if (clienteId != null) spec = combinarComAnd(spec, FaturamentoSpecification.porClienteId(clienteId));
        if (statusFatura != null) spec = combinarComAnd(spec, FaturamentoSpecification.porStatusFatura(statusFatura));
        if (statusPagamento != null) spec = combinarComAnd(spec, FaturamentoSpecification.porStatusPagamento(statusPagamento));
        if (dataVencimentoInicio != null && dataVencimentoFim != null) {
            spec = combinarComAnd(spec, FaturamentoSpecification.porPeriodoVencimento(dataVencimentoInicio, dataVencimentoFim));
        }

        Page<Faturamento> faturamentoPage = service.buscar(spec, pageable);
        List<FaturamentoDTO> dtos = faturamentoPage.getContent().stream()
                .map(FaturamentoDTO::create)
                .collect(Collectors.toList());
        Page<FaturamentoDTO> dtoPage = new PageImpl<>(dtos, pageable, faturamentoPage.getTotalElements());
        return ResponseEntity.ok(dtoPage);
    }

    @PostMapping
    public ResponseEntity post(@Valid @RequestBody FaturamentoDTO dto) {
        try {
            Faturamento faturamento = service.salvar(dto);
            return new ResponseEntity(FaturamentoDTO.create(faturamento), HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/{id}/registrar-pagamento")
    public ResponseEntity registrarPagamento(@PathVariable Long id, @RequestBody FaturamentoDTO dto) {
        try {
            Faturamento faturamentoPago = service.registrarPagamento(id, dto);
            return ResponseEntity.ok(FaturamentoDTO.create(faturamentoPago));
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity cancelar(@PathVariable Long id) {
        try {
            Faturamento faturamentoCancelado = service.cancelar(id);
            return ResponseEntity.ok(FaturamentoDTO.create(faturamentoCancelado));
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    private Specification<Faturamento> combinarComAnd(Specification<Faturamento> spec, Specification<Faturamento> novaSpec) {
        return spec == null ? novaSpec : spec.and(novaSpec);
    }
}