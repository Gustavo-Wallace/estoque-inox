package br.com.estoqueinox.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.estoqueinox.dto.ProdutoForm;
import br.com.estoqueinox.model.Produto;
import br.com.estoqueinox.service.ProdutoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@Controller
public class AdminProdutoController {

    private final ProdutoService produtoService;

    public AdminProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/admin/produtos")
    public String listar(Model model) {
        model.addAttribute("produtos", produtoService.listarTodosComCategoria());
        return "admin/produtos/lista";
    }

    @GetMapping("/admin/produtos/novo")
    public String novo(Model model) {
        model.addAttribute("produtoForm", new ProdutoForm());
        model.addAttribute("categorias", produtoService.listarCategoriasParaNovoProduto());
        model.addAttribute("titulo", "Novo produto");
        model.addAttribute("formAction", "/admin/produtos");
        model.addAttribute("edicao", false);
        return "admin/produtos/form";
    }

    @PostMapping("/admin/produtos")
    public String salvar(
            @Valid @ModelAttribute("produtoForm") ProdutoForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        produtoService.validarFormulario(form, null, result);

        if (result.hasErrors()) {
            model.addAttribute("categorias", produtoService.listarCategoriasParaNovoProduto());
            model.addAttribute("titulo", "Novo produto");
            model.addAttribute("formAction", "/admin/produtos");
            model.addAttribute("edicao", false);
            return "admin/produtos/form";
        }

        try {
            produtoService.salvar(form);
            redirectAttributes.addFlashAttribute("sucesso", "Produto cadastrado com sucesso.");
            return "redirect:/admin/produtos";
        } catch (IllegalArgumentException | EntityNotFoundException ex) {
            result.reject("produto.invalido", ex.getMessage());
            model.addAttribute("categorias", produtoService.listarCategoriasParaNovoProduto());
            model.addAttribute("titulo", "Novo produto");
            model.addAttribute("formAction", "/admin/produtos");
            model.addAttribute("edicao", false);
            return "admin/produtos/form";
        }
    }

    @GetMapping("/admin/produtos/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Produto produto = produtoService.buscarPorId(id);
            model.addAttribute("produtoForm", ProdutoForm.from(produto));
            model.addAttribute("categorias", produtoService.listarCategoriasParaEdicao(produto));
            model.addAttribute("titulo", "Editar produto");
            model.addAttribute("formAction", "/admin/produtos/" + id);
            model.addAttribute("edicao", true);
            return "admin/produtos/form";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/admin/produtos";
        }
    }

    @PostMapping("/admin/produtos/{id}")
    public String atualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute("produtoForm") ProdutoForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Produto produto;
        try {
            produto = produtoService.buscarPorId(id);
            produtoService.validarFormulario(form, id, result);
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/admin/produtos";
        }

        if (result.hasErrors()) {
            model.addAttribute("categorias", produtoService.listarCategoriasParaEdicao(produto));
            model.addAttribute("titulo", "Editar produto");
            model.addAttribute("formAction", "/admin/produtos/" + id);
            model.addAttribute("edicao", true);
            return "admin/produtos/form";
        }

        try {
            produtoService.atualizar(id, form);
            redirectAttributes.addFlashAttribute("sucesso", "Produto atualizado com sucesso.");
            return "redirect:/admin/produtos";
        } catch (IllegalArgumentException ex) {
            result.reject("produto.invalido", ex.getMessage());
            model.addAttribute("categorias", produtoService.listarCategoriasParaEdicao(produto));
            model.addAttribute("titulo", "Editar produto");
            model.addAttribute("formAction", "/admin/produtos/" + id);
            model.addAttribute("edicao", true);
            return "admin/produtos/form";
        }
    }

    @PostMapping("/admin/produtos/{id}/alternar-status")
    public String alternarStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produtoService.alternarStatus(id);
            redirectAttributes.addFlashAttribute("sucesso", "Status do produto atualizado com sucesso.");
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/admin/produtos";
    }
}
