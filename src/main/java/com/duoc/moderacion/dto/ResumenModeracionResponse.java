package com.duoc.moderacion.dto;

/** Indicadores para el panel de moderación. */
public record ResumenModeracionResponse(
        long temasActivos,
        long temasBaneados,
        long comentariosActivos,
        long comentariosBaneados,
        long totalBaneos,
        long totalDesbaneos,
        long accionesUltimos7Dias) {
}
