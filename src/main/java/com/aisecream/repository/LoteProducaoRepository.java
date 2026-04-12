package com.aisecream.repository;

import com.aisecream.model.LoteProducao;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoteProducaoRepository extends JpaRepository<LoteProducao, Integer> {

    @EntityGraph(attributePaths = {"sabor", "criadoPor"})
    @Override
    Optional<LoteProducao> findById(Integer id);

    @EntityGraph(attributePaths = {"sabor", "criadoPor"})
    List<LoteProducao> findAllByOrderByDataProducaoDescIdDesc();

    @EntityGraph(attributePaths = {"sabor", "criadoPor"})
    List<LoteProducao> findByQuantidadeDisponivelGreaterThanOrderByDataProducaoDescIdDesc(int minDisponivel);
}
