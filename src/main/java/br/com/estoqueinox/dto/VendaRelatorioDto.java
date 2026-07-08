package br.com.estoqueinox.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.estoqueinox.model.FormaPagamento;
import br.com.estoqueinox.model.StatusVenda;

public record VendaRelatorioDto(
        Long id,
        LocalDateTime criadoEm,
        String usuarioResponsavel,
        FormaPagamento formaPagamento,
        StatusVenda status,
        BigDecimal valorOriginal,
        BigDecimal valorDesconto,
        BigDecimal valorLiquido
) {
}
