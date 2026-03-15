package com.aisecrean.controller;

import com.aisecrean.model.Sabor;
import com.aisecrean.service.SaborService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sabores")
public class SaborController {

    private final SaborService saborService;

    public SaborController(SaborService saborService) {
        this.saborService = saborService;
    }

    // Lista todos os sabores
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("sabores", saborService.listarTodos());
        return "sabor/listar";
    }

    // Exibe formulário de cadastro
    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("sabor", new Sabor());
        return "sabor/form";
    }

    // Salva novo sabor
    @PostMapping("/novo")
    public String salvar(@Valid @ModelAttribute Sabor sabor,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "sabor/form";
        }
        try {
            saborService.salvar(sabor);
            redirectAttributes.addFlashAttribute("sucesso", "Sabor cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/sabores";
    }

    // Exibe formulário de edição
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        model.addAttribute("sabor", saborService.buscarPorId(id));
        return "sabor/form";
    }

    // Salva edição
    @PostMapping("/editar/{id}")
    public String atualizar(@PathVariable Integer id,
                            @Valid @ModelAttribute Sabor sabor,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "sabor/form";
        }
        try {
            saborService.atualizar(id, sabor);
            redirectAttributes.addFlashAttribute("sucesso", "Sabor atualizado com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/sabores";
    }

    // Inativa sabor
    @PostMapping("/inativar/{id}")
    public String inativar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            saborService.inativar(id);
            redirectAttributes.addFlashAttribute("sucesso", "Sabor inativado com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/sabores";
    }
}
