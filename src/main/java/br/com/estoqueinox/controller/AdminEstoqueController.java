package br.com.estoqueinox.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.estoqueinox.dto.AjusteEstoqueForm;
import br.com.estoqueinox.dto.EntradaEstoqueForm;
import br.com.estoqueinox.model.Produto;
import br.com.estoqueinox.service.EstoqueService;
import br.com.estoqueinox.service.ProdutoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@Controller
public class AdminEstoqueController {

    private final EstoqueService estoqueService;
    private final ProdutoService produtoService;

    public AdminEstoqueController(EstoqueService estoqueService, ProdutoService produtoService) {
        this.estoqueService = estoqueService;
        this.produtoService = produtoService;
    }

    @GetMapping("/admin/estoque")
    public String listar(Model model) {
        model.addAttribute("movimentacoes", estoqueService.listarMovimentacoes());
        return "admin/estoque/lista";
    }

    @GetMapping("/admin/estoque/entrada")
    public String entrada(@RequestParam(required = false) Long produtoId, Model model) {
        EntradaEstoqueForm form = new EntradaEstoqueForm();
        form.setProdutoId(produtoId);
        model.addAttribute("entradaEstoqueForm", form);
        adicionarProdutos(model);
        return "admin/estoque/entrada";
    }

    @PostMapping("/admin/estoque/entrada")
    public String registrarEntrada(
            @Valid @ModelAttribute("entradaEstoqueForm") EntradaEstoqueForm form,
            BindingResult result,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            adicionarProdutos(model);
            return "admin/estoque/entrada";
        }

        try {
            estoqueService.registrarEntrada(
                    form.getProdutoId(),
                    form.getQuantidade(),
                    form.getObservacao(),
                    authentication.getName()
            );
            redirectAttributes.addFlashAttribute("sucesso", "Entrada de estoque registrada com sucesso.");
            return "redirect:/admin/estoque";
        } catch (IllegalArgumentException | EntityNotFoundException ex) {
            result.reject("estoque.entrada.invalida", ex.getMessage());
            adicionarProdutos(model);
            return "admin/estoque/entrada";
        }
    }

    @GetMapping("/admin/estoque/ajuste")
    public String ajuste(@RequestParam(required = false) Long produtoId, Model model) {
        AjusteEstoqueForm form = new AjusteEstoqueForm();
        form.setProdutoId(produtoId);
        model.addAttribute("ajusteEstoqueForm", form);
        adicionarProdutos(model);
        return "admin/estoque/ajuste";
    }

    @PostMapping("/admin/estoque/ajuste")
    public String registrarAjuste(
            @Valid @ModelAttribute("ajusteEstoqueForm") AjusteEstoqueForm form,
            BindingResult result,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            adicionarProdutos(model);
            return "admin/estoque/ajuste";
        }

        try {
            estoqueService.registrarAjuste(
                    form.getProdutoId(),
                    form.getNovaQuantidade(),
                    form.getObservacao(),
                    authentication.getName()
            );
            redirectAttributes.addFlashAttribute("sucesso", "Ajuste de estoque registrado com sucesso.");
            return "redirect:/admin/estoque";
        } catch (IllegalArgumentException | EntityNotFoundException ex) {
            result.reject("estoque.ajuste.invalido", ex.getMessage());
            adicionarProdutos(model);
            return "admin/estoque/ajuste";
        }
    }

    @GetMapping("/admin/produtos/{id}/movimentacoes")
    public String historicoProduto(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Produto produto = produtoService.buscarPorId(id);
            model.addAttribute("produto", produto);
            model.addAttribute("movimentacoes", estoqueService.listarMovimentacoesDoProduto(id));
            return "admin/estoque/historico-produto";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/admin/estoque";
        }
    }

    private void adicionarProdutos(Model model) {
        model.addAttribute("produtos", produtoService.listarTodosComCategoria());
    }
}
