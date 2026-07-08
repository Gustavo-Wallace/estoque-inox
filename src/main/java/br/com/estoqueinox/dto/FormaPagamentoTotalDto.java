package br.com.estoqueinox.dto;

import java.math.BigDecimal;

import br.com.estoqueinox.model.FormaPagamento;

public record FormaPagamentoTotalDto(
        FormaPagamento formaPagamento,
        BigDecimal totalBruto,
        BigDecimal totalLiquido
) {
}
