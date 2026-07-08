package br.com.estoqueinox.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.estoqueinox.dto.FormaPagamentoTotalDto;
import br.com.estoqueinox.dto.ProdutoMaisVendidoDto;
import br.com.estoqueinox.dto.ResumoRelatorioDto;
import br.com.estoqueinox.dto.VendaRelatorioDto;
import br.com.estoqueinox.dto.VendasPeriodoRelatorioDto;
import br.com.estoqueinox.dto.VendedoraRelatorioDto;
import br.com.estoqueinox.model.FormaPagamento;
import br.com.estoqueinox.model.Produto;
import br.com.estoqueinox.model.StatusVenda;
import br.com.estoqueinox.model.Venda;
import br.com.estoqueinox.model.VendaItem;
import br.com.estoqueinox.repository.ProdutoRepository;
import br.com.estoqueinox.repository.VendaRepository;

@Service
public class RelatorioService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;

    public RelatorioService(VendaRepository vendaRepository, ProdutoRepository produtoRepository) {
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public ResumoRelatorioDto resumoDoDia() {
        LocalDate hoje = LocalDate.now();
        List<Venda> vendas = buscarVendasNoPeriodo(hoje, hoje);

        return new ResumoRelatorioDto(
                hoje,
                calcularTotalBruto(vendas),
                calcularTotalDesconto(vendas),
                calcularTotalLiquido(vendas),
                contarPorStatus(vendas, StatusVenda.CONCLUIDA),
                contarPorStatus(vendas, StatusVenda.PARCIALMENTE_CANCELADA),
                contarPorStatus(vendas, StatusVenda.CANCELADA),
                calcularItensVendidos(vendas),
                calcularTotaisPorFormaPagamento(vendas),
                produtoRepository.findComEstoqueBaixo()
        );
    }

    @Transactional(readOnly = true)
    public VendasPeriodoRelatorioDto vendasPorPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        Periodo periodo = normalizarPeriodo(dataInicial, dataFinal);
        List<Venda> vendas = buscarVendasNoPeriodo(periodo.dataInicial(), periodo.dataFinal());
        List<VendaRelatorioDto> linhas = vendas.stream()
                .map(venda -> new VendaRelatorioDto(
                        venda.getId(),
                        venda.getCriadoEm(),
                        venda.getUsuarioResponsavel(),
                        venda.getFormaPagamento(),
                        venda.getStatus(),
                        calcularValorOriginal(venda),
                        calcularValorDesconto(venda),
                        calcularValorLiquido(venda)
                ))
                .toList();

        return new VendasPeriodoRelatorioDto(
                periodo.dataInicial(),
                periodo.dataFinal(),
                calcularTotalBruto(vendas),
                calcularTotalDesconto(vendas),
                calcularTotalLiquido(vendas),
                calcularTotaisPorFormaPagamento(vendas),
                linhas
        );
    }

    @Transactional(readOnly = true)
    public List<ProdutoMaisVendidoDto> produtosMaisVendidos(LocalDate dataInicial, LocalDate dataFinal) {
        Periodo periodo = normalizarPeriodo(dataInicial, dataFinal);
        List<Venda> vendas = buscarVendasNoPeriodo(periodo.dataInicial(), periodo.dataFinal());
        Map<Long, ProdutoVendidoAgrupado> agrupados = new LinkedHashMap<>();

        vendas.stream()
                .flatMap(venda -> venda.getItens().stream())
                .filter(item -> item.getQuantidadeAtiva() > 0)
                .forEach(item -> {
                    Produto produto = item.getProduto();
                    ProdutoVendidoAgrupado agrupado = agrupados.computeIfAbsent(
                            produto.getId(),
                            id -> new ProdutoVendidoAgrupado(
                                    produto.getCodigo(),
                                    produto.getNome(),
                                    produto.getCategoria().getNome()
                            )
                    );
                    agrupado.adicionar(item.getQuantidadeAtiva(), item.getValorTotalFinalAtivo());
                });

        return agrupados.values().stream()
                .sorted(Comparator.comparingInt(ProdutoVendidoAgrupado::quantidadeVendida).reversed()
                        .thenComparing(ProdutoVendidoAgrupado::nome))
                .map(ProdutoVendidoAgrupado::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Produto> produtosComEstoqueBaixo() {
        return produtoRepository.findComEstoqueBaixo();
    }

    @Transactional(readOnly = true)
    public List<VendedoraRelatorioDto> vendasPorVendedora(LocalDate dataInicial, LocalDate dataFinal) {
        Periodo periodo = normalizarPeriodo(dataInicial, dataFinal);
        List<Venda> vendas = buscarVendasNoPeriodo(periodo.dataInicial(), periodo.dataFinal());
        Map<String, VendedoraAgrupada> agrupadas = new LinkedHashMap<>();

        for (Venda venda : vendas) {
            VendedoraAgrupada agrupada = agrupadas.computeIfAbsent(
                    venda.getUsuarioResponsavel(),
                    VendedoraAgrupada::new
            );
            agrupada.adicionarVenda(venda, calcularValorLiquido(venda), calcularItensVendidos(venda));
        }

        return agrupadas.values().stream()
                .sorted(Comparator.comparing(VendedoraAgrupada::usuarioResponsavel))
                .map(VendedoraAgrupada::toDto)
                .toList();
    }

    public Periodo normalizarPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        LocalDate hoje = LocalDate.now();
        LocalDate inicio = dataInicial != null ? dataInicial : hoje;
        LocalDate fim = dataFinal != null ? dataFinal : inicio;

        if (fim.isBefore(inicio)) {
            return new Periodo(fim, inicio);
        }
        return new Periodo(inicio, fim);
    }

    private List<Venda> buscarVendasNoPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        LocalDateTime inicio = dataInicial.atStartOfDay();
        LocalDateTime fim = dataFinal.atTime(LocalTime.MAX);
        return vendaRepository.findByCriadoEmBetweenOrderByCriadoEmDesc(inicio, fim);
    }

    private BigDecimal calcularTotalBruto(List<Venda> vendas) {
        return vendas.stream()
                .map(this::calcularValorOriginal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularTotalDesconto(List<Venda> vendas) {
        return vendas.stream()
                .flatMap(venda -> venda.getItens().stream())
                .filter(item -> item.getQuantidadeAtiva() > 0)
                .map(VendaItem::getValorTotalDescontoAtivo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularTotalLiquido(List<Venda> vendas) {
        return vendas.stream()
                .map(this::calcularValorLiquido)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularValorLiquido(Venda venda) {
        return venda.getItens().stream()
                .filter(item -> item.getQuantidadeAtiva() > 0)
                .map(VendaItem::getValorTotalFinalAtivo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularValorOriginal(Venda venda) {
        return venda.getItens().stream()
                .filter(item -> item.getQuantidadeAtiva() > 0)
                .map(VendaItem::getValorTotalOriginalAtivo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularValorDesconto(Venda venda) {
        return venda.getItens().stream()
                .filter(item -> item.getQuantidadeAtiva() > 0)
                .map(VendaItem::getValorTotalDescontoAtivo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int calcularItensVendidos(List<Venda> vendas) {
        return vendas.stream()
                .mapToInt(this::calcularItensVendidos)
                .sum();
    }

    private int calcularItensVendidos(Venda venda) {
        return venda.getItens().stream()
                .filter(item -> item.getQuantidadeAtiva() > 0)
                .mapToInt(VendaItem::getQuantidadeAtiva)
                .sum();
    }

    private long contarPorStatus(List<Venda> vendas, StatusVenda status) {
        return vendas.stream()
                .filter(venda -> venda.getStatus() == status)
                .count();
    }

    private List<FormaPagamentoTotalDto> calcularTotaisPorFormaPagamento(List<Venda> vendas) {
        Map<FormaPagamento, TotalFormaAgrupado> totais = new EnumMap<>(FormaPagamento.class);
        for (FormaPagamento formaPagamento : FormaPagamento.values()) {
            totais.put(formaPagamento, new TotalFormaAgrupado(formaPagamento));
        }

        for (Venda venda : vendas) {
            totais.get(venda.getFormaPagamento()).adicionar(
                    calcularValorOriginal(venda),
                    calcularValorDesconto(venda),
                    calcularValorLiquido(venda)
            );
        }

        return new ArrayList<>(totais.values()).stream()
                .map(TotalFormaAgrupado::toDto)
                .toList();
    }

    public record Periodo(LocalDate dataInicial, LocalDate dataFinal) {
    }

    private static class TotalFormaAgrupado {
        private final FormaPagamento formaPagamento;
        private BigDecimal totalBruto = BigDecimal.ZERO;
        private BigDecimal totalDesconto = BigDecimal.ZERO;
        private BigDecimal totalLiquido = BigDecimal.ZERO;

        private TotalFormaAgrupado(FormaPagamento formaPagamento) {
            this.formaPagamento = formaPagamento;
        }

        private void adicionar(BigDecimal bruto, BigDecimal desconto, BigDecimal liquido) {
            totalBruto = totalBruto.add(bruto);
            totalDesconto = totalDesconto.add(desconto);
            totalLiquido = totalLiquido.add(liquido);
        }

        private FormaPagamentoTotalDto toDto() {
            return new FormaPagamentoTotalDto(formaPagamento, totalBruto, totalDesconto, totalLiquido);
        }
    }

    private static class ProdutoVendidoAgrupado {
        private final String codigo;
        private final String nome;
        private final String categoria;
        private int quantidadeVendida;
        private BigDecimal valorTotal = BigDecimal.ZERO;

        private ProdutoVendidoAgrupado(String codigo, String nome, String categoria) {
            this.codigo = codigo;
            this.nome = nome;
            this.categoria = categoria;
        }

        private void adicionar(Integer quantidade, BigDecimal valor) {
            quantidadeVendida += quantidade;
            valorTotal = valorTotal.add(valor);
        }

        private int quantidadeVendida() {
            return quantidadeVendida;
        }

        private String nome() {
            return nome;
        }

        private ProdutoMaisVendidoDto toDto() {
            return new ProdutoMaisVendidoDto(codigo, nome, categoria, quantidadeVendida, valorTotal);
        }
    }

    private static class VendedoraAgrupada {
        private final String usuarioResponsavel;
        private long quantidadeVendasRegistradas;
        private int itensVendidos;
        private BigDecimal totalLiquido = BigDecimal.ZERO;
        private long vendasCanceladasOuParciais;

        private VendedoraAgrupada(String usuarioResponsavel) {
            this.usuarioResponsavel = usuarioResponsavel;
        }

        private void adicionarVenda(Venda venda, BigDecimal valorLiquido, int itensConcluidos) {
            quantidadeVendasRegistradas++;
            itensVendidos += itensConcluidos;
            totalLiquido = totalLiquido.add(valorLiquido);
            if (venda.getStatus() == StatusVenda.CANCELADA
                    || venda.getStatus() == StatusVenda.PARCIALMENTE_CANCELADA) {
                vendasCanceladasOuParciais++;
            }
        }

        private String usuarioResponsavel() {
            return usuarioResponsavel;
        }

        private VendedoraRelatorioDto toDto() {
            return new VendedoraRelatorioDto(
                    usuarioResponsavel,
                    quantidadeVendasRegistradas,
                    itensVendidos,
                    totalLiquido,
                    vendasCanceladasOuParciais
            );
        }
    }
}
