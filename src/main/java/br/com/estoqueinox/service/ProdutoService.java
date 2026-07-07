package br.com.estoqueinox.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import br.com.estoqueinox.dto.ProdutoForm;
import br.com.estoqueinox.model.Categoria;
import br.com.estoqueinox.model.Produto;
import br.com.estoqueinox.repository.CategoriaRepository;
import br.com.estoqueinox.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Produto> listarTodosComCategoria() {
        return produtoRepository.findAllWithCategoriaOrderByNome();
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto nao encontrado."));
    }

    public List<Categoria> listarCategoriasParaNovoProduto() {
        return categoriaRepository.findByAtivaTrueOrderByNomeAsc();
    }

    public List<Categoria> listarCategoriasParaEdicao(Produto produto) {
        List<Categoria> categorias = new ArrayList<>(categoriaRepository.findByAtivaTrueOrderByNomeAsc());
        Categoria atual = produto.getCategoria();

        boolean contemAtual = categorias.stream()
                .anyMatch(categoria -> categoria.getId().equals(atual.getId()));

        if (!contemAtual) {
            categorias.add(atual);
        }

        return categorias;
    }

    public void validarFormulario(ProdutoForm form, Long produtoId, BindingResult result) {
        String codigo = normalizarCodigo(form.getCodigo());
        if (!codigo.isBlank()) {
            boolean codigoEmUso = produtoId == null
                    ? produtoRepository.existsByCodigo(codigo)
                    : produtoRepository.existsByCodigoAndIdNot(codigo, produtoId);

            if (codigoEmUso) {
                result.rejectValue("codigo", "produto.codigo.duplicado", "Ja existe um produto com este codigo.");
            }
        }

        if (form.getCategoriaId() == null) {
            return;
        }

        Categoria categoria = categoriaRepository.findById(form.getCategoriaId()).orElse(null);
        if (categoria == null) {
            result.rejectValue("categoriaId", "produto.categoria.invalida", "Categoria invalida.");
            return;
        }

        if (produtoId == null && !Boolean.TRUE.equals(categoria.getAtiva())) {
            result.rejectValue("categoriaId", "produto.categoria.inativa", "Selecione uma categoria ativa.");
            return;
        }

        if (produtoId != null) {
            Produto produto = buscarPorId(produtoId);
            boolean categoriaAtual = produto.getCategoria().getId().equals(categoria.getId());
            if (!categoriaAtual && !Boolean.TRUE.equals(categoria.getAtiva())) {
                result.rejectValue("categoriaId", "produto.categoria.inativa", "Selecione uma categoria ativa.");
            }
        }
    }

    @Transactional
    public Produto salvar(ProdutoForm form) {
        Produto produto = new Produto();
        aplicarFormulario(produto, form, true);
        return produtoRepository.save(produto);
    }

    @Transactional
    public Produto atualizar(Long id, ProdutoForm form) {
        Produto produto = buscarPorId(id);
        aplicarFormulario(produto, form, false);
        return produto;
    }

    @Transactional
    public void alternarStatus(Long id) {
        Produto produto = buscarPorId(id);
        produto.setAtivo(!Boolean.TRUE.equals(produto.getAtivo()));
    }

    private void aplicarFormulario(Produto produto, ProdutoForm form, boolean atualizarEstoque) {
        Categoria categoria = categoriaRepository.findById(form.getCategoriaId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria nao encontrada."));

        produto.setCodigo(normalizarCodigo(form.getCodigo()));
        produto.setNome(form.getNome().trim());
        produto.setDescricao(form.getDescricao());
        produto.setCategoria(categoria);
        produto.setPrecoCusto(form.getPrecoCusto());
        produto.setPrecoVenda(form.getPrecoVenda());
        // Para produtos existentes, o estoque deve ser alterado pelas telas de movimentacao.
        if (atualizarEstoque) {
            produto.setQuantidadeEstoque(form.getQuantidadeEstoque());
        }
        produto.setEstoqueMinimo(form.getEstoqueMinimo());
        produto.setAtivo(Boolean.TRUE.equals(form.getAtivo()));
    }

    private String normalizarCodigo(String codigo) {
        if (codigo == null) {
            return "";
        }
        return codigo.trim().toUpperCase();
    }
}
