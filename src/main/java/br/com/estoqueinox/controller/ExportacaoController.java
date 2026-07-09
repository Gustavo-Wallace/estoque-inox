package br.com.estoqueinox.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.estoqueinox.service.ExportacaoService;

@Controller
@RequestMapping("/admin/exportacoes")
public class ExportacaoController {

    private final ExportacaoService exportacaoService;

    public ExportacaoController(ExportacaoService exportacaoService) {
        this.exportacaoService = exportacaoService;
    }

    @GetMapping
    public String index(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            Model model
    ) {
        ExportacaoService.Periodo periodo = exportacaoService.normalizarPeriodo(dataInicial, dataFinal);
        model.addAttribute("periodo", periodo);
        return "admin/exportacoes/index";
    }

    @GetMapping("/produtos")
    public ResponseEntity<byte[]> produtos() {
        return csv(exportacaoService.exportarProdutos());
    }

    @GetMapping("/estoque-baixo")
    public ResponseEntity<byte[]> estoqueBaixo() {
        return csv(exportacaoService.exportarEstoqueBaixo());
    }

    @GetMapping("/vendas")
    public ResponseEntity<byte[]> vendas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal
    ) {
        return csv(exportacaoService.exportarVendas(dataInicial, dataFinal));
    }

    @GetMapping("/movimentacoes-estoque")
    public ResponseEntity<byte[]> movimentacoesEstoque(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal
    ) {
        return csv(exportacaoService.exportarMovimentacoesEstoque(dataInicial, dataFinal));
    }

    @GetMapping("/produtos-mais-vendidos")
    public ResponseEntity<byte[]> produtosMaisVendidos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal
    ) {
        return csv(exportacaoService.exportarProdutosMaisVendidos(dataInicial, dataFinal));
    }

    private ResponseEntity<byte[]> csv(ExportacaoService.ArquivoCsv arquivo) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(arquivo.nomeArquivo())
                        .build()
                        .toString())
                .body(arquivo.conteudo());
    }
}
