package br.com.estoqueinox.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.estoqueinox.service.RelatorioService;

@Controller
@RequestMapping("/admin/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping
    public String resumo(Model model) {
        model.addAttribute("resumo", relatorioService.resumoDoDia());
        return "admin/relatorios/index";
    }

    @GetMapping("/vendas")
    public String vendas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            Model model
    ) {
        model.addAttribute("relatorio", relatorioService.vendasPorPeriodo(dataInicial, dataFinal));
        return "admin/relatorios/vendas";
    }

    @GetMapping("/produtos-mais-vendidos")
    public String produtosMaisVendidos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            Model model
    ) {
        RelatorioService.Periodo periodo = relatorioService.normalizarPeriodo(dataInicial, dataFinal);
        model.addAttribute("periodo", periodo);
        model.addAttribute("produtos", relatorioService.produtosMaisVendidos(dataInicial, dataFinal));
        return "admin/relatorios/produtos-mais-vendidos";
    }

    @GetMapping("/estoque-baixo")
    public String estoqueBaixo(Model model) {
        model.addAttribute("produtos", relatorioService.produtosComEstoqueBaixo());
        return "admin/relatorios/estoque-baixo";
    }

    @GetMapping("/vendedoras")
    public String vendedoras(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            Model model
    ) {
        RelatorioService.Periodo periodo = relatorioService.normalizarPeriodo(dataInicial, dataFinal);
        model.addAttribute("periodo", periodo);
        model.addAttribute("vendedoras", relatorioService.vendasPorVendedora(dataInicial, dataFinal));
        return "admin/relatorios/vendedoras";
    }
}
