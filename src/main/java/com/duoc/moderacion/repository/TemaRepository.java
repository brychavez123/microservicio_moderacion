package com.duoc.moderacion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duoc.moderacion.model.EstadoPublicacion;
import com.duoc.moderacion.model.Tema;

@Repository
public interface TemaRepository extends JpaRepository<Tema, Long> {

    List<Tema> findAllByOrderByFechaCreacionDesc();

    List<Tema> findByEstadoOrderByFechaCreacionDesc(EstadoPublicacion estado);

    long countByEstado(EstadoPublicacion estado);
}
