package br.com.estoqueinox.model;

public enum StatusVenda {
    CONCLUIDA("Concluida"),
    PARCIALMENTE_CANCELADA("Parcialmente cancelada"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusVenda(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
