package com.aisecream.repository;

import com.aisecream.model.Loja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LojaRepository extends JpaRepository<Loja, Integer> {

    List<Loja> findAllByOrderByNomeAsc();
}
