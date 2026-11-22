package com.duoc.moderacion.dto;

import java.time.LocalDateTime;

import com.duoc.moderacion.model.Comentario;
import com.duoc.moderacion.model.EstadoPublicacion;
import com.duoc.moderacion.model.Tema;
import com.duoc.moderacion.model.TipoContenido;
import com.fasterxml.jackson.annotation.JsonInclude;

/** Vista común de un tema o comentario para el panel de moderación. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ContenidoModeradoResponse(
        TipoContenido tipo,
        Long id,
        String titulo,
        String contenido,
        LocalDateTime fecha,
        EstadoPublicacion estado,
        String motivoBaneo,
        Long temaId,
        String categoria,
        Long autorId,
        String autor) {

    public static ContenidoModeradoResponse from(Tema tema) {
        return new ContenidoModeradoResponse(
                TipoContenido.TEMA,
                tema.getId(),
                tema.getTitulo(),
                tema.getContenido(),
                tema.getFechaCreacion(),
                tema.getEstado(),
                tema.getMotivoBaneo(),
                null,
                tema.getCategoria() != null ? tema.getCategoria().getNombre() : null,
                tema.getUsuario() != null ? tema.getUsuario().getId() : null,
                tema.getUsuario() != null ? tema.getUsuario().getNombre() : null);
    }

    public static ContenidoModeradoResponse from(Comentario comentario) {
        Tema tema = comentario.getTema();
        return new ContenidoModeradoResponse(
                TipoContenido.COMENTARIO,
                comentario.getId(),
                tema != null ? tema.getTitulo() : null,
                comentario.getContenido(),
                comentario.getFecha(),
                comentario.getEstado(),
                comentario.getMotivoBaneo(),
                tema != null ? tema.getId() : null,
                null,
                comentario.getUsuario() != null ? comentario.getUsuario().getId() : null,
                comentario.getUsuario() != null ? comentario.getUsuario().getNombre() : null);
    }
}
