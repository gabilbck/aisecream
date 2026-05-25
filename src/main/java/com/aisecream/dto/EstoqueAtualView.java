package com.aisecream.dto;

import com.aisecream.model.LoteProducao;
import lombok.Getter;

import java.util.List;

@Getter
public class EstoqueAtualView {

    private final List<LoteProducao> estoqueCd;
    private final List<EstoquePorLojaView> estoqueLojas;
    private final int totalUnidadesCd;
    private final int totalUnidadesLojas;

    public EstoqueAtualView(List<LoteProducao> estoqueCd, List<EstoquePorLojaView> estoqueLojas) {
        this.estoqueCd = List.copyOf(estoqueCd);
        this.estoqueLojas = List.copyOf(estoqueLojas);
        this.totalUnidadesCd = estoqueCd.stream().mapToInt(LoteProducao::getQuantidadeDisponivel).sum();
        this.totalUnidadesLojas = estoqueLojas.stream().mapToInt(EstoquePorLojaView::getTotalUnidades).sum();
    }

    public int getTotalGeral() {
        return totalUnidadesCd + totalUnidadesLojas;
    }
}
