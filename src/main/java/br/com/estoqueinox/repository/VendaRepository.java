package br.com.estoqueinox.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.estoqueinox.model.StatusVenda;
import br.com.estoqueinox.model.Venda;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    @Query("select v from Venda v join fetch v.produto p order by v.criadoEm desc")
    List<Venda> findAllOrderByCriadoEmDesc();

    @Query("select v from Venda v join fetch v.produto p where v.usuarioResponsavel = :username order by v.criadoEm desc")
    List<Venda> findByUsuarioResponsavelOrderByCriadoEmDesc(@Param("username") String username);

    @Query("select v from Venda v join fetch v.produto p where v.status = :status order by v.criadoEm desc")
    List<Venda> findByStatusOrderByCriadoEmDesc(@Param("status") StatusVenda status);

    @Query("select v from Venda v join fetch v.produto p where v.criadoEm between :inicio and :fim order by v.criadoEm desc")
    List<Venda> findByCriadoEmBetweenOrderByCriadoEmDesc(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}
