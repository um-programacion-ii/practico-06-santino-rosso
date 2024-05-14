package ar.edu.um.Excepciones;

public class RecetaNoCreadaException extends RuntimeException{
    public RecetaNoCreadaException(String mensaje){
        super(mensaje);
    }
}
