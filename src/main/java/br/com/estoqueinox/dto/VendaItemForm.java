package br.com.estoqueinox.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class VendaItemForm {

    @NotNull(message = "Selecione um produto.")
    private Long produtoId;

    @NotNull(message = "Informe a quantidade.")
    @Positive(message = "A quantidade deve ser maior que zero.")
    private Integer quantidade;

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
}
