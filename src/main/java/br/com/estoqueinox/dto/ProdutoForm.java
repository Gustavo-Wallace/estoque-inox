package br.com.estoqueinox.dto;

import java.math.BigDecimal;

import br.com.estoqueinox.model.Produto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class ProdutoForm {

    @NotBlank(message = "Informe o codigo do produto.")
    private String codigo;

    @NotBlank(message = "Informe o nome do produto.")
    private String nome;

    private String descricao;

    @NotNull(message = "Selecione uma categoria.")
    private Long categoriaId;

    @PositiveOrZero(message = "O preco de custo nao pode ser negativo.")
    private BigDecimal precoCusto;

    @NotNull(message = "Informe o preco de venda.")
    @PositiveOrZero(message = "O preco de venda nao pode ser negativo.")
    private BigDecimal precoVenda;

    @NotNull(message = "Informe a quantidade em estoque.")
    @PositiveOrZero(message = "A quantidade em estoque nao pode ser negativa.")
    private Integer quantidadeEstoque = 0;

    @NotNull(message = "Informe o estoque minimo.")
    @PositiveOrZero(message = "O estoque minimo nao pode ser negativo.")
    private Integer estoqueMinimo = 0;

    private Boolean ativo = true;

    public static ProdutoForm from(Produto produto) {
        ProdutoForm form = new ProdutoForm();
        form.setCodigo(produto.getCodigo());
        form.setNome(produto.getNome());
        form.setDescricao(produto.getDescricao());
        form.setCategoriaId(produto.getCategoria().getId());
        form.setPrecoCusto(produto.getPrecoCusto());
        form.setPrecoVenda(produto.getPrecoVenda());
        form.setQuantidadeEstoque(produto.getQuantidadeEstoque());
        form.setEstoqueMinimo(produto.getEstoqueMinimo());
        form.setAtivo(produto.getAtivo());
        return form;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public BigDecimal getPrecoCusto() {
        return precoCusto;
    }

    public void setPrecoCusto(BigDecimal precoCusto) {
        this.precoCusto = precoCusto;
    }

    public BigDecimal getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(BigDecimal precoVenda) {
        this.precoVenda = precoVenda;
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(Integer quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public Integer getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(Integer estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
