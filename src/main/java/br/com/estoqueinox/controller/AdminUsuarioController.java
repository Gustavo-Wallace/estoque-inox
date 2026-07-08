package br.com.estoqueinox.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.estoqueinox.dto.AlterarSenhaUsuarioForm;
import br.com.estoqueinox.dto.UsuarioForm;
import br.com.estoqueinox.model.PerfilUsuario;
import br.com.estoqueinox.model.Usuario;
import br.com.estoqueinox.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@Controller
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    public AdminUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/admin/usuarios")
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "admin/usuarios/lista";
    }

    @GetMapping("/admin/usuarios/novo")
    public String novo(Model model) {
        model.addAttribute("usuarioForm", new UsuarioForm());
        adicionarAtributosFormulario(model, "Novo usuario", "/admin/usuarios", true);
        return "admin/usuarios/form";
    }

    @PostMapping("/admin/usuarios")
    public String salvar(
            @Valid @ModelAttribute("usuarioForm") UsuarioForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (!result.hasErrors()) {
            try {
                usuarioService.criar(form);
                redirectAttributes.addFlashAttribute("sucesso", "Usuario cadastrado com sucesso.");
                return "redirect:/admin/usuarios";
            } catch (IllegalArgumentException ex) {
                result.reject("usuario.invalido", ex.getMessage());
            }
        }

        adicionarAtributosFormulario(model, "Novo usuario", "/admin/usuarios", true);
        return "admin/usuarios/form";
    }

    @GetMapping("/admin/usuarios/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id);
            model.addAttribute("usuarioForm", UsuarioForm.from(usuario));
            adicionarAtributosFormulario(model, "Editar usuario", "/admin/usuarios/" + id, false);
            return "admin/usuarios/form";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    @PostMapping("/admin/usuarios/{id}")
    public String atualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute("usuarioForm") UsuarioForm form,
            BindingResult result,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (!result.hasErrors()) {
            try {
                usuarioService.atualizar(id, form, authentication.getName());
                redirectAttributes.addFlashAttribute("sucesso", "Usuario atualizado com sucesso.");
                return "redirect:/admin/usuarios";
            } catch (IllegalArgumentException ex) {
                result.reject("usuario.invalido", ex.getMessage());
            } catch (EntityNotFoundException ex) {
                redirectAttributes.addFlashAttribute("erro", ex.getMessage());
                return "redirect:/admin/usuarios";
            }
        }

        adicionarAtributosFormulario(model, "Editar usuario", "/admin/usuarios/" + id, false);
        return "admin/usuarios/form";
    }

    @GetMapping("/admin/usuarios/{id}/senha")
    public String senha(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id);
            model.addAttribute("usuario", usuario);
            model.addAttribute("alterarSenhaUsuarioForm", new AlterarSenhaUsuarioForm());
            return "admin/usuarios/senha";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    @PostMapping("/admin/usuarios/{id}/senha")
    public String alterarSenha(
            @PathVariable Long id,
            @Valid @ModelAttribute("alterarSenhaUsuarioForm") AlterarSenhaUsuarioForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Usuario usuario;
        try {
            usuario = usuarioService.buscarPorId(id);
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/admin/usuarios";
        }

        if (result.hasErrors()) {
            model.addAttribute("usuario", usuario);
            return "admin/usuarios/senha";
        }

        usuarioService.alterarSenha(id, form.getNovaSenha());
        redirectAttributes.addFlashAttribute("sucesso", "Senha alterada com sucesso.");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/admin/usuarios/{id}/alternar-status")
    public String alternarStatus(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            usuarioService.alternarStatus(id, authentication.getName());
            redirectAttributes.addFlashAttribute("sucesso", "Status do usuario atualizado com sucesso.");
        } catch (IllegalArgumentException | EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }

        return "redirect:/admin/usuarios";
    }

    private void adicionarAtributosFormulario(Model model, String titulo, String formAction, boolean novo) {
        model.addAttribute("titulo", titulo);
        model.addAttribute("formAction", formAction);
        model.addAttribute("novo", novo);
        model.addAttribute("perfis", PerfilUsuario.values());
    }
}
