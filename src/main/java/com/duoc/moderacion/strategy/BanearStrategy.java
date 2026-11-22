package com.duoc.moderacion.strategy;

import org.springframework.stereotype.Component;

import com.duoc.moderacion.exception.ConflictoException;
import com.duoc.moderacion.model.EstadoPublicacion;
import com.duoc.moderacion.model.Moderable;
import com.duoc.moderacion.model.TipoAccion;

/** Banea un contenido activo. El motivo es obligatorio. */
@Component
public class BanearStrategy implements AccionModeracionStrategy {

    @Override
    public TipoAccion tipo() {
        return TipoAccion.BANEAR;
    }

    @Override
    public void validar(Moderable contenido, String motivo) {
        if (contenido.getEstado() == EstadoPublicacion.BANEADO) {
            throw new ConflictoException(nombre(contenido) + " con id " + contenido.getId() + " ya está baneado");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new ConflictoException("Debes indicar el motivo del baneo");
        }
    }

    @Override
    public void aplicar(Moderable contenido, String motivo) {
        contenido.setEstado(EstadoPublicacion.BANEADO);
        contenido.setMotivoBaneo(motivo.trim());
    }

    private static String nombre(Moderable contenido) {
        return switch (contenido.getTipoContenido()) {
            case TEMA -> "El tema";
            case COMENTARIO -> "El comentario";
        };
    }
}
