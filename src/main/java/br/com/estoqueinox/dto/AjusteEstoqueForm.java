package br.com.estoqueinox.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class AjusteEstoqueForm {

    @NotNull(message = "Selecione um produto.")
    private Long produtoId;

    @NotNull(message = "Informe a nova quantidade.")
    @PositiveOrZero(message = "A nova quantidade nao pode ser negativa.")
    private Integer novaQuantidade;

    @Size(max = 255, message = "A observacao deve ter no maximo 255 caracteres.")
    private String observacao;

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getNovaQuantidade() {
        return novaQuantidade;
    }

    public void setNovaQuantidade(Integer novaQuantidade) {
        this.novaQuantidade = novaQuantidade;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
