package ar.edu.um.Entidades;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Turno {
    private String numeroTurno;
    private Medico medico;
    private Paciente paciente;
    private Boolean conObraSocial;
    private boolean haComenzado;
    private boolean haTerminado;
}
