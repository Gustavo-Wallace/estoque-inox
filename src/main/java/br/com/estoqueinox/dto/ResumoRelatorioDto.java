package br.com.estoqueinox.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import br.com.estoqueinox.model.Produto;

public record ResumoRelatorioDto(
        LocalDate data,
        BigDecimal totalBruto,
        BigDecimal totalLiquido,
        long vendasConcluidas,
        long vendasParcialmenteCanceladas,
        long vendasCanceladas,
        int itensVendidos,
        List<FormaPagamentoTotalDto> totaisPorFormaPagamento,
        List<Produto> produtosEstoqueBaixo
) {
}
