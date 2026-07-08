package br.com.estoqueinox.dto;

import java.util.ArrayList;
import java.util.List;

import br.com.estoqueinox.model.FormaPagamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class VendaForm {

    @Valid
    @Size(min = 1, message = "Adicione pelo menos um item.")
    private List<VendaItemForm> itens = new ArrayList<>();

    @NotNull(message = "Selecione a forma de pagamento.")
    private FormaPagamento formaPagamento;

    public List<VendaItemForm> getItens() {
        return itens;
    }

    public void setItens(List<VendaItemForm> itens) {
        this.itens = itens;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }
}
