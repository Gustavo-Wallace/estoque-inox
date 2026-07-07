package br.com.estoqueinox.model;

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
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "movimentacoes_estoque")
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoMovimentacaoEstoque tipo;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer quantidade;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer estoqueAnterior;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer estoquePosterior;

    @Column(length = 255)
    private String observacao;

    @NotNull
    @Column(nullable = false, length = 100)
    private String usuarioResponsavel;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    public MovimentacaoEstoque() {
    }

    public MovimentacaoEstoque(
            Produto produto,
            TipoMovimentacaoEstoque tipo,
            Integer quantidade,
            Integer estoqueAnterior,
            Integer estoquePosterior,
            String observacao,
            String usuarioResponsavel
    ) {
        this.produto = produto;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.estoqueAnterior = estoqueAnterior;
        this.estoquePosterior = estoquePosterior;
        this.observacao = observacao;
        this.usuarioResponsavel = usuarioResponsavel;
    }

    @PrePersist
    public void prePersist() {
        criadoEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public TipoMovimentacaoEstoque getTipo() {
        return tipo;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public Integer getEstoqueAnterior() {
        return estoqueAnterior;
    }

    public Integer getEstoquePosterior() {
        return estoquePosterior;
    }

    public String getObservacao() {
        return observacao;
    }

    public String getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
