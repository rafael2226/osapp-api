package com.lp3.osapp.service;

import com.lp3.osapp.api.dto.ItemOrdemServicoDTO;
import com.lp3.osapp.exception.RegraNegocioException;
import com.lp3.osapp.model.entity.ItensOrdemServicos;
import com.lp3.osapp.model.entity.OrdemServico;
import com.lp3.osapp.model.entity.Servico;
import com.lp3.osapp.model.enums.Prioridade;
import com.lp3.osapp.model.enums.StatusOrdemServico;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdemServicoService {

    private final OrdemServicoRepository repository;
    private final ServicoService servicoService;

    @Transactional
    public OrdemServico salvar(OrdemServico os) {
        if (os.getItens() == null || os.getItens().isEmpty()) {
            throw new RegraNegocioException("Uma Ordem de Serviço deve ter pelo menos um item.");
        }
        os.getItens().forEach(this::processarValoresDoItem);
        os.setValorTotal(calcularValorTotalDaOS(os));
        if (os.getId() == null) {
            os.setStatus(StatusOrdemServico.ABERTA);
            os.setDataAbertura(LocalDateTime.now());
        }
        return repository.save(os);
    }

    @Transactional
    public OrdemServico adicionarItem(Long osId, ItemOrdemServicoDTO dto) {
        OrdemServico os = getOrdemServicoById(osId).orElseThrow(() -> new RegraNegocioException("Ordem de Serviço com ID " + osId + " não encontrada."));
        if (os.getStatus() == StatusOrdemServico.CONCLUIDA || os.getStatus() == StatusOrdemServico.CANCELADA) {
            throw new RegraNegocioException("Não é possível adicionar itens a uma OS concluída ou cancelada.");
        }
        ItensOrdemServicos novoItem = new ItensOrdemServicos();
        processarValoresDoItem(novoItem, dto);
        novoItem.setOrdemServico(os);
        os.getItens().add(novoItem);
        os.setValorTotal(calcularValorTotalDaOS(os));
        return repository.save(os);
    }

    @Transactional
    public OrdemServico atualizarItem(Long osId, Long itemId, ItemOrdemServicoDTO dto) {
        OrdemServico os = getOrdemServicoById(osId).orElseThrow(() -> new RegraNegocioException("Ordem de Serviço com ID " + osId + " não encontrada."));
        ItensOrdemServicos itemParaAtualizar = os.getItens().stream().filter(item -> item.getId().equals(itemId)).findFirst().orElseThrow(() -> new RegraNegocioException("Item com ID " + itemId + " não encontrado nesta Ordem de Serviço."));

        if (os.getStatus() == StatusOrdemServico.EM_ANDAMENTO) {
            if (dto.getPrioridade() != null) itemParaAtualizar.setPrioridade(dto.getPrioridade());
        } else if (os.getStatus() == StatusOrdemServico.ABERTA) {
            processarValoresDoItem(itemParaAtualizar, dto);
        } else {
            throw new RegraNegocioException("Itens não podem ser atualizados pois a OS está no status " + os.getStatus());
        }
        recalcularValorTotalItem(itemParaAtualizar);
        os.setValorTotal(calcularValorTotalDaOS(os));
        return repository.save(os);
    }

    @Transactional
    public OrdemServico removerItem(Long osId, Long itemId) {
        OrdemServico os = getOrdemServicoById(osId).orElseThrow(() -> new RegraNegocioException("Ordem de Serviço com ID " + osId + " não encontrada."));
        if (os.getStatus() != StatusOrdemServico.ABERTA) {
            throw new RegraNegocioException("Itens só podem ser removidos de Ordens de Serviço com status ABERTA.");
        }
        if (os.getItens().size() <= 1) {
            throw new RegraNegocioException("Não é possível remover o último item de uma Ordem de Serviço.");
        }
        boolean removido = os.getItens().removeIf(item -> item.getId().equals(itemId));
        if (!removido) {
            throw new RegraNegocioException("Item com ID " + itemId + " não encontrado para remoção.");
        }
        os.setValorTotal(calcularValorTotalDaOS(os));
        return repository.save(os);
    }

    @Transactional(readOnly = true)
    public Optional<OrdemServico> getOrdemServicoById(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<OrdemServico> buscar(Specification<OrdemServico> spec, Pageable pageable) {
        return repository.findAll(spec, pageable);
    }

    @Transactional
    public OrdemServico mudarStatus(Long osId, StatusOrdemServico novoStatus) {
        OrdemServico os = getOrdemServicoById(osId).orElseThrow(() -> new RegraNegocioException("Ordem de Serviço não encontrada."));
        if (os.getStatus() == StatusOrdemServico.CONCLUIDA || os.getStatus() == StatusOrdemServico.CANCELADA) {
            throw new RegraNegocioException("Não é possível alterar o status de uma Ordem de Serviço que já foi concluída ou cancelada.");
        }
        os.setStatus(novoStatus);
        return repository.save(os);
    }

    @Transactional
    public void excluir(OrdemServico os) {
        if (os == null || os.getId() == null) {
            throw new RegraNegocioException("Ordem de Serviço a ser excluída não pode ser nula.");
        }
        if (os.getStatus() != StatusOrdemServico.ABERTA) {
            throw new RegraNegocioException("Apenas Ordens de Serviço com status 'ABERTA' podem ser deletadas.");
        }
        repository.delete(os);
    }

    @Transactional
    public OrdemServico clonarOrdemServico(Long osId) {
        OrdemServico osOriginal = getOrdemServicoById(osId).orElseThrow(() -> new RegraNegocioException("Ordem de Serviço original com ID " + osId + " não encontrada para clonagem."));
        OrdemServico osClone = new OrdemServico();
        osClone.setStatus(StatusOrdemServico.ABERTA);
        osClone.setDataAbertura(LocalDateTime.now());
        osClone.setObservacoes("Clone da OS ID: " + osOriginal.getId() + ". " + osOriginal.getObservacoes());
        osClone.setCliente(osOriginal.getCliente());
        osClone.setFuncionario(osOriginal.getFuncionario());
        List<ItensOrdemServicos> itensClonados = osOriginal.getItens().stream().map(itemOriginal -> {
            ItensOrdemServicos itemClone = new ItensOrdemServicos();
            itemClone.setServico(itemOriginal.getServico());
            itemClone.setQuantidade(itemOriginal.getQuantidade());
            itemClone.setDuracaoEstimada(itemOriginal.getDuracaoEstimada());
            itemClone.setPrioridade(itemOriginal.getPrioridade());
            itemClone.setValorUnitarioCobrado(itemOriginal.getValorUnitarioCobrado());
            itemClone.setDescontoAplicado(itemOriginal.getDescontoAplicado());
            itemClone.setCustoEfetivo(itemOriginal.getCustoEfetivo());
            itemClone.setValorTotalItem(itemOriginal.getValorTotalItem());
            itemClone.setOrdemServico(osClone);
            return itemClone;
        }).collect(Collectors.toList());
        osClone.setItens(itensClonados);
        osClone.setValorTotal(calcularValorTotalDaOS(osClone));
        return repository.save(osClone);
    }

    private void processarValoresDoItem(ItensOrdemServicos item) {
        if (item.getServico() == null) {
            throw new RegraNegocioException("Item da OS está sem um serviço associado.");
        }
        item.setValorUnitarioCobrado(item.getServico().getPrecoSugeridoVenda());
        item.setCustoEfetivo(item.getServico().getCustoPadrao());
        item.setDescontoAplicado(BigDecimal.ZERO);
        recalcularValorTotalItem(item);
    }

    private void processarValoresDoItem(ItensOrdemServicos item, ItemOrdemServicoDTO dto) {
        if (dto.getServicoId() == null) {
            throw new RegraNegocioException("O ID do Serviço é obrigatório para adicionar um item.");
        }
        Servico servicoDoCatalogo = servicoService.getServicoById(dto.getServicoId()).orElseThrow(() -> new RegraNegocioException("Serviço com ID " + dto.getServicoId() + " não encontrado."));
        item.setServico(servicoDoCatalogo);
        item.setQuantidade(dto.getQuantidade());
        item.setDuracaoEstimada(dto.getDuracaoEstimada());
        item.setPrioridade(dto.getPrioridade() != null ? dto.getPrioridade() : Prioridade.NORMAL);
        item.setValorUnitarioCobrado(dto.getValorUnitarioCobrado() != null ? dto.getValorUnitarioCobrado() : servicoDoCatalogo.getPrecoSugeridoVenda());
        item.setCustoEfetivo(dto.getCustoEfetivo() != null ? dto.getCustoEfetivo() : servicoDoCatalogo.getCustoPadrao());
        item.setDescontoAplicado(dto.getDescontoAplicado() != null ? dto.getDescontoAplicado() : BigDecimal.ZERO);
    }

    private void recalcularValorTotalItem(ItensOrdemServicos item) {
        if (item.getValorUnitarioCobrado() == null || item.getQuantidade() == null || item.getDescontoAplicado() == null) {
            throw new RegraNegocioException("Dados insuficientes para calcular o valor total do item.");
        }
        BigDecimal valorBruto = item.getValorUnitarioCobrado().multiply(new BigDecimal(item.getQuantidade()));
        item.setValorTotalItem(valorBruto.subtract(item.getDescontoAplicado()));
    }

    private BigDecimal calcularValorTotalDaOS(OrdemServico os) {
        if (os.getItens() == null) return BigDecimal.ZERO;
        return os.getItens().stream().map(ItensOrdemServicos::getValorTotalItem).filter(val -> val != null).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}