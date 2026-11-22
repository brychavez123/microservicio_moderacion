package com.duoc.moderacion.strategy;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.duoc.moderacion.model.TipoAccion;

/**
 * Patrón Factory: entrega la estrategia correcta según la acción pedida.
 * Spring inyecta todas las implementaciones de AccionModeracionStrategy.
 */
@Component
public class AccionModeracionFactory {

    private final Map<TipoAccion, AccionModeracionStrategy> estrategias = new EnumMap<>(TipoAccion.class);

    public AccionModeracionFactory(List<AccionModeracionStrategy> disponibles) {
        disponibles.forEach(estrategia -> estrategias.put(estrategia.tipo(), estrategia));
    }

    public AccionModeracionStrategy obtener(TipoAccion accion) {
        AccionModeracionStrategy estrategia = estrategias.get(accion);
        if (estrategia == null) {
            throw new IllegalStateException("No hay una estrategia para la acción " + accion);
        }
        return estrategia;
    }
}
