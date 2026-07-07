package br.com.estoqueinox.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.estoqueinox.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Optional<Produto> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    List<Produto> findByAtivoTrueOrderByNomeAsc();

    @Query("select p from Produto p join fetch p.categoria order by p.nome")
    List<Produto> findAllWithCategoriaOrderByNome();

    @Query("select p from Produto p join fetch p.categoria where p.ativo = true order by p.nome")
    List<Produto> findAtivosWithCategoriaOrderByNome();

    @Query("select p from Produto p join fetch p.categoria where p.ativo = true and p.quantidadeEstoque > 0 order by p.nome")
    List<Produto> findAtivosComEstoqueWithCategoriaOrderByNome();

    @Query("select p from Produto p where p.quantidadeEstoque <= p.estoqueMinimo order by p.nome")
    List<Produto> findComEstoqueBaixo();
}
