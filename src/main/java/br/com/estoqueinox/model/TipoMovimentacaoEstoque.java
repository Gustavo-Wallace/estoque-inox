package br.com.estoqueinox.model;

public enum TipoMovimentacaoEstoque {
    ENTRADA("Entrada"),
    AJUSTE_POSITIVO("Ajuste positivo"),
    AJUSTE_NEGATIVO("Ajuste negativo"),
    VENDA("Venda"),
    CANCELAMENTO("Cancelamento");

    private final String descricao;

    TipoMovimentacaoEstoque(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
