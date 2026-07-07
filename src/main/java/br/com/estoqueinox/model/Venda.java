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
}
