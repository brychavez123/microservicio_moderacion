package com.duoc.moderacion.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.duoc.moderacion.exception.RecursoNoEncontradoException;
import com.duoc.moderacion.model.Usuario;
import com.duoc.moderacion.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

/** Utilidades para conocer al usuario autenticado y sus privilegios. */
@Component
@RequiredArgsConstructor
public class UsuarioActual {

    private final UsuarioRepository usuarioRepository;

    public Usuario obtener(Authentication auth) {
        return usuarioRepository.findByEmailIgnoreCase(auth.getName())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario autenticado no encontrado"));
    }

    public boolean esAdmin(Authentication auth) {
        return tieneRol(auth, "ADMIN");
    }

    public boolean puedeModerar(Authentication auth) {
        return tieneRol(auth, "ADMIN") || tieneRol(auth, "MODERADOR");
    }

    private boolean tieneRol(Authentication auth, String rol) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + rol));
    }
}
