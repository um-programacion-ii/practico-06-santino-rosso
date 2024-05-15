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
public class Medico {
    private int dni;
    private String nombre;
    private String apellido;
    private Especialidad especialidad;
    private List<ObraSocial> obrasSocialesAceptadas;
    private Boolean particular;
    private boolean disponible;
    private boolean pacienteEsperando;
}
