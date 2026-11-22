package com.duoc.moderacion.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Comentario publicado en ms-foro. Aquí solo se modera (ESTADO y MOTIVO_BANEO). */
@Entity
@DynamicUpdate
@Table(name = "COMENTARIO")
@Getter
@Setter
@NoArgsConstructor
public class Comentario implements Moderable {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "CONTENIDO", insertable = false, updatable = false)
    private String contenido;

    @Column(name = "FECHA", insertable = false, updatable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoPublicacion estado;

    @Column(name = "MOTIVO_BANEO", length = 500)
    private String motivoBaneo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEMA_ID", insertable = false, updatable = false)
    private Tema tema;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_ID", insertable = false, updatable = false)
    private Usuario usuario;

    @Override
    public TipoContenido getTipoContenido() {
        return TipoContenido.COMENTARIO;
    }
}
