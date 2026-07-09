package br.com.estoqueinox.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import br.com.estoqueinox.model.MovimentacaoEstoque;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    @Query("select m from MovimentacaoEstoque m join fetch m.produto p order by m.criadoEm desc")
    List<MovimentacaoEstoque> findAllOrderByCriadoEmDesc();

    @Query("select m from MovimentacaoEstoque m join fetch m.produto p where p.id = :produtoId order by m.criadoEm desc")
    List<MovimentacaoEstoque> findByProdutoIdOrderByCriadoEmDesc(@Param("produtoId") Long produtoId);

    @Query("select m from MovimentacaoEstoque m join fetch m.produto p where m.criadoEm between :inicio and :fim order by m.criadoEm desc")
    List<MovimentacaoEstoque> findByCriadoEmBetweenOrderByCriadoEmDesc(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}
