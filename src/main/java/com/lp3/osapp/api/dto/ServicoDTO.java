package com.lp3.osapp.api.dto;

import com.lp3.osapp.model.entity.Servico;
import com.lp3.osapp.model.enums.TipoServico;
import com.lp3.osapp.model.enums.UnidadeMedidaServico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicoDTO {

    private Long id;

    @NotBlank(message = "O nome do serviço é obrigatório.")
    private String nomeServico;

    private String descricaoServico;

    @NotNull(message = "O tipo de serviço é obrigatório.")
    private TipoServico tipoServico;

    @NotNull(message = "A unidade de medida é obrigatória.")
    private UnidadeMedidaServico unidadeMedidaServico;

    @NotNull(message = "O custo padrão é obrigatório.")
    @PositiveOrZero(message = "O custo padrão deve ser um valor positivo ou zero.")
    private BigDecimal custoPadrao;

    @NotNull(message = "A margem de lucro padrão é obrigatória.")
    private BigDecimal margemLucroPadrao;

    @NotNull(message = "O preço mínimo de venda é obrigatório.")
    @PositiveOrZero(message = "O preço mínimo de venda deve ser um valor positivo ou zero.")
    private BigDecimal precoMinimoVenda;

    private BigDecimal precoSugeridoVenda;


    public static ServicoDTO create(Servico servico) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(servico, ServicoDTO.class);
    }
}