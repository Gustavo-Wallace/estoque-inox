package br.com.estoqueinox.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.estoqueinox.model.FormaPagamento;
import br.com.estoqueinox.model.Produto;
import br.com.estoqueinox.model.StatusVenda;
import br.com.estoqueinox.model.Venda;
import br.com.estoqueinox.repository.ProdutoRepository;
import br.com.estoqueinox.repository.VendaRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueService estoqueService;

    public VendaService(
            VendaRepository vendaRepository,
            ProdutoRepository produtoRepository,
            EstoqueService estoqueService
    ) {
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueService = estoqueService;
    }

    public List<Venda> listarTodas() {
        return vendaRepository.findAllOrderByCriadoEmDesc();
    }

    public List<Venda> listarPorUsuario(String username) {
        return vendaRepository.findByUsuarioResponsavelOrderByCriadoEmDesc(username);
    }

    public List<Produto> listarProdutosDisponiveis() {
        return produtoRepository.findAtivosComEstoqueWithCategoriaOrderByNome();
    }

    public Venda buscarPorId(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venda nao encontrada."));
    }

    public Venda buscarParaCancelamento(Long vendaId, String username, boolean admin) {
        Venda venda = buscarPorId(vendaId);
        validarPermissaoCancelamento(venda, username, admin);
        validarVendaConcluida(venda);
        return venda;
    }

    @Transactional
    public Venda registrarVenda(Long produtoId, Integer quantidade, FormaPagamento formaPagamento, String username) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }

        if (formaPagamento == null) {
            throw new IllegalArgumentException("Selecione a forma de pagamento.");
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto nao encontrado."));

        if (!Boolean.TRUE.equals(produto.getAtivo())) {
            throw new IllegalArgumentException("Nao e possivel vender produto inativo.");
        }

        Venda venda = new Venda(
                produto,
                quantidade,
                produto.getPrecoVenda(),
                formaPagamento,
                username
        );

        estoqueService.registrarBaixaPorVenda(produto, quantidade, username);
        return vendaRepository.save(venda);
    }

    @Transactional
    public void cancelarVenda(Long vendaId, String motivoCancelamento, String username, boolean admin) {
        Venda venda = buscarPorId(vendaId);
        validarPermissaoCancelamento(venda, username, admin);
        validarVendaConcluida(venda);

        Produto produto = venda.getProduto();
        venda.cancelar(username, motivoCancelamento);
        estoqueService.registrarEstornoPorCancelamento(
                produto,
                venda.getQuantidade(),
                venda.getId(),
                motivoCancelamento,
                username
        );
    }

    public boolean podeCancelar(Venda venda, String username, boolean admin) {
        return venda.getStatus() == StatusVenda.CONCLUIDA
                && (admin || venda.getUsuarioResponsavel().equals(username));
    }

    private void validarPermissaoCancelamento(Venda venda, String username, boolean admin) {
        if (!admin && !venda.getUsuarioResponsavel().equals(username)) {
            throw new IllegalArgumentException("Voce nao pode cancelar uma venda registrada por outra pessoa.");
        }
    }

    private void validarVendaConcluida(Venda venda) {
        if (venda.getStatus() == StatusVenda.CANCELADA) {
            throw new IllegalArgumentException("Esta venda ja foi cancelada.");
        }
    }
}
