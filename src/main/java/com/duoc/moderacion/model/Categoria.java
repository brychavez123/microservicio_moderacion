package com.duoc.moderacion.model;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Tabla administrada por ms-foro; aquí solo se lee. */
@Entity
@Immutable
@Table(name = "CATEGORIA")
@Getter
@NoArgsConstructor
public class Categoria {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOMBRE")
    private String nombre;
}
