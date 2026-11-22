package com.duoc.moderacion.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoc.moderacion.dto.AccionModeracionResponse;
import com.duoc.moderacion.dto.ContenidoModeradoResponse;
import com.duoc.moderacion.dto.MensajeResponse;
import com.duoc.moderacion.dto.MotivoRequest;
import com.duoc.moderacion.dto.ResumenModeracionResponse;
import com.duoc.moderacion.model.EstadoPublicacion;
import com.duoc.moderacion.model.TipoAccion;
import com.duoc.moderacion.model.TipoContenido;
import com.duoc.moderacion.service.ModeracionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Gestión de publicaciones y comentarios por parte de MODERADOR y ADMIN. */
@RestController
@RequestMapping("/api/moderacion")
@RequiredArgsConstructor
public class ModeracionController {

    private final ModeracionService moderacionService;

    @GetMapping("/resumen")
    public ResponseEntity<ResumenModeracionResponse> resumen() {
        return ResponseEntity.ok(moderacionService.resumen());
    }

    // ---------- Temas ----------

    /** ?estado=ACTIVO | BANEADO (sin filtro devuelve todos). */
    @GetMapping("/temas")
    public ResponseEntity<List<ContenidoModeradoResponse>> temas(
            @RequestParam(required = false) EstadoPublicacion estado) {
        return ResponseEntity.ok(moderacionService.listarTemas(estado));
    }

    @GetMapping("/temas/{id}")
    public ResponseEntity<ContenidoModeradoResponse> tema(@PathVariable Long id) {
        return ResponseEntity.ok(moderacionService.obtenerTema(id));
    }

    @PutMapping("/temas/{id}/banear")
    public ResponseEntity<ContenidoModeradoResponse> banearTema(@PathVariable Long id,
            @Valid @RequestBody MotivoRequest request, Authentication auth) {
        return ResponseEntity.ok(moderacionService.moderarTema(id, TipoAccion.BANEAR, request.motivo(), auth));
    }

    @PutMapping("/temas/{id}/desbanear")
    public ResponseEntity<ContenidoModeradoResponse> desbanearTema(@PathVariable Long id,
            @Valid @RequestBody(required = false) MotivoRequest request, Authentication auth) {
        return ResponseEntity.ok(moderacionService.moderarTema(id, TipoAccion.DESBANEAR, motivo(request), auth));
    }

    // ---------- Comentarios ----------

    @GetMapping("/comentarios")
    public ResponseEntity<List<ContenidoModeradoResponse>> comentarios(
            @RequestParam(required = false) EstadoPublicacion estado) {
        return ResponseEntity.ok(moderacionService.listarComentarios(estado));
    }

    @GetMapping("/comentarios/{id}")
    public ResponseEntity<ContenidoModeradoResponse> comentario(@PathVariable Long id) {
        return ResponseEntity.ok(moderacionService.obtenerComentario(id));
    }

    @PutMapping("/comentarios/{id}/banear")
    public ResponseEntity<ContenidoModeradoResponse> banearComentario(@PathVariable Long id,
            @Valid @RequestBody MotivoRequest request, Authentication auth) {
        return ResponseEntity.ok(moderacionService.moderarComentario(id, TipoAccion.BANEAR, request.motivo(), auth));
    }

    @PutMapping("/comentarios/{id}/desbanear")
    public ResponseEntity<ContenidoModeradoResponse> desbanearComentario(@PathVariable Long id,
            @Valid @RequestBody(required = false) MotivoRequest request, Authentication auth) {
        return ResponseEntity.ok(moderacionService.moderarComentario(id, TipoAccion.DESBANEAR, motivo(request), auth));
    }

    // ---------- Historial ----------

    /** ?tipo=TEMA|COMENTARIO y opcionalmente &contenidoId=5 */
    @GetMapping("/historial")
    public ResponseEntity<List<AccionModeracionResponse>> historial(
            @RequestParam(required = false) TipoContenido tipo,
            @RequestParam(required = false) Long contenidoId) {
        return ResponseEntity.ok(moderacionService.historial(tipo, contenidoId));
    }

    @GetMapping("/historial/{id}")
    public ResponseEntity<AccionModeracionResponse> accion(@PathVariable Long id) {
        return ResponseEntity.ok(moderacionService.obtenerAccion(id));
    }

    @GetMapping("/historial/moderador/{moderadorId}")
    public ResponseEntity<List<AccionModeracionResponse>> porModerador(@PathVariable Long moderadorId) {
        return ResponseEntity.ok(moderacionService.historialPorModerador(moderadorId));
    }

    /** Solo ADMIN. */
    @DeleteMapping("/historial/{id}")
    public ResponseEntity<MensajeResponse> eliminarAccion(@PathVariable Long id) {
        moderacionService.eliminarAccion(id);
        return ResponseEntity.ok(new MensajeResponse("Registro de moderación " + id + " eliminado correctamente"));
    }

    private static String motivo(MotivoRequest request) {
        return request != null ? request.motivo() : null;
    }
}
