package br.com.estoqueinox.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "vendas")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<VendaItem> itens = new ArrayList<>();

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

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

    public Venda(FormaPagamento formaPagamento, String usuarioResponsavel) {
        this.formaPagamento = formaPagamento;
        this.usuarioResponsavel = usuarioResponsavel;
        this.status = StatusVenda.CONCLUIDA;
    }

    @PrePersist
    public void prePersist() {
        criadoEm = LocalDateTime.now();
        if (status == null) {
            status = StatusVenda.CONCLUIDA;
        }
        recalcularValorTotal();
    }

    public void adicionarItem(VendaItem item) {
        item.setVenda(this);
        itens.add(item);
        recalcularValorTotal();
    }

    public void cancelar(String username, String motivoCancelamento) {
        status = StatusVenda.CANCELADA;
        canceladaEm = LocalDateTime.now();
        usuarioCancelamento = username;
        this.motivoCancelamento = normalizarMotivo(motivoCancelamento);
    }

    public void atualizarStatusAposCancelamento(String username, String motivoCancelamento) {
        boolean todosCancelados = itens.stream()
                .allMatch(item -> item.getQuantidadeAtiva() == 0);
        boolean algumCancelado = itens.stream()
                .anyMatch(item -> item.getQuantidadeCancelada() > 0);

        if (todosCancelados) {
            cancelar(username, motivoCancelamento);
        } else if (algumCancelado) {
            status = StatusVenda.PARCIALMENTE_CANCELADA;
        } else {
            status = StatusVenda.CONCLUIDA;
        }
    }

    public void recalcularValorTotal() {
        valorTotalOriginal = itens.stream()
                .map(VendaItem::getValorTotalOriginal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        valorTotalDesconto = itens.stream()
                .map(VendaItem::getValorTotalDesconto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        valorTotalFinal = itens.stream()
                .map(VendaItem::getValorTotalFinal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        valorTotal = valorTotalFinal;
    }

    public Long getId() {
        return id;
    }

    public List<VendaItem> getItens() {
        return itens;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
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

    private String normalizarMotivo(String motivoCancelamento) {
        if (motivoCancelamento == null || motivoCancelamento.isBlank()) {
            return null;
        }
        return motivoCancelamento.trim();
    }
}
