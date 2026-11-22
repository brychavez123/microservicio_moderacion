package com.duoc.moderacion.dto;

import jakarta.validation.constraints.Size;

/** Motivo de la acción. Obligatorio para banear (lo valida BanearStrategy), opcional para desbanear. */
public record MotivoRequest(

        @Size(min = 5, max = 500, message = "El motivo debe tener entre 5 y 500 caracteres")
        String motivo) {
}
