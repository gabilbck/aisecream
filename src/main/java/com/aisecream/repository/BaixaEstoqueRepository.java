package com.aisecream.repository;

import com.aisecream.model.BaixaEstoque;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BaixaEstoqueRepository extends JpaRepository<BaixaEstoque, Integer> {

    @EntityGraph(attributePaths = {"loja", "lote", "lote.sabor", "criadoPor"})
    @Override
    Optional<BaixaEstoque> findById(Integer id);

    @EntityGraph(attributePaths = {"loja", "lote", "lote.sabor", "criadoPor"})
    List<BaixaEstoque> findAllByOrderByCriadoEmDescIdDesc();

    @EntityGraph(attributePaths = {"loja", "lote", "lote.sabor", "criadoPor"})
    List<BaixaEstoque> findByLoja_IdOrderByCriadoEmDescIdDesc(Integer lojaId);

    @Query("SELECT COALESCE(SUM(b.quantidade), 0) FROM BaixaEstoque b WHERE b.loja.id = :lojaId AND b.lote.id = :loteId")
    long sumQuantidadeByLojaAndLote(@Param("lojaId") Integer lojaId, @Param("loteId") Integer loteId);
}
