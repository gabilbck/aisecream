package com.aisecream.controller;

import com.aisecream.dto.DistribuicaoForm;
import com.aisecream.service.DistribuicaoService;
import com.aisecream.service.LojaService;
import com.aisecream.service.LoteService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/distribuicoes")
public class DistribuicaoController {

    private final DistribuicaoService distribuicaoService;
    private final LoteService loteService;
    private final LojaService lojaService;

    public DistribuicaoController(
            DistribuicaoService distribuicaoService,
            LoteService loteService,
            LojaService lojaService
    ) {
        this.distribuicaoService = distribuicaoService;
        this.loteService = loteService;
        this.lojaService = lojaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("distribuicoes", distribuicaoService.listarTodos());
        return "distribuicao/listar";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("distribuicaoForm", new DistribuicaoForm());
        model.addAttribute("lotes", loteService.listarComEstoqueDisponivel());
        model.addAttribute("lojas", lojaService.listarAtivas());
        return "distribuicao/form";
    }

    @PostMapping("/novo")
    public String salvar(@Valid @ModelAttribute("distribuicaoForm") DistribuicaoForm form,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("lotes", loteService.listarComEstoqueDisponivel());
            model.addAttribute("lojas", lojaService.listarAtivas());
            return "distribuicao/form";
        }
        try {
            distribuicaoService.registrar(form);
            redirectAttributes.addFlashAttribute("sucesso", "Distribuição registrada com sucesso.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/distribuicoes";
    }

    @PostMapping("/cancelar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String cancelar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            distribuicaoService.cancelar(id);
            redirectAttributes.addFlashAttribute("sucesso", "Distribuição cancelada e estoque do lote reintegrado.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/distribuicoes";
    }
}
