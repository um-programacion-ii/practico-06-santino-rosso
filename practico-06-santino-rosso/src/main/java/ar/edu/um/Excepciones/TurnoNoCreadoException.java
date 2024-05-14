package ar.edu.um.Excepciones;

public class TurnoNoCreadoException extends RuntimeException{
    public TurnoNoCreadoException(String mensaje){
        super(mensaje);
    }
}
