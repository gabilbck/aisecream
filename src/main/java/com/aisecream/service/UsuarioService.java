package com.aisecream.service;

import com.aisecream.model.Usuario;
import com.aisecream.model.enums.Perfil;
import com.aisecream.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
        return User.builder()
                .username(u.getEmail())
                .password(u.getSenhaHash())
                .authorities("ROLE_" + u.getPerfil().name())
                .disabled(!u.isAtivo())
                .build();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarOperadores() {
        return usuarioRepository.findByPerfilOrderByNomeAsc(Perfil.OPERADOR);
    }

    @Transactional(readOnly = true)
    public Usuario buscarOperadorPorId(Integer id) {
        return usuarioRepository.findByIdAndPerfil(id, Perfil.OPERADOR)
                .orElseThrow(() -> new IllegalArgumentException("Operador não encontrado."));
    }

    @Transactional
    public void cadastrarOperador(String nome, String email, String senhaPlano) {
        if (senhaPlano == null || senhaPlano.isBlank()) {
            throw new IllegalArgumentException("Informe a senha.");
        }
        String emailNorm = normalizarEmail(email);
        if (usuarioRepository.existsByEmailIgnoreCase(emailNorm)) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado.");
        }
        Usuario u = new Usuario();
        u.setNome(nome.trim());
        u.setEmail(emailNorm);
        u.setSenhaHash(passwordEncoder.encode(senhaPlano));
        u.setPerfil(Perfil.OPERADOR);
        u.setAtivo(true);
        usuarioRepository.save(u);
    }

    @Transactional
    public void atualizarOperador(Integer id, String nome, String email, String senhaOpcional, boolean ativo) {
        Usuario u = buscarOperadorPorId(id);
        String emailNorm = normalizarEmail(email);
        if (!emailNorm.equals(u.getEmail())
                && usuarioRepository.existsByEmailIgnoreCaseAndIdNot(emailNorm, id)) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado.");
        }
        u.setNome(nome.trim());
        u.setEmail(emailNorm);
        u.setAtivo(ativo);
        if (senhaOpcional != null && !senhaOpcional.isBlank()) {
            u.setSenhaHash(passwordEncoder.encode(senhaOpcional));
        }
        usuarioRepository.save(u);
    }

    private static String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
