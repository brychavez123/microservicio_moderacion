package com.duoc.moderacion.strategy;

import org.springframework.stereotype.Component;

import com.duoc.moderacion.exception.ConflictoException;
import com.duoc.moderacion.model.EstadoPublicacion;
import com.duoc.moderacion.model.Moderable;
import com.duoc.moderacion.model.TipoAccion;

/** Restaura un contenido baneado. El motivo es opcional (queda en el historial). */
@Component
public class DesbanearStrategy implements AccionModeracionStrategy {

    @Override
    public TipoAccion tipo() {
        return TipoAccion.DESBANEAR;
    }

    @Override
    public void validar(Moderable contenido, String motivo) {
        if (contenido.getEstado() == EstadoPublicacion.ACTIVO) {
            throw new ConflictoException("El contenido con id " + contenido.getId() + " no está baneado");
        }
    }

    @Override
    public void aplicar(Moderable contenido, String motivo) {
        contenido.setEstado(EstadoPublicacion.ACTIVO);
        contenido.setMotivoBaneo(null);
    }
}
