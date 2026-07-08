package br.com.estoqueinox.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.estoqueinox.dto.VendaForm;
import br.com.estoqueinox.dto.VendaItemForm;
import br.com.estoqueinox.model.Produto;
import br.com.estoqueinox.model.StatusVenda;
import br.com.estoqueinox.model.StatusVendaItem;
import br.com.estoqueinox.model.Venda;
import br.com.estoqueinox.model.VendaItem;
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
        return vendaRepository.findByIdWithItens(id)
                .orElseThrow(() -> new EntityNotFoundException("Venda nao encontrada."));
    }

    public Venda buscarParaDetalhe(Long vendaId, String username, boolean admin) {
        Venda venda = buscarPorId(vendaId);
        validarPermissaoUsuario(venda, username, admin);
        return venda;
    }

    public Venda buscarParaCancelamento(Long vendaId, String username, boolean admin) {
        Venda venda = buscarParaDetalhe(vendaId, username, admin);
        validarVendaNaoCancelada(venda);
        return venda;
    }

    @Transactional
    public Venda registrarVenda(VendaForm form, String username) {
        validarFormulario(form);

        List<ItemVendaValidado> itensValidados = new ArrayList<>();
        for (VendaItemForm itemForm : form.getItens()) {
            Produto produto = buscarProdutoParaVenda(itemForm.getProdutoId());
            validarEstoque(produto, itemForm.getQuantidade());
            BigDecimal descontoUnitario = normalizarDesconto(itemForm.getDescontoUnitario());
            validarDesconto(produto, descontoUnitario);
            itensValidados.add(new ItemVendaValidado(produto, itemForm.getQuantidade(), descontoUnitario));
        }

        Venda venda = new Venda(form.getFormaPagamento(), username);
        for (ItemVendaValidado itemValidado : itensValidados) {
            VendaItem item = new VendaItem(
                    itemValidado.produto(),
                    itemValidado.quantidade(),
                    itemValidado.produto().getPrecoVenda(),
                    itemValidado.descontoUnitario()
            );
            venda.adicionarItem(item);
            estoqueService.registrarBaixaPorVenda(itemValidado.produto(), itemValidado.quantidade(), username);
        }

        return vendaRepository.save(venda);
    }

    @Transactional
    public void cancelarVenda(Long vendaId, String motivoCancelamento, String username, boolean admin) {
        Venda venda = buscarParaCancelamento(vendaId, username, admin);

        boolean algumItemCancelado = false;
        for (VendaItem item : venda.getItens()) {
            if (item.getStatus() == StatusVendaItem.CONCLUIDO) {
                item.cancelar(username, motivoCancelamento);
                estoqueService.registrarEstornoPorCancelamento(
                        item.getProduto(),
                        item.getQuantidade(),
                        venda.getId(),
                        motivoCancelamento,
                        username
                );
                algumItemCancelado = true;
            }
        }

        if (!algumItemCancelado) {
            throw new IllegalArgumentException("Esta venda nao possui itens para cancelar.");
        }

        venda.cancelar(username, motivoCancelamento);
    }

    @Transactional
    public void cancelarItem(Long vendaId, Long itemId, String motivoCancelamento, String username, boolean admin) {
        Venda venda = buscarParaDetalhe(vendaId, username, admin);
        validarVendaNaoCancelada(venda);

        VendaItem item = buscarItemDaVenda(venda, itemId);
        if (item.getStatus() == StatusVendaItem.CANCELADO) {
            throw new IllegalArgumentException("Este item ja foi cancelado.");
        }

        item.cancelar(username, motivoCancelamento);
        estoqueService.registrarEstornoPorCancelamento(
                item.getProduto(),
                item.getQuantidade(),
                venda.getId(),
                motivoCancelamento,
                username
        );
        venda.atualizarStatusAposCancelamento(username, motivoCancelamento);
    }

    public VendaItem buscarItemParaCancelamento(Long vendaId, Long itemId, String username, boolean admin) {
        Venda venda = buscarParaCancelamento(vendaId, username, admin);
        VendaItem item = buscarItemDaVenda(venda, itemId);
        if (item.getStatus() == StatusVendaItem.CANCELADO) {
            throw new IllegalArgumentException("Este item ja foi cancelado.");
        }
        return item;
    }

    public boolean podeCancelarVenda(Venda venda, String username, boolean admin) {
        return venda.getStatus() != StatusVenda.CANCELADA
                && (admin || venda.getUsuarioResponsavel().equals(username));
    }

    public boolean podeCancelarItem(Venda venda, VendaItem item, String username, boolean admin) {
        return podeCancelarVenda(venda, username, admin)
                && item.getStatus() == StatusVendaItem.CONCLUIDO;
    }

    private void validarFormulario(VendaForm form) {
        if (form.getFormaPagamento() == null) {
            throw new IllegalArgumentException("Selecione a forma de pagamento.");
        }

        if (form.getItens() == null || form.getItens().isEmpty()) {
            throw new IllegalArgumentException("Adicione pelo menos um item.");
        }

        Set<Long> produtoIds = new HashSet<>();
        for (VendaItemForm item : form.getItens()) {
            if (item.getProdutoId() == null) {
                throw new IllegalArgumentException("Selecione o produto de todos os itens.");
            }
            if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
                throw new IllegalArgumentException("A quantidade de todos os itens deve ser maior que zero.");
            }
            if (item.getDescontoUnitario() != null && item.getDescontoUnitario().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("O desconto nao pode ser negativo.");
            }
            if (!produtoIds.add(item.getProdutoId())) {
                throw new IllegalArgumentException("Nao repita o mesmo produto na venda.");
            }
        }
    }

    private Produto buscarProdutoParaVenda(Long produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto nao encontrado."));

        if (!Boolean.TRUE.equals(produto.getAtivo())) {
            throw new IllegalArgumentException("Nao e possivel vender produto inativo.");
        }

        return produto;
    }

    private void validarEstoque(Produto produto, Integer quantidade) {
        if (quantidade > produto.getQuantidadeEstoque()) {
            throw new IllegalArgumentException("Estoque insuficiente para o produto " + produto.getNome() + ".");
        }
    }

    private BigDecimal normalizarDesconto(BigDecimal descontoUnitario) {
        if (descontoUnitario == null) {
            return BigDecimal.ZERO;
        }
        return descontoUnitario;
    }

    private void validarDesconto(Produto produto, BigDecimal descontoUnitario) {
        if (descontoUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O desconto nao pode ser negativo.");
        }

        if (descontoUnitario.compareTo(produto.getPrecoVenda()) > 0) {
            throw new IllegalArgumentException("O desconto nao pode ser maior que o preco do produto " + produto.getNome() + ".");
        }
    }

    private VendaItem buscarItemDaVenda(Venda venda, Long itemId) {
        return venda.getItens().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Item da venda nao encontrado."));
    }

    private void validarPermissaoUsuario(Venda venda, String username, boolean admin) {
        if (!admin && !venda.getUsuarioResponsavel().equals(username)) {
            throw new IllegalArgumentException("Voce nao pode acessar uma venda registrada por outra pessoa.");
        }
    }

    private void validarVendaNaoCancelada(Venda venda) {
        if (venda.getStatus() == StatusVenda.CANCELADA) {
            throw new IllegalArgumentException("Esta venda ja foi cancelada.");
        }
    }

    private record ItemVendaValidado(Produto produto, Integer quantidade, BigDecimal descontoUnitario) {
    }
}
