package br.com.estoqueinox.dto;

import br.com.estoqueinox.model.FormaPagamento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class VendaForm {

    @NotNull(message = "Selecione um produto.")
    private Long produtoId;

    @NotNull(message = "Informe a quantidade.")
    @Positive(message = "A quantidade deve ser maior que zero.")
    private Integer quantidade = 1;

    @NotNull(message = "Selecione a forma de pagamento.")
    private FormaPagamento formaPagamento;

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

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }
}
