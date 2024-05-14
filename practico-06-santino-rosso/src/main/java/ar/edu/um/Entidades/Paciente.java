package ar.edu.um.Entidades;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Paciente{
    private int dni;
    private String nombre;
    private String apellido;
    private ObraSocial obraSocial;
    private List<Turno> turnos;
    private List<Receta> recetas;
}