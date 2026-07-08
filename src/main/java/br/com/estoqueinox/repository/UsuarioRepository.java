package br.com.estoqueinox.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.estoqueinox.model.PerfilUsuario;
import br.com.estoqueinox.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long id);

    long countByPerfilAndAtivoTrue(PerfilUsuario perfil);

    List<Usuario> findAllByOrderByNomeAsc();

    List<Usuario> findByAtivoTrueOrderByNomeAsc();

    List<Usuario> findByPerfilAndAtivoTrueOrderByNomeAsc(PerfilUsuario perfil);
}
