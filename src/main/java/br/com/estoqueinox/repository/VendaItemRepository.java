package br.com.estoqueinox.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.estoqueinox.model.VendaItem;

public interface VendaItemRepository extends JpaRepository<VendaItem, Long> {
}
