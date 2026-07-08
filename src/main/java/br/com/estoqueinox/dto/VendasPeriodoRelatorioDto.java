package br.com.estoqueinox.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record VendasPeriodoRelatorioDto(
        LocalDate dataInicial,
        LocalDate dataFinal,
        BigDecimal totalBruto,
        BigDecimal totalDesconto,
        BigDecimal totalLiquido,
        List<FormaPagamentoTotalDto> totaisPorFormaPagamento,
        List<VendaRelatorioDto> vendas
) {
}
