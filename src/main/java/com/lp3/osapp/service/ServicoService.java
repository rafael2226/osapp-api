package com.lp3.osapp.service;

import com.lp3.osapp.exception.RegraNegocioException;
import com.lp3.osapp.model.entity.Servico;
import com.lp3.osapp.model.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository repository;

    @Transactional
    public Servico salvar(Servico servico) {
        if (servico.getCustoPadrao() != null && servico.getMargemLucroPadrao() != null) {
            if (servico.getMargemLucroPadrao().compareTo(BigDecimal.ZERO) < 0) {
                throw new RegraNegocioException("A margem de lucro não pode ser negativa.");
            }
            BigDecimal multiplicador = BigDecimal.ONE.add(servico.getMargemLucroPadrao().divide(new BigDecimal("100")));
            BigDecimal precoSugerido = servico.getCustoPadrao().multiply(multiplicador).setScale(2, RoundingMode.HALF_UP);
            servico.setPrecoSugeridoVenda(precoSugerido);

            if (servico.getPrecoMinimoVenda() != null && servico.getPrecoMinimoVenda().compareTo(precoSugerido) > 0) {
                throw new RegraNegocioException("O preço mínimo de venda não pode ser maior que o preço sugerido.");
            }
        }
        return repository.save(servico);
    }



    @Transactional(readOnly = true)
    public Optional<Servico> getServicoById(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Servico> getServicos() {
        return repository.findAll();
    }

    @Transactional
    public void excluir(Servico servico) {
        if (servico == null || servico.getId() == null) {
            throw new NullPointerException("Serviço a ser excluído não pode ser nulo.");
        }

        repository.delete(servico);
    }
}