package br.com.estoqueinox.config;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Locale;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.estoqueinox.model.Categoria;
import br.com.estoqueinox.model.PerfilUsuario;
import br.com.estoqueinox.model.Produto;
import br.com.estoqueinox.model.Usuario;
import br.com.estoqueinox.repository.CategoriaRepository;
import br.com.estoqueinox.repository.ProdutoRepository;
import br.com.estoqueinox.repository.UsuarioRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(
            CategoriaRepository categoriaRepository,
            ProdutoRepository produtoRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.categoriaRepository = categoriaRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        criarCategoriasIniciais();
        criarProdutosSimulados();
        criarUsuariosIniciais();
    }

    private void criarCategoriasIniciais() {
        criarCategoriaSeNaoExistir("Brincos");
        criarCategoriaSeNaoExistir("Aneis");
        criarCategoriaSeNaoExistir("Colares");
        criarCategoriaSeNaoExistir("Pulseiras");
        criarCategoriaSeNaoExistir("Correntes");
        criarCategoriaSeNaoExistir("Conjuntos");
        criarCategoriaSeNaoExistir("Piercings");
        criarCategoriaSeNaoExistir("Tornozeleiras");
    }

    private void criarProdutosSimulados() {
        criarProdutoSeNaoExistir("BRI-001", "Brinco argola inox dourado pequeno", "Brincos", "8.00", "25.00", 12, 3);
        criarProdutoSeNaoExistir("BRI-002", "Brinco ponto de luz inox prata", "Brincos", "7.00", "20.00", 10, 3);
        criarProdutoSeNaoExistir("BRI-003", "Brinco coracao inox dourado", "Brincos", "9.00", "28.00", 8, 2);
        criarProdutoSeNaoExistir("ANE-001", "Anel inox liso", "Aneis", "6.00", "20.00", 10, 2);
        criarProdutoSeNaoExistir("ANE-002", "Anel inox pedra preta", "Aneis", "10.00", "30.00", 6, 2);
        criarProdutoSeNaoExistir("COL-001", "Colar inox ponto de luz", "Colares", "12.00", "35.00", 8, 2);
        criarProdutoSeNaoExistir("COL-002", "Colar inox corrente veneziana", "Colares", "15.00", "40.00", 5, 2);
        criarProdutoSeNaoExistir("PUL-001", "Pulseira inox elo portugues", "Pulseiras", "13.00", "35.00", 7, 2);
        criarProdutoSeNaoExistir("PUL-002", "Pulseira inox dourada fina", "Pulseiras", "11.00", "30.00", 4, 2);
        criarProdutoSeNaoExistir("COR-001", "Corrente inox masculina", "Correntes", "18.00", "45.00", 5, 2);
        criarProdutoSeNaoExistir("CON-001", "Conjunto colar e brinco inox", "Conjuntos", "22.00", "55.00", 3, 1);
        criarProdutoSeNaoExistir("TOR-001", "Tornozeleira inox coracao", "Tornozeleiras", "8.00", "25.00", 6, 2);
    }

    private void criarUsuariosIniciais() {
        criarUsuarioSeNaoExistir("Administradora", "admin", "admin123", PerfilUsuario.ADMIN);
        criarUsuarioSeNaoExistir("Vendedora Teste", "vendedora", "venda123", PerfilUsuario.VENDEDORA);
    }

    private Categoria criarCategoriaSeNaoExistir(String nome) {
        return buscarCategoriaPorNomeFlexivel(nome)
                .orElseGet(() -> categoriaRepository.save(new Categoria(nome)));
    }

    private void criarProdutoSeNaoExistir(
            String codigo,
            String nome,
            String nomeCategoria,
            String precoCusto,
            String precoVenda,
            Integer quantidadeEstoque,
            Integer estoqueMinimo
    ) {
        if (produtoRepository.existsByCodigo(codigo)) {
            return;
        }

        Categoria categoria = buscarCategoriaPorNomeFlexivel(nomeCategoria)
                .orElseThrow(() -> new IllegalStateException("Categoria nao encontrada: " + nomeCategoria));

        Produto produto = new Produto(
                codigo,
                nome,
                new BigDecimal(precoCusto),
                new BigDecimal(precoVenda),
                quantidadeEstoque,
                estoqueMinimo,
                categoria
        );

        produtoRepository.save(produto);
    }

    private java.util.Optional<Categoria> buscarCategoriaPorNomeFlexivel(String nome) {
        String nomeNormalizado = normalizarTexto(nome);
        return categoriaRepository.findAll().stream()
                .filter(categoria -> normalizarTexto(categoria.getNome()).equals(nomeNormalizado))
                .findFirst();
    }

    private String normalizarTexto(String valor) {
        String semAcentos = Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcentos.trim().toLowerCase(Locale.ROOT);
    }

    private void criarUsuarioSeNaoExistir(
            String nome,
            String username,
            String senha,
            PerfilUsuario perfil
    ) {
        if (usuarioRepository.existsByUsername(username)) {
            return;
        }

        Usuario usuario = new Usuario(
                nome,
                username,
                passwordEncoder.encode(senha),
                perfil,
                true
        );

        usuarioRepository.save(usuario);
    }
}
