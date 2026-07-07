package br.com.estoqueinox.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.estoqueinox.dto.CategoriaForm;
import br.com.estoqueinox.model.Categoria;
import br.com.estoqueinox.service.CategoriaService;
import jakarta.validation.Valid;

@Controller
public class AdminCategoriaController {

    private final CategoriaService categoriaService;

    public AdminCategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/admin/categorias")
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "admin/categorias/lista";
    }

    @GetMapping("/admin/categorias/nova")
    public String nova(Model model) {
        model.addAttribute("categoriaForm", new CategoriaForm());
        model.addAttribute("titulo", "Nova categoria");
        model.addAttribute("formAction", "/admin/categorias");
        return "admin/categorias/form";
    }

    @PostMapping("/admin/categorias")
    public String salvar(
            @Valid @ModelAttribute("categoriaForm") CategoriaForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Nova categoria");
            model.addAttribute("formAction", "/admin/categorias");
            return "admin/categorias/form";
        }

        categoriaService.salvar(form);
        redirectAttributes.addFlashAttribute("sucesso", "Categoria cadastrada com sucesso.");
        return "redirect:/admin/categorias";
    }

    @GetMapping("/admin/categorias/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Categoria categoria = categoriaService.buscarPorId(id);
        model.addAttribute("categoriaForm", CategoriaForm.from(categoria));
        model.addAttribute("titulo", "Editar categoria");
        model.addAttribute("formAction", "/admin/categorias/" + id);
        return "admin/categorias/form";
    }

    @PostMapping("/admin/categorias/{id}")
    public String atualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute("categoriaForm") CategoriaForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Editar categoria");
            model.addAttribute("formAction", "/admin/categorias/" + id);
            return "admin/categorias/form";
        }

        categoriaService.atualizar(id, form);
        redirectAttributes.addFlashAttribute("sucesso", "Categoria atualizada com sucesso.");
        return "redirect:/admin/categorias";
    }

    @PostMapping("/admin/categorias/{id}/alternar-status")
    public String alternarStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoriaService.alternarStatus(id);
        redirectAttributes.addFlashAttribute("sucesso", "Status da categoria atualizado com sucesso.");
        return "redirect:/admin/categorias";
    }
}
