package com.lp3.osapp.api.dto;

import com.lp3.osapp.model.entity.Cliente;
import com.lp3.osapp.model.entity.Funcionario;
import com.lp3.osapp.model.entity.OrdemServico;
import com.lp3.osapp.model.enums.StatusOrdemServico;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class OrdemServicoDTO {


    private Long id;
    private StatusOrdemServico status;
    private LocalDateTime dataAbertura;
    private BigDecimal valorTotal;
    private ClienteOSDTO cliente;
    private FuncionarioOSDTO funcionario;
    private EnderecoDTO enderecoDeServico;
    private List<ItemOrdemServicoDTO> itens;


    private Long clienteId;
    private Long funcionarioId;
    private Long enderecoDeServicoId;


    private String observacoes;
    private LocalDateTime prazoConclusao;


    public static OrdemServicoDTO create(OrdemServico os) {
        ModelMapper modelMapper = new ModelMapper();
        OrdemServicoDTO dto = modelMapper.map(os, OrdemServicoDTO.class);

        dto.setCliente(ClienteOSDTO.fromEntity(os.getCliente()));
        dto.setFuncionario(FuncionarioOSDTO.fromEntity(os.getFuncionario()));

        if (os.getEnderecoDeServico() != null) {
            dto.setEnderecoDeServico(EnderecoDTO.create(os.getEnderecoDeServico()));
        }

        if (os.getItens() != null) {
            dto.setItens(os.getItens().stream()
                    .map(ItemOrdemServicoDTO::create)
                    .collect(Collectors.toList()));
        }
        return dto;
    }



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClienteOSDTO {
        private Long id;
        private String nome;
        private String cpf;

        public static ClienteOSDTO fromEntity(Cliente cliente) {
            if (cliente == null || cliente.getPerfil() == null) return null;
            return new ClienteOSDTO(cliente.getId(), cliente.getPerfil().getNome(), cliente.getPerfil().getCpf());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FuncionarioOSDTO {
        private Long id;
        private String nome;
        private String cpf;

        public static FuncionarioOSDTO fromEntity(Funcionario funcionario) {
            if (funcionario == null || funcionario.getPerfil() == null) return null;
            return new FuncionarioOSDTO(funcionario.getId(), funcionario.getPerfil().getNome(), funcionario.getPerfil().getCpf());
        }
    }
}