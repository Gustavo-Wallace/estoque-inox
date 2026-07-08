package br.com.estoqueinox.dto;

import java.math.BigDecimal;

public record VendedoraRelatorioDto(
        String usuarioResponsavel,
        long quantidadeVendasRegistradas,
        int itensVendidos,
        BigDecimal totalLiquido,
        long vendasCanceladasOuParciais
) {
}
