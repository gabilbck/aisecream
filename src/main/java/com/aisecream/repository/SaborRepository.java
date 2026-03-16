package com.aisecream.repository;

import com.aisecream.model.Sabor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaborRepository extends JpaRepository<Sabor, Integer> {

    List<Sabor> findAllByOrderByNomeAsc();

    List<Sabor> findByAtivoTrueOrderByNomeAsc();

    boolean existsByNomeIgnoreCase(String nome);
}
