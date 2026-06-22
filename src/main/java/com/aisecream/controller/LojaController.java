package com.aisecream.controller;

import com.aisecream.model.Loja;
import com.aisecream.service.LojaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/lojas")
public class LojaController {

    private final LojaService lojaService;

    public LojaController(LojaService lojaService) {
        this.lojaService = lojaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lojas", lojaService.listarTodos());
        return "loja/listar";
    }

    @GetMapping("/novo")
    @PreAuthorize("hasRole('ADMIN')")
    public String novoForm(Model model) {
        model.addAttribute("loja", new Loja());
        return "loja/form";
    }

    @PostMapping("/novo")
    @PreAuthorize("hasRole('ADMIN')")
    public String salvar(@Valid @ModelAttribute Loja loja,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "loja/form";
        }
        try {
            lojaService.salvar(loja);
            redirectAttributes.addFlashAttribute("sucesso", "Loja cadastrada com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/lojas";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editarForm(@PathVariable Integer id, Model model) {
        model.addAttribute("loja", lojaService.buscarPorId(id));
        return "loja/form";
    }

    @PostMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String atualizar(@PathVariable Integer id,
                            @Valid @ModelAttribute Loja loja,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "loja/form";
        }
        try {
            lojaService.atualizar(id, loja);
            redirectAttributes.addFlashAttribute("sucesso", "Loja atualizada com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/lojas";
    }

    @PostMapping("/inativar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String inativar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            lojaService.inativar(id);
            redirectAttributes.addFlashAttribute("sucesso", "Loja inativada com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/lojas";
    }
}
