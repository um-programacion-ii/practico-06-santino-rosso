package ar.edu.um.Servicios;

import ar.edu.um.Dao.ClinicaDao;
import ar.edu.um.Entidades.*;
import ar.edu.um.Excepciones.MedicosNoEncontradosException;
import ar.edu.um.Excepciones.TurnoIniciadoException;
import ar.edu.um.Excepciones.TurnoNoCreadoException;
import ar.edu.um.Excepciones.TurnoTerminadoException;

import java.util.List;
import java.util.Random;

public class ClinicaService {
    private static ClinicaService instancia;
    private ClinicaDao clinicaDao;
    private GestionTurnoService gestionTurnoService;



    private ClinicaService() {
        this.clinicaDao = ClinicaDao.getInstance();
        this.gestionTurnoService = GestionTurnoService.getInstance();
    }

    public static synchronized ClinicaService getInstance() {
        if (instancia == null) {
            instancia = new ClinicaService();
        }
        return instancia;
    }

    public synchronized Turno pedirTurno(Paciente paciente, Especialidad especialidad, ObraSocial obraSocial) {
        mostrarTodosLosMedicos();
        mostrarMédicosDeUnaEspecialidad(especialidad);
        Random random = new Random();
        Turno turnoNuevo;
        if (obraSocial == null) {
            List<Medico> medicosParticulares = this.clinicaDao.obtenerMedicosPorEspecialidadParticulares(especialidad);
            if (medicosParticulares.isEmpty()) {
                throw new MedicosNoEncontradosException("Medicos no encontrados.");
            }
            int indiceAleatorio = random.nextInt(medicosParticulares.size());
            Medico medicoSeleccionado = medicosParticulares.get(indiceAleatorio);
            turnoNuevo =  gestionTurnoService.crearTurno(medicoSeleccionado, paciente, false);

        } else {
            List<Medico> medicosObraSocial = this.clinicaDao.encontrarMedicosPorEspecialidadObraSocial(especialidad, obraSocial);
            if (medicosObraSocial.isEmpty()) {
                throw new MedicosNoEncontradosException("Medicos no encontrados.");
            }
            int indiceAleatorio = random.nextInt(medicosObraSocial.size());
            Medico medicoSeleccionado = medicosObraSocial.get(indiceAleatorio);
            turnoNuevo = gestionTurnoService.crearTurno(medicoSeleccionado, paciente, true);
        }
        if (turnoNuevo == null){
            throw new TurnoNoCreadoException("No se creo el turno");
        }
        this.clinicaDao.guardarTurnoPaciente(paciente,turnoNuevo);
        System.out.println("Se te asigno un turno con el medico: " + turnoNuevo.getMedico());
        return turnoNuevo;
    }

    public synchronized Receta puedeIniciarTurno(Turno turno, Paciente paciente) {
        Receta receta = new Receta();
        if (turno.isHaComenzado()){
            throw new TurnoIniciadoException("Turno ya iniciado");
        } else {
            Medico medico = turno.getMedico();
            while (!medico.isDisponible()){
                try {
                    medico.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException("Error al esperar por el médico.", e);
                }
            }
            medico.setDisponible(false);
            System.out.println("Iniciando turno");
            receta = gestionTurnoService.iniciarTurno(turno);
            if (receta != null) {
                this.clinicaDao.guardarRecetaPaciente(paciente,receta);
            }
        }
        return receta;
    }

    public synchronized void puedeTerminarTurno(Turno turno) {
       if (turno.isHaComenzado()){
           if (turno.isHaTerminado()){
               throw new TurnoTerminadoException("El turno ya termino");
           } else {
               gestionTurnoService.terminarTurno(turno);
               Medico medico = turno.getMedico();
               medico.setDisponible(true);
               medico.notifyAll();
           }
       } else {
           throw new TurnoIniciadoException("El turno nunca empezo");
       }
    }

    public void mostrarMédicosDeUnaEspecialidad(Especialidad especialidad){
        List<Medico> medicosMostrar = clinicaDao.obtenerMedicosPorEspecialidad(especialidad);
        System.out.println("Los medicos de la especialidad " + especialidad.getNombre() + " son:");
        for(int i = 0; i < medicosMostrar.size(); i++){
            Medico medico = medicosMostrar.get(i);
            List<ObraSocial> obrasSocialesAceptadas = medico.getObrasSocialesAceptadas();
            StringBuilder obrasSociales = new StringBuilder();
            for (int j = 0; j < obrasSocialesAceptadas.size(); j++) {
                obrasSociales.append(obrasSocialesAceptadas.get(j).getNombre());
                if (j < obrasSocialesAceptadas.size() - 1) {
                    obrasSociales.append(", ");
                }
            }
            System.out.println(medico.getNombre() + ", " + medico.getApellido() + ". Las obras sociales que acepta son: " + obrasSociales);
        }
    }


    public void mostrarTodosLosMedicos(){
        List<Medico> medicos = clinicaDao.obtenerMedicos();
        System.out.println("Todos los medicos son:");
        for(int i = 0; i < medicos.size(); i++){
            Medico medico = medicos.get(i);
            System.out.println(medico.getNombre() + ", " + medico.getApellido() + ". Especialidad: " + medico.getEspecialidad().getNombre());
        }
    }
}

