package ar.edu.um.Servicios;

import ar.edu.um.Entidades.Medico;
import ar.edu.um.Entidades.Paciente;
import ar.edu.um.Entidades.Receta;
import ar.edu.um.Entidades.Turno;
import ar.edu.um.Excepciones.TurnoIniciadoException;
import ar.edu.um.Excepciones.TurnoTerminadoException;

import java.util.Random;


public class GestionTurnoService {
    private static GestionTurnoService instancia;
    private AtencionMedicoService atencionMedicoService;



    private GestionTurnoService() {
        this.atencionMedicoService = AtencionMedicoService.getInstance();
    }

    public static synchronized GestionTurnoService getInstance() {
        if (instancia == null) {
            instancia = new GestionTurnoService();
        }
        return instancia;
    }

    public synchronized Turno crearTurno(Medico medico, Paciente paciente, boolean obraSocial) {
        Turno turnoAsignado = new Turno();
        String numero = "" + medico + obraSocial + paciente;
        String hashCodeString = Integer.toString(numero.hashCode());
        turnoAsignado.setNumeroTurno(hashCodeString);
        turnoAsignado.setMedico(medico);
        turnoAsignado.setConObraSocial(obraSocial);
        turnoAsignado.setPaciente(paciente);
        return turnoAsignado;
    }

    public synchronized Receta iniciarTurno(Turno turno) {
        if (!turno.isHaComenzado()) {
            turno.setHaComenzado(true);
            System.out.println("Turno iniciado con " + turno.getMedico().getNombre() + " en " + turno.getMedico().getEspecialidad().getNombre() + ".");
            Random random = new Random();
            Receta receta = atencionMedicoService.crearReceta(random);
            return receta;
        } else {
            throw new TurnoIniciadoException("El turno ya ha sido iniciado.");
        }
    }

    public synchronized void terminarTurno(Turno turno) {
        if (turno.isHaComenzado()) {
            if(!turno.isHaTerminado()) {
                turno.setHaTerminado(true);
                System.out.println("Turno terminado con " + turno.getMedico().getNombre() + " en " + turno.getMedico().getEspecialidad().getNombre() + ".");
            } else {
                throw new TurnoTerminadoException("El turno ya ha terminado.");
            }
        } else {
            throw new TurnoIniciadoException("El turno no ha sido iniciado.");
        }
    }
}


