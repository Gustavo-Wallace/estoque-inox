package br.com.estoqueinox.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class VendaItemForm {

    @NotNull(message = "Selecione um produto.")
    private Long produtoId;

    @NotNull(message = "Informe a quantidade.")
    @Positive(message = "A quantidade deve ser maior que zero.")
    private Integer quantidade;

    @PositiveOrZero(message = "O desconto nao pode ser negativo.")
    private BigDecimal descontoUnitario = BigDecimal.ZERO;

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getDescontoUnitario() {
        return descontoUnitario;
    }

    public void setDescontoUnitario(BigDecimal descontoUnitario) {
        this.descontoUnitario = descontoUnitario;
    }
}
