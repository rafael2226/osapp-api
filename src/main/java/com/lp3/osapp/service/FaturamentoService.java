package com.lp3.osapp.service;

import com.lp3.osapp.api.dto.FaturamentoDTO;
import com.lp3.osapp.exception.RegraNegocioException;
import com.lp3.osapp.model.entity.Cliente;
import com.lp3.osapp.model.entity.Faturamento;
import com.lp3.osapp.model.entity.OrdemServico;
import com.lp3.osapp.model.enums.StatusFatura;
import com.lp3.osapp.model.enums.StatusOrdemServico;
import com.lp3.osapp.model.enums.StatusPagamento;
import com.lp3.osapp.model.repository.FaturamentoRepository;
import com.lp3.osapp.model.repository.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FaturamentoService {

    private final FaturamentoRepository repository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteService clienteService;

    @Transactional
    public Faturamento salvar(FaturamentoDTO dto) {
        if (dto.getOrdemServicoIds() == null || dto.getOrdemServicoIds().isEmpty()) {
            throw new RegraNegocioException("É necessário fornecer pelo menos uma Ordem de Serviço para faturar.");
        }

        Cliente cliente = clienteService.getClienteById(dto.getClienteId())
                .orElseThrow(() -> new RegraNegocioException("Cliente com ID " + dto.getClienteId() + " não encontrado."));

        List<OrdemServico> ordensDeServico = ordemServicoRepository.findAllById(dto.getOrdemServicoIds());

        validarOrdensParaFaturamento(ordensDeServico, dto.getOrdemServicoIds(), cliente.getId());

        Faturamento faturamento = new Faturamento();
        faturamento.setCliente(cliente);
        if (dto.getDataVencimento() != null) {
            faturamento.setDataVencimento(dto.getDataVencimento().atStartOfDay());
        }
        faturamento.setFormaPagamento(dto.getFormaPagamento());
        faturamento.setDescricaoFatura(dto.getDescricaoFatura());

        BigDecimal valorTotal = ordensDeServico.stream().map(OrdemServico::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        faturamento.setValorFatura(valorTotal);

        faturamento.setDataRegistro(LocalDateTime.now());
        faturamento.setStatusFatura(StatusFatura.ABERTA);
        faturamento.setStatusPagamento(StatusPagamento.PENDENTE);

        Faturamento faturamentoSalvo = repository.save(faturamento);

        ordensDeServico.forEach(os -> os.setFaturamento(faturamentoSalvo));
        ordemServicoRepository.saveAll(ordensDeServico);

        faturamentoSalvo.setOrdensDeServico(ordensDeServico);
        return faturamentoSalvo;
    }

    @Transactional
    public Faturamento registrarPagamento(Long faturamentoId, FaturamentoDTO dto) {
        Faturamento faturamento = getFaturamentoById(faturamentoId)
                .orElseThrow(() -> new RegraNegocioException("Faturamento com ID "+ faturamentoId +" não encontrado."));
        if (faturamento.getStatusPagamento() == StatusPagamento.PAGO || faturamento.getStatusPagamento() == StatusPagamento.CANCELADO) {
            throw new RegraNegocioException("Este faturamento já foi pago ou está cancelado.");
        }
        faturamento.setStatusPagamento(StatusPagamento.PAGO);
        faturamento.setDataAtualizacao(LocalDateTime.now());

        return repository.save(faturamento);
    }

    @Transactional
    public Faturamento cancelar(Long faturamentoId) {
        Faturamento faturamento = getFaturamentoById(faturamentoId)
                .orElseThrow(() -> new RegraNegocioException("Faturamento com ID "+ faturamentoId +" não encontrado."));
        if (faturamento.getStatusPagamento() == StatusPagamento.PAGO) {
            throw new RegraNegocioException("Não é possível cancelar um faturamento que já foi pago.");
        }
        if (faturamento.getStatusFatura() == StatusFatura.CANCELADA) {
            throw new RegraNegocioException("Este faturamento já está cancelado.");
        }
        List<OrdemServico> ordensParaLiberar = faturamento.getOrdensDeServico();
        ordensParaLiberar.forEach(os -> os.setFaturamento(null));
        ordemServicoRepository.saveAll(ordensParaLiberar);
        faturamento.setStatusFatura(StatusFatura.CANCELADA);
        faturamento.setStatusPagamento(StatusPagamento.CANCELADO);
        faturamento.setDataAtualizacao(LocalDateTime.now());
        return repository.save(faturamento);
    }

    @Transactional(readOnly = true)
    public Page<Faturamento> buscar(Specification<Faturamento> spec, Pageable pageable) {
        return repository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Faturamento> getFaturamentoById(Long id) {
        return repository.findById(id);
    }

    private void validarOrdensParaFaturamento(List<OrdemServico> ordens, List<Long> idsOriginais, Long clienteId) {
        if (ordens.size() != idsOriginais.size()) {
            throw new RegraNegocioException("Uma ou mais Ordens de Serviço não foram encontradas.");
        }
        for (OrdemServico os : ordens) {
            if (!os.getCliente().getId().equals(clienteId)) {
                throw new RegraNegocioException("Todas as Ordens de Serviço devem pertencer ao mesmo cliente.");
            }
            if (os.getStatus() != StatusOrdemServico.CONCLUIDA) {
                throw new RegraNegocioException("A Ordem de Serviço de ID " + os.getId() + " não está concluída e não pode ser faturada.");
            }
            if (os.getFaturamento() != null) {
                throw new RegraNegocioException("A Ordem de Serviço de ID " + os.getId() + " já pertence a outro faturamento.");
            }
        }
    }
}