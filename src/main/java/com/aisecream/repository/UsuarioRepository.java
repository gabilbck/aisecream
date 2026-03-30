package com.aisecream.repository;

import com.aisecream.model.Usuario;
import com.aisecream.model.enums.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByEmailIgnoreCase(String email);

    List<Usuario> findByPerfilOrderByNomeAsc(Perfil perfil);

    Optional<Usuario> findByIdAndPerfil(Integer id, Perfil perfil);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Integer id);
}
