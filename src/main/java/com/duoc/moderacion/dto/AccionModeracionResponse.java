package com.duoc.moderacion.dto;

import java.time.LocalDateTime;

import com.duoc.moderacion.model.AccionModeracion;
import com.duoc.moderacion.model.TipoAccion;
import com.duoc.moderacion.model.TipoContenido;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AccionModeracionResponse(
        Long id,
        TipoContenido tipoContenido,
        Long contenidoId,
        TipoAccion accion,
        String motivo,
        LocalDateTime fecha,
        Long moderadorId,
        String moderador) {

    public static AccionModeracionResponse from(AccionModeracion accion) {
        return new AccionModeracionResponse(
                accion.getId(),
                accion.getTipoContenido(),
                accion.getContenidoId(),
                accion.getAccion(),
                accion.getMotivo(),
                accion.getFecha(),
                accion.getModerador() != null ? accion.getModerador().getId() : null,
                accion.getModerador() != null ? accion.getModerador().getNombre() : "Usuario eliminado");
    }
}
