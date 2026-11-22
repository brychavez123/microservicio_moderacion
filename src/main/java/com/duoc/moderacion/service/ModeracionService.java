package com.duoc.moderacion.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.duoc.moderacion.dto.AccionModeracionResponse;
import com.duoc.moderacion.dto.ContenidoModeradoResponse;
import com.duoc.moderacion.dto.ResumenModeracionResponse;
import com.duoc.moderacion.exception.RecursoNoEncontradoException;
import com.duoc.moderacion.model.AccionModeracion;
import com.duoc.moderacion.model.Comentario;
import com.duoc.moderacion.model.EstadoPublicacion;
import com.duoc.moderacion.model.Moderable;
import com.duoc.moderacion.model.Tema;
import com.duoc.moderacion.model.TipoAccion;
import com.duoc.moderacion.model.TipoContenido;
import com.duoc.moderacion.repository.AccionModeracionRepository;
import com.duoc.moderacion.repository.ComentarioRepository;
import com.duoc.moderacion.repository.TemaRepository;
import com.duoc.moderacion.security.UsuarioActual;
import com.duoc.moderacion.strategy.AccionModeracionFactory;
import com.duoc.moderacion.strategy.AccionModeracionStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModeracionService {

    private final TemaRepository temaRepository;
    private final ComentarioRepository comentarioRepository;
    private final AccionModeracionRepository accionRepository;
    private final AccionModeracionFactory accionFactory;
    private final UsuarioActual usuarioActual;

    // ---------- Temas ----------

    @Transactional(readOnly = true)
    public List<ContenidoModeradoResponse> listarTemas(EstadoPublicacion estado) {
        List<Tema> temas = estado == null
                ? temaRepository.findAllByOrderByFechaCreacionDesc()
                : temaRepository.findByEstadoOrderByFechaCreacionDesc(estado);
        return temas.stream().map(ContenidoModeradoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ContenidoModeradoResponse obtenerTema(Long id) {
        return ContenidoModeradoResponse.from(buscarTema(id));
    }

    @Transactional
    public ContenidoModeradoResponse moderarTema(Long id, TipoAccion accion, String motivo, Authentication auth) {
        Tema tema = buscarTema(id);
        ejecutar(tema, accion, motivo, auth);
        return ContenidoModeradoResponse.from(tema);
    }

    // ---------- Comentarios ----------

    @Transactional(readOnly = true)
    public List<ContenidoModeradoResponse> listarComentarios(EstadoPublicacion estado) {
        List<Comentario> comentarios = estado == null
                ? comentarioRepository.findAllByOrderByFechaDesc()
                : comentarioRepository.findByEstadoOrderByFechaDesc(estado);
        return comentarios.stream().map(ContenidoModeradoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ContenidoModeradoResponse obtenerComentario(Long id) {
        return ContenidoModeradoResponse.from(buscarComentario(id));
    }

    @Transactional
    public ContenidoModeradoResponse moderarComentario(Long id, TipoAccion accion, String motivo, Authentication auth) {
        Comentario comentario = buscarComentario(id);
        ejecutar(comentario, accion, motivo, auth);
        return ContenidoModeradoResponse.from(comentario);
    }

    // ---------- Historial ----------

    @Transactional(readOnly = true)
    public List<AccionModeracionResponse> historial(TipoContenido tipo, Long contenidoId) {
        List<AccionModeracion> acciones;
        if (tipo != null && contenidoId != null) {
            acciones = accionRepository.findByTipoContenidoAndContenidoIdOrderByFechaDesc(tipo, contenidoId);
        } else if (tipo != null) {
            acciones = accionRepository.findByTipoContenidoOrderByFechaDesc(tipo);
        } else {
            acciones = accionRepository.findAllByOrderByFechaDesc();
        }
        return acciones.stream().map(AccionModeracionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AccionModeracionResponse obtenerAccion(Long id) {
        return AccionModeracionResponse.from(buscarAccion(id));
    }

    @Transactional(readOnly = true)
    public List<AccionModeracionResponse> historialPorModerador(Long moderadorId) {
        return accionRepository.findByModeradorIdOrderByFechaDesc(moderadorId).stream()
                .map(AccionModeracionResponse::from).toList();
    }

    @Transactional
    public void eliminarAccion(Long id) {
        accionRepository.delete(buscarAccion(id));
        log.info("Registro de moderación eliminado: id={}", id);
    }

    @Transactional(readOnly = true)
    public ResumenModeracionResponse resumen() {
        return new ResumenModeracionResponse(
                temaRepository.countByEstado(EstadoPublicacion.ACTIVO),
                temaRepository.countByEstado(EstadoPublicacion.BANEADO),
                comentarioRepository.countByEstado(EstadoPublicacion.ACTIVO),
                comentarioRepository.countByEstado(EstadoPublicacion.BANEADO),
                accionRepository.countByAccion(TipoAccion.BANEAR),
                accionRepository.countByAccion(TipoAccion.DESBANEAR),
                accionRepository.countByFechaAfter(LocalDateTime.now().minusDays(7)));
    }

    // ---------- Núcleo: Strategy + Factory + Builder ----------

    private void ejecutar(Moderable contenido, TipoAccion accion, String motivo, Authentication auth) {
        AccionModeracionStrategy estrategia = accionFactory.obtener(accion);
        estrategia.validar(contenido, motivo);
        estrategia.aplicar(contenido, motivo);

        AccionModeracion registro = AccionModeracion.builder()
                .tipoContenido(contenido.getTipoContenido())
                .contenidoId(contenido.getId())
                .accion(accion)
                .motivo(motivo != null && !motivo.isBlank() ? motivo.trim() : null)
                .moderador(usuarioActual.obtener(auth))
                .build();
        accionRepository.save(registro);

        log.warn("{} {} id={} por {} (motivo: {})", accion, contenido.getTipoContenido(), contenido.getId(),
                auth.getName(), motivo);
    }

    private Tema buscarTema(Long id) {
        return temaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un tema con id " + id));
    }

    private Comentario buscarComentario(Long id) {
        return comentarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un comentario con id " + id));
    }

    private AccionModeracion buscarAccion(Long id) {
        return accionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un registro de moderación con id " + id));
    }
}
