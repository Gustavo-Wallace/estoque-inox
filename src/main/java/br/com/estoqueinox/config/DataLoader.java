package br.com.estoqueinox.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
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
@Profile("dev")
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
        criarCategoriaSeNaoExistir("Brincos");
        criarCategoriaSeNaoExistir("Anéis");
        criarCategoriaSeNaoExistir("Colares");
        criarCategoriaSeNaoExistir("Pulseiras");
        criarCategoriaSeNaoExistir("Correntes");

        criarProdutoSeNaoExistir(
                "BRI-001",
                "Brinco argola inox dourado",
                "Brincos",
                "8.00",
                "25.00",
                12,
                3
        );
        criarProdutoSeNaoExistir(
                "ANE-001",
                "Anel inox liso",
                "Anéis",
                "6.00",
                "20.00",
                10,
                2
        );
        criarProdutoSeNaoExistir(
                "COL-001",
                "Colar inox ponto de luz",
                "Colares",
                "12.00",
                "35.00",
                8,
                2
        );

        criarUsuarioSeNaoExistir(
                "Administradora",
                "admin",
                "admin123",
                PerfilUsuario.ADMIN
        );
        criarUsuarioSeNaoExistir(
                "Vendedora Teste",
                "vendedora",
                "venda123",
                PerfilUsuario.VENDEDORA
        );
    }

    private void criarCategoriaSeNaoExistir(String nome) {
        categoriaRepository.findByNome(nome)
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

        Categoria categoria = categoriaRepository.findByNome(nomeCategoria)
                .orElseThrow(() -> new IllegalStateException("Categoria não encontrada: " + nomeCategoria));

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
