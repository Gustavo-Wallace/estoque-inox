package br.com.estoqueinox.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.estoqueinox.repository.ProdutoRepository;

@Controller
public class ProdutoController {

    private final ProdutoRepository produtoRepository;

    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @GetMapping("/produtos")
    public String listar(Model model) {
        model.addAttribute("produtos", produtoRepository.findAllWithCategoriaOrderByNome());
        return "produtos";
    }
}
