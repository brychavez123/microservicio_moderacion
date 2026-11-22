package com.duoc.moderacion.strategy;

import com.duoc.moderacion.model.Moderable;
import com.duoc.moderacion.model.TipoAccion;

/**
 * Patrón Strategy: cada acción de moderación encapsula sus propias reglas.
 * Para agregar una acción nueva (por ejemplo OCULTAR) basta con crear otra
 * implementación anotada con @Component; el servicio no cambia.
 */
public interface AccionModeracionStrategy {

    /** Acción que resuelve esta estrategia. */
    TipoAccion tipo();

    /** Verifica que la acción se pueda aplicar; lanza ConflictoException si no. */
    void validar(Moderable contenido, String motivo);

    /** Aplica la acción sobre el contenido. */
    void aplicar(Moderable contenido, String motivo);
}
