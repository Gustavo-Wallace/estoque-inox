package br.com.estoqueinox.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "venda_itens")
public class VendaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer quantidade;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoOriginalUnitario = BigDecimal.ZERO;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal descontoUnitario = BigDecimal.ZERO;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoFinalUnitario = BigDecimal.ZERO;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotalOriginal = BigDecimal.ZERO;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotalDesconto = BigDecimal.ZERO;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotalFinal = BigDecimal.ZERO;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusVendaItem status = StatusVendaItem.CONCLUIDO;

    private LocalDateTime canceladoEm;

    @Column(length = 100)
    private String usuarioCancelamento;

    @Column(length = 255)
    private String motivoCancelamento;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    public VendaItem() {
    }

    public VendaItem(Produto produto, Integer quantidade, BigDecimal precoOriginalUnitario, BigDecimal descontoUnitario) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoOriginalUnitario = moeda(precoOriginalUnitario);
        this.descontoUnitario = moeda(descontoUnitario == null ? BigDecimal.ZERO : descontoUnitario);
        this.status = StatusVendaItem.CONCLUIDO;
        recalcularValores();
    }

    public VendaItem(Produto produto, Integer quantidade, BigDecimal precoOriginalUnitario) {
        this(produto, quantidade, precoOriginalUnitario, BigDecimal.ZERO);
    }

    @PrePersist
    public void prePersist() {
        criadoEm = LocalDateTime.now();
        recalcularValores();
        if (status == null) {
            status = StatusVendaItem.CONCLUIDO;
        }
    }

    public void cancelar(String username, String motivoCancelamento) {
        status = StatusVendaItem.CANCELADO;
        canceladoEm = LocalDateTime.now();
        usuarioCancelamento = username;
        this.motivoCancelamento = normalizarMotivo(motivoCancelamento);
    }

    public void recalcularValores() {
        precoOriginalUnitario = moeda(precoOriginalUnitario);
        descontoUnitario = moeda(descontoUnitario);
        precoFinalUnitario = moeda(precoOriginalUnitario.subtract(descontoUnitario));
        valorTotalOriginal = moeda(precoOriginalUnitario.multiply(BigDecimal.valueOf(quantidade)));
        valorTotalDesconto = moeda(descontoUnitario.multiply(BigDecimal.valueOf(quantidade)));
        valorTotalFinal = moeda(precoFinalUnitario.multiply(BigDecimal.valueOf(quantidade)));
    }

    public Long getId() {
        return id;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Produto getProduto() {
        return produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoOriginalUnitario() {
        return precoOriginalUnitario;
    }

    public BigDecimal getDescontoUnitario() {
        return descontoUnitario;
    }

    public BigDecimal getPrecoFinalUnitario() {
        return precoFinalUnitario;
    }

    public BigDecimal getValorTotalOriginal() {
        return valorTotalOriginal;
    }

    public BigDecimal getValorTotalDesconto() {
        return valorTotalDesconto;
    }

    public BigDecimal getValorTotalFinal() {
        return valorTotalFinal;
    }

    public BigDecimal getPrecoUnitario() {
        return precoFinalUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotalFinal;
    }

    public StatusVendaItem getStatus() {
        return status;
    }

    public LocalDateTime getCanceladoEm() {
        return canceladoEm;
    }

    public String getUsuarioCancelamento() {
        return usuarioCancelamento;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    private BigDecimal moeda(BigDecimal valor) {
        if (valor == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    private String normalizarMotivo(String motivoCancelamento) {
        if (motivoCancelamento == null || motivoCancelamento.isBlank()) {
            return null;
        }
        return motivoCancelamento.trim();
    }
}
