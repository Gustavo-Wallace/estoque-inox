package br.com.estoqueinox.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.estoqueinox.model.MovimentacaoEstoque;
import br.com.estoqueinox.model.Produto;
import br.com.estoqueinox.model.TipoMovimentacaoEstoque;
import br.com.estoqueinox.repository.MovimentacaoEstoqueRepository;
import br.com.estoqueinox.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class EstoqueService {

    private final ProdutoRepository produtoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    public EstoqueService(
            ProdutoRepository produtoRepository,
            MovimentacaoEstoqueRepository movimentacaoEstoqueRepository
    ) {
        this.produtoRepository = produtoRepository;
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
    }

    public List<MovimentacaoEstoque> listarMovimentacoes() {
        return movimentacaoEstoqueRepository.findAllOrderByCriadoEmDesc();
    }

    public List<MovimentacaoEstoque> listarMovimentacoesDoProduto(Long produtoId) {
        return movimentacaoEstoqueRepository.findByProdutoIdOrderByCriadoEmDesc(produtoId);
    }

    @Transactional
    public void registrarEntrada(Long produtoId, Integer quantidade, String observacao, String username) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade de entrada deve ser maior que zero.");
        }

        Produto produto = buscarProduto(produtoId);
        int estoqueAnterior = produto.getQuantidadeEstoque();
        int estoquePosterior = estoqueAnterior + quantidade;

        produto.setQuantidadeEstoque(estoquePosterior);
        movimentacaoEstoqueRepository.save(new MovimentacaoEstoque(
                produto,
                TipoMovimentacaoEstoque.ENTRADA,
                quantidade,
                estoqueAnterior,
                estoquePosterior,
                normalizarObservacao(observacao),
                username
        ));
    }

    @Transactional
    public void registrarAjuste(Long produtoId, Integer novaQuantidade, String observacao, String username) {
        if (novaQuantidade == null || novaQuantidade < 0) {
            throw new IllegalArgumentException("A nova quantidade nao pode ser negativa.");
        }

        Produto produto = buscarProduto(produtoId);
        int estoqueAnterior = produto.getQuantidadeEstoque();

        if (estoqueAnterior == novaQuantidade) {
            throw new IllegalArgumentException("Nao houve alteracao no estoque do produto.");
        }

        TipoMovimentacaoEstoque tipo = novaQuantidade > estoqueAnterior
                ? TipoMovimentacaoEstoque.AJUSTE_POSITIVO
                : TipoMovimentacaoEstoque.AJUSTE_NEGATIVO;

        int diferenca = Math.abs(novaQuantidade - estoqueAnterior);
        produto.setQuantidadeEstoque(novaQuantidade);

        movimentacaoEstoqueRepository.save(new MovimentacaoEstoque(
                produto,
                tipo,
                diferenca,
                estoqueAnterior,
                novaQuantidade,
                normalizarObservacao(observacao),
                username
        ));
    }

    public void registrarBaixaPorVenda(Produto produto, Integer quantidade, String username) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade vendida deve ser maior que zero.");
        }

        int estoqueAnterior = produto.getQuantidadeEstoque();
        if (quantidade > estoqueAnterior) {
            throw new IllegalArgumentException("Estoque insuficiente para registrar a venda.");
        }

        int estoquePosterior = estoqueAnterior - quantidade;
        produto.setQuantidadeEstoque(estoquePosterior);

        movimentacaoEstoqueRepository.save(new MovimentacaoEstoque(
                produto,
                TipoMovimentacaoEstoque.VENDA,
                quantidade,
                estoqueAnterior,
                estoquePosterior,
                "Venda registrada",
                username
        ));
    }

    public void registrarEstornoPorCancelamento(Produto produto, Integer quantidade, Long vendaId, String motivo, String username) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade cancelada deve ser maior que zero.");
        }

        int estoqueAnterior = produto.getQuantidadeEstoque();
        int estoquePosterior = estoqueAnterior + quantidade;
        produto.setQuantidadeEstoque(estoquePosterior);

        movimentacaoEstoqueRepository.save(new MovimentacaoEstoque(
                produto,
                TipoMovimentacaoEstoque.CANCELAMENTO,
                quantidade,
                estoqueAnterior,
                estoquePosterior,
                montarObservacaoCancelamento(vendaId, motivo),
                username
        ));
    }

    private Produto buscarProduto(Long produtoId) {
        return produtoRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto nao encontrado."));
    }

    private String normalizarObservacao(String observacao) {
        if (observacao == null || observacao.isBlank()) {
            return null;
        }
        return observacao.trim();
    }

    private String montarObservacaoCancelamento(Long vendaId, String motivo) {
        String observacao = "Cancelamento da venda #" + vendaId;
        if (motivo != null && !motivo.isBlank()) {
            observacao += " - motivo: " + motivo.trim();
        }
        return observacao;
    }
}
