package com.aisecream.repository;

import com.aisecream.model.Distribuicao;
import com.aisecream.model.enums.StatusDistribuicao;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistribuicaoRepository extends JpaRepository<Distribuicao, Integer> {

    @EntityGraph(attributePaths = {"lote", "lote.sabor", "loja", "criadoPor"})
    @Override
    Optional<Distribuicao> findById(Integer id);

    @EntityGraph(attributePaths = {"lote", "lote.sabor", "loja", "criadoPor"})
    List<Distribuicao> findAllByOrderByDistribuidoEmDescIdDesc();

    @Query("SELECT COALESCE(SUM(d.quantidade), 0) FROM Distribuicao d WHERE d.loja.id = :lojaId AND d.lote.id = :loteId AND d.status = :status")
    long sumQuantidadeByLojaAndLoteAndStatus(
            @Param("lojaId") Integer lojaId,
            @Param("loteId") Integer loteId,
            @Param("status") StatusDistribuicao status
    );

    @Query("SELECT DISTINCT d.lote.id FROM Distribuicao d WHERE d.loja.id = :lojaId AND d.status = :status")
    List<Integer> findDistinctLoteIdsByLojaIdAndStatus(
            @Param("lojaId") Integer lojaId,
            @Param("status") StatusDistribuicao status
    );
}
