package ar.edu.um.Excepciones;

public class MedicamentosNoDisponiblesException extends RuntimeException{
    public MedicamentosNoDisponiblesException(String mensaje){
        super(mensaje);
    }
}
