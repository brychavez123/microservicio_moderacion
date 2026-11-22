package com.duoc.moderacion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duoc.moderacion.model.Comentario;
import com.duoc.moderacion.model.EstadoPublicacion;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    List<Comentario> findAllByOrderByFechaDesc();

    List<Comentario> findByEstadoOrderByFechaDesc(EstadoPublicacion estado);

    long countByEstado(EstadoPublicacion estado);
}
