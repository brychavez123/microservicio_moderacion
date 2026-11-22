package com.duoc.moderacion.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duoc.moderacion.model.AccionModeracion;
import com.duoc.moderacion.model.TipoAccion;
import com.duoc.moderacion.model.TipoContenido;

@Repository
public interface AccionModeracionRepository extends JpaRepository<AccionModeracion, Long> {

    List<AccionModeracion> findAllByOrderByFechaDesc();

    List<AccionModeracion> findByTipoContenidoOrderByFechaDesc(TipoContenido tipo);

    List<AccionModeracion> findByTipoContenidoAndContenidoIdOrderByFechaDesc(TipoContenido tipo, Long contenidoId);

    List<AccionModeracion> findByModeradorIdOrderByFechaDesc(Long moderadorId);

    long countByAccion(TipoAccion accion);

    long countByFechaAfter(LocalDateTime desde);
}
