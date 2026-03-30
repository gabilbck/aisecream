package com.aisecream.controller;

import com.aisecream.dto.UsuarioOperadorForm;
import com.aisecream.model.Usuario;
import com.aisecream.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("operadores", usuarioService.listarOperadores());
        return "usuario/listar";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("form", new UsuarioOperadorForm());
        model.addAttribute("modoEdicao", false);
        return "usuario/form";
    }

    @PostMapping("/novo")
    public String salvar(
            @Valid @ModelAttribute("form") UsuarioOperadorForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "usuario/form";
        }
        try {
            usuarioService.cadastrarOperador(form.getNome(), form.getEmail(), form.getSenha());
            redirectAttributes.addFlashAttribute("sucesso", "Operador cadastrado com sucesso.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("modoEdicao", false);
            return "usuario/form";
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        Usuario u = usuarioService.buscarOperadorPorId(id);
        UsuarioOperadorForm form = new UsuarioOperadorForm();
        form.setId(u.getId());
        form.setNome(u.getNome());
        form.setEmail(u.getEmail());
        form.setAtivo(u.getAtivo());
        form.setSenha(null);
        model.addAttribute("form", form);
        model.addAttribute("modoEdicao", true);
        return "usuario/form";
    }

    @PostMapping("/editar/{id}")
    public String atualizar(
            @PathVariable Integer id,
            @Valid @ModelAttribute("form") UsuarioOperadorForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("modoEdicao", true);
            return "usuario/form";
        }
        try {
            usuarioService.atualizarOperador(
                    id,
                    form.getNome(),
                    form.getEmail(),
                    form.getSenha(),
                    Boolean.TRUE.equals(form.getAtivo())
            );
            redirectAttributes.addFlashAttribute("sucesso", "Operador atualizado com sucesso.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("modoEdicao", true);
            return "usuario/form";
        }
        return "redirect:/usuarios";
    }
}
