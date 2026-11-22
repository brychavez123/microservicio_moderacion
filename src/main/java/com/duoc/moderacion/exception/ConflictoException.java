package com.duoc.moderacion.exception;

/** Se traduce a HTTP 409. */
public class ConflictoException extends RuntimeException {

    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
