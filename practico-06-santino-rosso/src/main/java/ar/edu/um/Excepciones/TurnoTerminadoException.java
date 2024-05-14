package ar.edu.um.Excepciones;

public class TurnoTerminadoException extends RuntimeException{
    public TurnoTerminadoException(String mensaje){
        super(mensaje);
    }
}
