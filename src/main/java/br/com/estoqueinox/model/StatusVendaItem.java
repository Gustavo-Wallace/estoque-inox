package br.com.estoqueinox.model;

public enum StatusVendaItem {
    CONCLUIDO("Concluido"),
    PARCIALMENTE_CANCELADO("Parcialmente cancelado"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusVendaItem(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
