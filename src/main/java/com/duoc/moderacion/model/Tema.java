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

/**
 * Tema publicado en ms-foro. Este microservicio solo cambia su ESTADO y MOTIVO_BANEO
 * (@DynamicUpdate hace que el UPDATE incluya únicamente las columnas modificadas).
 */
@Entity
@DynamicUpdate
@Table(name = "TEMA")
@Getter
@Setter
@NoArgsConstructor
public class Tema implements Moderable {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "TITULO", insertable = false, updatable = false)
    private String titulo;

    @Column(name = "CONTENIDO", insertable = false, updatable = false)
    private String contenido;

    @Column(name = "FECHA_CREACION", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoPublicacion estado;

    @Column(name = "MOTIVO_BANEO", length = 500)
    private String motivoBaneo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_ID", insertable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORIA_ID", insertable = false, updatable = false)
    private Categoria categoria;

    @Override
    public TipoContenido getTipoContenido() {
        return TipoContenido.TEMA;
    }
}
