package br.com.estoqueinox.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.estoqueinox.model.MovimentacaoEstoque;
import br.com.estoqueinox.model.Produto;
import br.com.estoqueinox.model.Venda;
import br.com.estoqueinox.model.VendaItem;
import br.com.estoqueinox.repository.MovimentacaoEstoqueRepository;
import br.com.estoqueinox.repository.ProdutoRepository;
import br.com.estoqueinox.repository.VendaRepository;

@Service
public class ExportacaoService {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ProdutoRepository produtoRepository;
    private final VendaRepository vendaRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    public ExportacaoService(
            ProdutoRepository produtoRepository,
            VendaRepository vendaRepository,
            MovimentacaoEstoqueRepository movimentacaoEstoqueRepository
    ) {
        this.produtoRepository = produtoRepository;
        this.vendaRepository = vendaRepository;
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
    }

    @Transactional(readOnly = true)
    public ArquivoCsv exportarProdutos() {
        CsvBuilder csv = new CsvBuilder()
                .linha("codigo", "nome", "categoria", "preco_custo", "preco_venda", "estoque_atual",
                        "estoque_minimo", "ativo", "criado_em", "atualizado_em");

        for (Produto produto : produtoRepository.findAllWithCategoriaOrderByNome()) {
            csv.linha(
                    produto.getCodigo(),
                    produto.getNome(),
                    produto.getCategoria().getNome(),
                    dinheiro(produto.getPrecoCusto()),
                    dinheiro(produto.getPrecoVenda()),
                    produto.getQuantidadeEstoque(),
                    produto.getEstoqueMinimo(),
                    ativo(produto.getAtivo()),
                    dataHora(produto.getCriadoEm()),
                    dataHora(produto.getAtualizadoEm())
            );
        }

        return new ArquivoCsv("produtos_" + LocalDate.now().format(DATA) + ".csv", csv.toBytes());
    }

    @Transactional(readOnly = true)
    public ArquivoCsv exportarEstoqueBaixo() {
        CsvBuilder csv = new CsvBuilder()
                .linha("codigo", "nome", "categoria", "estoque_atual", "estoque_minimo", "preco_venda", "ativo");

        for (Produto produto : produtoRepository.findComEstoqueBaixo()) {
            csv.linha(
                    produto.getCodigo(),
                    produto.getNome(),
                    produto.getCategoria().getNome(),
                    produto.getQuantidadeEstoque(),
                    produto.getEstoqueMinimo(),
                    dinheiro(produto.getPrecoVenda()),
                    ativo(produto.getAtivo())
            );
        }

        return new ArquivoCsv("estoque_baixo_" + LocalDate.now().format(DATA) + ".csv", csv.toBytes());
    }

    @Transactional(readOnly = true)
    public ArquivoCsv exportarVendas(LocalDate dataInicial, LocalDate dataFinal) {
        Periodo periodo = normalizarPeriodo(dataInicial, dataFinal);
        CsvBuilder csv = new CsvBuilder()
                .linha("id_venda", "data", "usuario_responsavel", "forma_pagamento", "status_venda",
                        "valor_total_original_ativo", "desconto_total_ativo", "valor_total_final_ativo",
                        "quantidade_total_original", "quantidade_total_cancelada", "quantidade_total_ativa");

        for (Venda venda : buscarVendas(periodo)) {
            csv.linha(
                    venda.getId(),
                    dataHora(venda.getCriadoEm()),
                    venda.getUsuarioResponsavel(),
                    venda.getFormaPagamento().getDescricao(),
                    venda.getStatus().getDescricao(),
                    dinheiro(venda.getValorTotalOriginalAtivo()),
                    dinheiro(venda.getValorTotalDescontoAtivo()),
                    dinheiro(venda.getValorTotalFinalAtivo()),
                    quantidadeOriginal(venda),
                    quantidadeCancelada(venda),
                    quantidadeAtiva(venda)
            );
        }

        return new ArquivoCsv(nomePeriodo("vendas", periodo), csv.toBytes());
    }

    @Transactional(readOnly = true)
    public ArquivoCsv exportarMovimentacoesEstoque(LocalDate dataInicial, LocalDate dataFinal) {
        Periodo periodo = normalizarPeriodo(dataInicial, dataFinal);
        CsvBuilder csv = new CsvBuilder()
                .linha("data", "produto_codigo", "produto_nome", "tipo", "quantidade", "estoque_anterior",
                        "estoque_posterior", "usuario_responsavel", "observacao");

        LocalDateTime inicio = periodo.dataInicial().atStartOfDay();
        LocalDateTime fim = periodo.dataFinal().atTime(LocalTime.MAX);
        for (MovimentacaoEstoque movimentacao : movimentacaoEstoqueRepository.findByCriadoEmBetweenOrderByCriadoEmDesc(inicio, fim)) {
            csv.linha(
                    dataHora(movimentacao.getCriadoEm()),
                    movimentacao.getProduto().getCodigo(),
                    movimentacao.getProduto().getNome(),
                    movimentacao.getTipo().getDescricao(),
                    movimentacao.getQuantidade(),
                    movimentacao.getEstoqueAnterior(),
                    movimentacao.getEstoquePosterior(),
                    movimentacao.getUsuarioResponsavel(),
                    movimentacao.getObservacao()
            );
        }

        return new ArquivoCsv(nomePeriodo("movimentacoes_estoque", periodo), csv.toBytes());
    }

