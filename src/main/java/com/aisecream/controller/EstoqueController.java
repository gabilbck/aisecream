package com.aisecream.controller;

import com.aisecream.service.EstoqueAtualService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EstoqueController {

    private final EstoqueAtualService estoqueAtualService;

    public EstoqueController(EstoqueAtualService estoqueAtualService) {
        this.estoqueAtualService = estoqueAtualService;
    }

    @GetMapping({"/", "/estoque"})
    public String estoqueAtual(Model model) {
        model.addAttribute("estoque", estoqueAtualService.montarVisaoGeral());
        return "estoque/atual";
    }
}
