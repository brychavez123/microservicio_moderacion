package com.duoc.moderacion.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Historial de moderación: cada vez que se banea o desbanea algo queda un registro.
 * Se construye con el patrón Builder: AccionModeracion.builder()...build().
 */
@Entity
@Table(name = "MODERACION_ACCION")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccionModeracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_CONTENIDO", nullable = false, length = 20)
    private TipoContenido tipoContenido;

    @Column(name = "CONTENIDO_ID", nullable = false)
    private Long contenidoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACCION", nullable = false, length = 20)
    private TipoAccion accion;

    @Column(name = "MOTIVO", length = 500)
    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MODERADOR_ID")
    private Usuario moderador;

    @Column(name = "FECHA", nullable = false)
    private LocalDateTime fecha;

    @PrePersist
    void prePersist() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}
