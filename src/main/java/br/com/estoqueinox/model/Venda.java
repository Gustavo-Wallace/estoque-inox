package br.com.estoqueinox.model;

import java.math.BigDecimal;
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
@Table(name = "vendas")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private BigDecimal precoUnitario;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FormaPagamento formaPagamento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusVenda status = StatusVenda.CONCLUIDA;

    @NotNull
    @Column(nullable = false, length = 100)
    private String usuarioResponsavel;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    private LocalDateTime canceladaEm;

    @Column(length = 100)
    private String usuarioCancelamento;

    @Column(length = 255)
    private String motivoCancelamento;

    public Venda() {
    }

    public Venda(
            Produto produto,
            Integer quantidade,
            BigDecimal precoUnitario,
            FormaPagamento formaPagamento,
            String usuarioResponsavel
    ) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.valorTotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
        this.formaPagamento = formaPagamento;
        this.usuarioResponsavel = usuarioResponsavel;
        this.status = StatusVenda.CONCLUIDA;
    }

    @PrePersist
    public void prePersist() {
        criadoEm = LocalDateTime.now();
        valorTotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
        if (status == null) {
            status = StatusVenda.CONCLUIDA;
        }
    }

    public Long getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public StatusVenda getStatus() {
        return status;
    }

    public String getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getCanceladaEm() {
        return canceladaEm;
    }

    public String getUsuarioCancelamento() {
        return usuarioCancelamento;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void cancelar(String username, String motivoCancelamento) {
        status = StatusVenda.CANCELADA;
        canceladaEm = LocalDateTime.now();
        usuarioCancelamento = username;
        this.motivoCancelamento = normalizarMotivo(motivoCancelamento);
    }

    private String normalizarMotivo(String motivoCancelamento) {
        if (motivoCancelamento == null || motivoCancelamento.isBlank()) {
            return null;
        }
        return motivoCancelamento.trim();
    }
}