    @Transactional(readOnly = true)
    public ArquivoCsv exportarProdutosMaisVendidos(LocalDate dataInicial, LocalDate dataFinal) {
        Periodo periodo = normalizarPeriodo(dataInicial, dataFinal);
        CsvBuilder csv = new CsvBuilder()
                .linha("codigo", "nome", "categoria", "quantidade_vendida_ativa",
                        "valor_bruto_original_ativo", "desconto_total_ativo", "valor_final_vendido_ativo");

        Map<Long, ProdutoVendidoAgrupado> agrupados = new LinkedHashMap<>();
        buscarVendas(periodo).stream()
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
                    agrupado.adicionar(item);
                });

        agrupados.values().stream()
                .sorted(Comparator.comparingInt(ProdutoVendidoAgrupado::quantidadeVendida).reversed()
                        .thenComparing(ProdutoVendidoAgrupado::nome))
                .forEach(produto -> csv.linha(
                        produto.codigo,
                        produto.nome,
                        produto.categoria,
                        produto.quantidadeVendida,
                        dinheiro(produto.valorBrutoOriginal),
                        dinheiro(produto.valorDesconto),
                        dinheiro(produto.valorFinal)
                ));

        return new ArquivoCsv(nomePeriodo("produtos_mais_vendidos", periodo), csv.toBytes());
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

    private List<Venda> buscarVendas(Periodo periodo) {
        LocalDateTime inicio = periodo.dataInicial().atStartOfDay();
        LocalDateTime fim = periodo.dataFinal().atTime(LocalTime.MAX);
        return vendaRepository.findByCriadoEmBetweenOrderByCriadoEmDesc(inicio, fim);
    }

    private int quantidadeOriginal(Venda venda) {
        return venda.getItens().stream()
                .mapToInt(VendaItem::getQuantidade)
                .sum();
    }

    private int quantidadeCancelada(Venda venda) {
        return venda.getItens().stream()
                .mapToInt(VendaItem::getQuantidadeCancelada)
                .sum();
    }

    private int quantidadeAtiva(Venda venda) {
        return venda.getItens().stream()
                .mapToInt(VendaItem::getQuantidadeAtiva)
                .sum();
    }

    private String nomePeriodo(String prefixo, Periodo periodo) {
        return prefixo + "_" + periodo.dataInicial().format(DATA) + "_a_" + periodo.dataFinal().format(DATA) + ".csv";
    }

    private String dataHora(LocalDateTime valor) {
        if (valor == null) {
            return "";
        }
        return valor.format(DATA_HORA);
    }

    private String dinheiro(BigDecimal valor) {
        if (valor == null) {
            return "";
        }
        return valor.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String ativo(Boolean valor) {
        return Boolean.TRUE.equals(valor) ? "sim" : "nao";
    }

    public record ArquivoCsv(String nomeArquivo, byte[] conteudo) {
    }

    public record Periodo(LocalDate dataInicial, LocalDate dataFinal) {
    }

    private static class ProdutoVendidoAgrupado {
        private final String codigo;
        private final String nome;
        private final String categoria;
        private int quantidadeVendida;
        private BigDecimal valorBrutoOriginal = BigDecimal.ZERO;
        private BigDecimal valorDesconto = BigDecimal.ZERO;
        private BigDecimal valorFinal = BigDecimal.ZERO;

        private ProdutoVendidoAgrupado(String codigo, String nome, String categoria) {
            this.codigo = codigo;
            this.nome = nome;
            this.categoria = categoria;
        }

        private void adicionar(VendaItem item) {
            quantidadeVendida += item.getQuantidadeAtiva();
            valorBrutoOriginal = valorBrutoOriginal.add(item.getValorTotalOriginalAtivo());
            valorDesconto = valorDesconto.add(item.getValorTotalDescontoAtivo());
            valorFinal = valorFinal.add(item.getValorTotalFinalAtivo());
        }

        private int quantidadeVendida() {
            return quantidadeVendida;
        }

        private String nome() {
            return nome;
        }
    }

    private static class CsvBuilder {
        private final List<String> linhas = new ArrayList<>();

        private CsvBuilder linha(Object... valores) {
            List<String> escapados = new ArrayList<>();
            for (Object valor : valores) {
                escapados.add(escapar(valor));
            }
            linhas.add(String.join(";", escapados));
            return this;
        }

        private byte[] toBytes() {
            String csv = "\uFEFF" + String.join("\r\n", linhas) + "\r\n";
            return csv.getBytes(StandardCharsets.UTF_8);
        }

        private String escapar(Object valor) {
            if (valor == null) {
                return "";
            }
            String texto = String.valueOf(valor);
            boolean precisaEscapar = texto.contains(";")
                    || texto.contains("\"")
                    || texto.contains("\n")
                    || texto.contains("\r");
            String normalizado = texto.replace("\"", "\"\"");
            if (precisaEscapar) {
                return "\"" + normalizado + "\"";
            }
            return normalizado;
        }
    }
}
