package com.aisecream.controller;

import com.aisecream.dto.BaixaEstoqueForm;
import com.aisecream.model.Loja;
import com.aisecream.service.BaixaEstoqueService;
import com.aisecream.service.LojaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/baixas")
public class BaixaEstoqueController {

    private final BaixaEstoqueService baixaEstoqueService;
    private final LojaService lojaService;

    public BaixaEstoqueController(BaixaEstoqueService baixaEstoqueService, LojaService lojaService) {
        this.baixaEstoqueService = baixaEstoqueService;
        this.lojaService = lojaService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Integer lojaId, Model model) {
        model.addAttribute("baixas", baixaEstoqueService.listar(lojaId));
        model.addAttribute("lojasFiltro", lojaService.listarTodos());
        model.addAttribute("lojaIdFiltro", lojaId);
        return "baixa/listar";
    }

    @GetMapping("/novo")
    public String novoForm(
            @RequestParam(required = false) Integer lojaId,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        model.addAttribute("lojasAtivas", lojaService.listarAtivas());
        if (lojaId == null) {
            return "baixa/form";
        }
        try {
            Loja loja = lojaService.buscarPorId(lojaId);
            if (!loja.isAtivo()) {
                redirectAttributes.addFlashAttribute("erro", "Somente lojas ativas podem registrar baixa.");
                return "redirect:/baixas/novo";
            }
            var lotesSaldo = baixaEstoqueService.listarLotesComSaldoNaLoja(lojaId);
            BaixaEstoqueForm form = new BaixaEstoqueForm();
            form.setLojaId(lojaId);
            model.addAttribute("baixaForm", form);
            model.addAttribute("lotesSaldo", lotesSaldo);
            model.addAttribute("lojaNome", loja.getNome());
            return "baixa/form";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", "Loja não encontrada.");
            return "redirect:/baixas/novo";
        }
    }

    @PostMapping("/novo")
    public String salvar(@Valid @ModelAttribute("baixaForm") BaixaEstoqueForm form,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            enriquecerModeloFormulario(model, form);
            return "baixa/form";
        }
        try {
            baixaEstoqueService.registrar(form);
            redirectAttributes.addFlashAttribute("sucesso", "Baixa de estoque registrada com sucesso.");
            return "redirect:/baixas";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/baixas/novo?lojaId=" + form.getLojaId();
        }
    }

    private void enriquecerModeloFormulario(Model model, BaixaEstoqueForm form) {
        model.addAttribute("lojasAtivas", lojaService.listarAtivas());
        if (form.getLojaId() != null) {
            try {
                Loja loja = lojaService.buscarPorId(form.getLojaId());
                model.addAttribute("lojaNome", loja.getNome());
            } catch (IllegalArgumentException ignored) {
                // mantém formulário sem nome da loja
            }
            model.addAttribute("lotesSaldo", baixaEstoqueService.listarLotesComSaldoNaLoja(form.getLojaId()));
        }
    }
}
