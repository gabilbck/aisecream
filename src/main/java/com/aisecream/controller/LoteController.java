package com.aisecream.controller;

import com.aisecream.dto.LoteProducaoForm;
import com.aisecream.service.LoteService;
import com.aisecream.service.SaborService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/lotes")
public class LoteController {

    private final LoteService loteService;
    private final SaborService saborService;

    public LoteController(LoteService loteService, SaborService saborService) {
        this.loteService = loteService;
        this.saborService = saborService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lotes", loteService.listarTodos());
        return "lote/listar";
    }

    @GetMapping("/novo")
    @PreAuthorize("hasRole('ADMIN')")
    public String novoForm(Model model) {
        LoteProducaoForm form = new LoteProducaoForm();
        form.setDataProducao(LocalDate.now());
        model.addAttribute("loteForm", form);
        model.addAttribute("sabores", saborService.listarAtivos());
        return "lote/form";
    }

    @PostMapping("/novo")
    @PreAuthorize("hasRole('ADMIN')")
    public String salvar(@Valid @ModelAttribute("loteForm") LoteProducaoForm form,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("sabores", saborService.listarAtivos());
            return "lote/form";
        }
        try {
            loteService.criar(form);
            redirectAttributes.addFlashAttribute("sucesso", "Lote de produção registrado com sucesso.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/lotes";
    }
}
