package br.com.estoqueinox.model;

public enum FormaPagamento {
    PIX("Pix"),
    DINHEIRO("Dinheiro"),
    CARTAO_DEBITO("Cartao de debito"),
    CARTAO_CREDITO("Cartao de credito"),
    OUTRO("Outro");

    private final String descricao;

    FormaPagamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
