package com.lp3.osapp.api.dto;

import com.lp3.osapp.model.entity.Faturamento;
import com.lp3.osapp.model.entity.OrdemServico;
import com.lp3.osapp.model.enums.FormaPagamento;
import com.lp3.osapp.model.enums.StatusFatura;
import com.lp3.osapp.model.enums.StatusPagamento;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaturamentoDTO {


    private Long id;
    private BigDecimal valorFatura;
    private LocalDateTime dataRegistro;
    private StatusPagamento statusPagamento;
    private StatusFatura statusFatura;
    private ClienteDTO cliente;
    private List<FaturamentoOrdemServicoDTO> ordensDeServico;


    @NotNull(message = "O ID do Cliente é obrigatório.")
    private Long clienteId;
    @NotEmpty(message = "É necessário fornecer pelo menos uma Ordem de Serviço.")
    private List<Long> ordemServicoIds;


    @NotNull(message = "A data de vencimento é obrigatória.")
    @Future(message = "A data de vencimento deve ser uma data futura.")
    private LocalDate dataVencimento;
    @NotNull(message = "A forma de pagamento é obrigatória.")
    private FormaPagamento formaPagamento;
    private String descricaoFatura;


    private BigDecimal valorPago;
    private LocalDate dataPagamento;



    public static FaturamentoDTO create(Faturamento faturamento) {
        ModelMapper modelMapper = new ModelMapper();
        FaturamentoDTO dto = new FaturamentoDTO();
        dto.setId(faturamento.getId());
        dto.setValorFatura(faturamento.getValorFatura());
        dto.setDataRegistro(faturamento.getDataRegistro());
        if (faturamento.getDataVencimento() != null) {
            dto.setDataVencimento(faturamento.getDataVencimento().toLocalDate());
        }
        dto.setFormaPagamento(faturamento.getFormaPagamento());
        dto.setStatusPagamento(faturamento.getStatusPagamento());
        dto.setStatusFatura(faturamento.getStatusFatura());
        dto.setDescricaoFatura(faturamento.getDescricaoFatura());
        if (faturamento.getCliente() != null) {
            dto.setCliente(ClienteDTO.create(faturamento.getCliente()));
        }
        if (faturamento.getOrdensDeServico() != null) {
            dto.setOrdensDeServico(faturamento.getOrdensDeServico().stream()
                    .map(FaturamentoOrdemServicoDTO::fromEntity)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FaturamentoOrdemServicoDTO {
        private Long id;
        private LocalDateTime dataAbertura;
        private BigDecimal valorTotal;

        public static FaturamentoOrdemServicoDTO fromEntity(OrdemServico os) {
            ModelMapper modelMapper = new ModelMapper();
            return modelMapper.map(os, FaturamentoOrdemServicoDTO.class);
        }
    }
}