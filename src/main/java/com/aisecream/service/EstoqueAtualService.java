package com.aisecream.service;

import com.aisecream.dto.EstoqueAtualView;
import com.aisecream.dto.EstoquePorLojaView;
import com.aisecream.dto.LoteSaldoNaLojaView;
import com.aisecream.model.Loja;
import com.aisecream.model.LoteProducao;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class EstoqueAtualService {

    private final LoteService loteService;
    private final LojaService lojaService;
    private final BaixaEstoqueService baixaEstoqueService;

    public EstoqueAtualService(
            LoteService loteService,
            LojaService lojaService,
            BaixaEstoqueService baixaEstoqueService
    ) {
        this.loteService = loteService;
        this.lojaService = lojaService;
        this.baixaEstoqueService = baixaEstoqueService;
    }

    public EstoqueAtualView montarVisaoGeral() {
        List<LoteProducao> estoqueCd = loteService.listarComEstoqueDisponivel();
        estoqueCd.sort(Comparator
                .comparing((LoteProducao l) -> l.getSabor().getNome(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(LoteProducao::getDataProducao, Comparator.reverseOrder()));

        List<EstoquePorLojaView> estoqueLojas = new ArrayList<>();
        for (Loja loja : lojaService.listarAtivas()) {
            List<LoteSaldoNaLojaView> itens = baixaEstoqueService.listarLotesComSaldoNaLoja(loja.getId());
            if (!itens.isEmpty()) {
                estoqueLojas.add(new EstoquePorLojaView(loja, itens));
            }
        }
        estoqueLojas.sort(Comparator.comparing(v -> v.getLoja().getNome(), String.CASE_INSENSITIVE_ORDER));

        return new EstoqueAtualView(estoqueCd, estoqueLojas);
    }
}
