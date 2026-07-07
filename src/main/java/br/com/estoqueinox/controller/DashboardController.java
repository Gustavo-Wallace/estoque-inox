package br.com.estoqueinox.controller;

import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        addUserInfo(authentication, model);
        return "dashboard";
    }

    @GetMapping("/admin")
    public String admin(Authentication authentication, Model model) {
        addUserInfo(authentication, model);
        return "admin";
    }

    private void addUserInfo(Authentication authentication, Model model) {
        String roles = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.joining(", "));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("username", authentication.getName());
        model.addAttribute("roles", roles);
        model.addAttribute("isAdmin", isAdmin);
    }
}
