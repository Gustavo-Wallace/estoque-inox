package br.com.estoqueinox.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.estoqueinox.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findByNome(String nome);
}
