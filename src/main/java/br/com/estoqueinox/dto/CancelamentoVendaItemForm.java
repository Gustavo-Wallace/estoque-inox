package br.com.estoqueinox.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CancelamentoVendaItemForm {

    @NotNull(message = "Informe a quantidade a cancelar.")
    @Positive(message = "A quantidade a cancelar deve ser maior que zero.")
    private Integer quantidadeCancelar;

    @Size(max = 255, message = "O motivo deve ter no maximo 255 caracteres.")
    private String motivoCancelamento;

    public Integer getQuantidadeCancelar() {
        return quantidadeCancelar;
    }

    public void setQuantidadeCancelar(Integer quantidadeCancelar) {
        this.quantidadeCancelar = quantidadeCancelar;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }
}
