package ar.edu.um.Excepciones;

public class MedicosNoEncontradosException extends RuntimeException{
    public MedicosNoEncontradosException(String mensaje){
        super(mensaje);
    }
}
