package com.duoc.moderacion.exception;

/** Se traduce a HTTP 403. */
public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
