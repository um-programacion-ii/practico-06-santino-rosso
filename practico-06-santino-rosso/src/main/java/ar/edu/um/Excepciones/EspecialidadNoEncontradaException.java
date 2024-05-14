package ar.edu.um.Excepciones;

public class EspecialidadNoEncontradaException extends RuntimeException{
    public EspecialidadNoEncontradaException(String mensaje){
        super(mensaje);
    }
}
