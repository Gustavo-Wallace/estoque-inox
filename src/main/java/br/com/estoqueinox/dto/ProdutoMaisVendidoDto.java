package br.com.estoqueinox.dto;

import java.math.BigDecimal;

public record ProdutoMaisVendidoDto(
        String codigo,
        String nome,
        String categoria,
        int quantidadeVendida,
        BigDecimal valorTotal
) {
}
