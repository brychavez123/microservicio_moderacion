package com.duoc.moderacion.model;

/**
 * Contrato común de todo contenido que se puede moderar (Tema y Comentario).
 * Gracias a esta interfaz las estrategias de moderación funcionan igual para ambos.
 */
public interface Moderable {

    Long getId();

    TipoContenido getTipoContenido();

    EstadoPublicacion getEstado();

    void setEstado(EstadoPublicacion estado);

    void setMotivoBaneo(String motivo);
}
