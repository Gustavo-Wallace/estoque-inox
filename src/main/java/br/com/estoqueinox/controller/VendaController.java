package br.com.estoqueinox.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.estoqueinox.dto.VendaForm;
import br.com.estoqueinox.model.FormaPagamento;
import br.com.estoqueinox.service.VendaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@Controller
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @GetMapping("/vendas")
    public String listar(Authentication authentication, Model model) {
        boolean admin = isAdmin(authentication);
        model.addAttribute("vendas", admin
                ? vendaService.listarTodas()
                : vendaService.listarPorUsuario(authentication.getName()));
        model.addAttribute("isAdmin", admin);
        return "vendas";
    }

    @GetMapping("/vendas/nova")
    public String nova(Model model) {
        model.addAttribute("vendaForm", new VendaForm());
        adicionarOpcoesFormulario(model);
        return "venda-form";
    }

    @PostMapping("/vendas")
    public String salvar(
            @Valid @ModelAttribute("vendaForm") VendaForm form,
            BindingResult result,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            adicionarOpcoesFormulario(model);
            return "venda-form";
        }

        try {
            vendaService.registrarVenda(
                    form.getProdutoId(),
                    form.getQuantidade(),
                    form.getFormaPagamento(),
                    authentication.getName()
            );
            redirectAttributes.addFlashAttribute("sucesso", "Venda registrada com sucesso.");
            return "redirect:/vendas";
        } catch (IllegalArgumentException | EntityNotFoundException ex) {
            result.reject("venda.invalida", ex.getMessage());
            adicionarOpcoesFormulario(model);
            return "venda-form";
        }
    }

    private void adicionarOpcoesFormulario(Model model) {
        model.addAttribute("produtos", vendaService.listarProdutosDisponiveis());
        model.addAttribute("formasPagamento", FormaPagamento.values());
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
