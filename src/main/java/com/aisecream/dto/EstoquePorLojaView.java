package com.aisecream.dto;

import com.aisecream.model.Loja;
import lombok.Getter;

import java.util.List;

@Getter
public class EstoquePorLojaView {

    private final Loja loja;
    private final List<LoteSaldoNaLojaView> itens;
    private final int totalUnidades;

    public EstoquePorLojaView(Loja loja, List<LoteSaldoNaLojaView> itens) {
        this.loja = loja;
        this.itens = List.copyOf(itens);
        this.totalUnidades = itens.stream().mapToInt(LoteSaldoNaLojaView::getSaldoDisponivel).sum();
    }
}
