package com.lp3.osapp.api.dto;

import com.lp3.osapp.model.entity.ItensOrdemServicos;
import com.lp3.osapp.model.enums.Prioridade;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ItemOrdemServicoDTO {


    private Long id;
    private ServicoDTO servico;
    private BigDecimal valorUnitarioCobrado;
    private BigDecimal custoEfetivo;
    private BigDecimal descontoAplicado;
    private BigDecimal valorTotalItem;


    private Long servicoId;
    private Integer quantidade;
    private BigDecimal duracaoEstimada;
    private Prioridade prioridade;


    public static ItemOrdemServicoDTO create(ItensOrdemServicos item) {
        ModelMapper modelMapper = new ModelMapper();
        ItemOrdemServicoDTO dto = modelMapper.map(item, ItemOrdemServicoDTO.class);

        if (item.getServico() != null) {
            dto.setServico(ServicoDTO.create(item.getServico()));
            dto.setServicoId(item.getServico().getId());
        }
        return dto;
    }
}