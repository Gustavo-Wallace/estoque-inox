package br.com.estoqueinox.dto;

import jakarta.validation.constraints.Size;

public class CancelamentoVendaForm {

    @Size(max = 255, message = "O motivo deve ter no maximo 255 caracteres.")
    private String motivoCancelamento;

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }
}
