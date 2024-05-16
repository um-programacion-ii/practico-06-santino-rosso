package TestDao;

import ar.edu.um.Dao.ClinicaDao;
import ar.edu.um.Entidades.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ClinicaDaoTest {

    private ClinicaDao clinicaDao;
    private Paciente paciente;
    private Medico medico;
    private Especialidad especialidad;
    private Turno turno1;
    private Turno turno2;
    private List<Receta> recetas;
    private Receta receta;
    private ObraSocial obraSocial;
    private List<Turno> turnos;
    private Medicamento medicamento;
    private Map<Medicamento,Integer> medicamentos;
    private List<ObraSocial> obrasSociales;

    @BeforeEach
    void setUp() {
        this.turnos = new ArrayList<>();
        this.recetas = new ArrayList<>();
        this.obrasSociales = new ArrayList<>();
        this.medicamentos = new HashMap<>();
        this.clinicaDao = ClinicaDao.getInstance();
        this.turno1 = new Turno("16156",this.medico,this.paciente,true,false,false);
        this.turno2 = new Turno("9898",this.medico,this.paciente,true,false,false);
        this.turnos.add(this.turno1);
        this.turnos.add(this.turno2);
        this.medicamento = new Medicamento("Flexiplen","Diclofenac");
        this.medicamentos.put(this.medicamento,5);
        this.receta = new Receta(this.medicamentos);
        this.especialidad = new Especialidad("Cardiología");
        this.obraSocial = new ObraSocial("OSDE");
        this.obrasSociales.add(obraSocial);
        this.medico = new Medico(87654321,"Alberto", "Gomez", this.especialidad,this.obrasSociales,true,true,false);
        this.paciente = new Paciente(12345678, "Juan", "Perez",this.obraSocial,this.turnos,this.recetas);


        clinicaDao.eliminarPaciente(paciente.getDni());
        clinicaDao.eliminarMedico(medico.getDni());
        clinicaDao.eliminarEspecialidad(especialidad);
    }

    @Test
    void testGuardarYObtenerPaciente() {
        this.clinicaDao.guardarPaciente(this.paciente);
        Paciente pacienteObtenido = this.clinicaDao.obtenerPaciente(this.paciente.getDni());
        assertEquals(this.paciente, pacienteObtenido);
    }

    @Test
    void testActualizarPaciente() {
        this.clinicaDao.guardarPaciente(this.paciente);
        this.paciente.setNombre("Lucas");
        this.clinicaDao.actualizarPaciente(this.paciente);
        Paciente pacienteActualizado = this.clinicaDao.obtenerPaciente(this.paciente.getDni());
        assertEquals("Lucas", pacienteActualizado.getNombre());
    }

    @Test
    void testEliminarPaciente() {
        this.clinicaDao.guardarPaciente(this.paciente);
        this.clinicaDao.eliminarPaciente(this.paciente.getDni());
        Paciente pacienteEliminado = this.clinicaDao.obtenerPaciente(this.paciente.getDni());
        assertNull(pacienteEliminado);
    }

    @Test
    void testObtenerPacientes() {
        this.clinicaDao.guardarPaciente(this.paciente);
        List<Paciente> pacientes = this.clinicaDao.obtenerPacientes();
        assertTrue(pacientes.contains(this.paciente));
    }

    @Test
    void testGuardarTurnoPaciente() {
        this.clinicaDao.guardarPaciente(this.paciente);
        this.clinicaDao.guardarTurnoPaciente(this.paciente, this.turno1);
        Paciente pacienteConTurno = this.clinicaDao.obtenerPaciente(this.paciente.getDni());
        assertTrue(pacienteConTurno.getTurnos().contains(this.turno1));
    }

    @Test
    void testGuardarRecetaPaciente() {
        this.clinicaDao.guardarPaciente(this.paciente);
        this.clinicaDao.guardarRecetaPaciente(this.paciente, this.receta);
        Paciente pacienteConReceta = this.clinicaDao.obtenerPaciente(this.paciente.getDni());
        assertTrue(pacienteConReceta.getRecetas().contains(this.receta));
    }

    @Test
    void testGuardarYObtenerMedico() {
        this.clinicaDao.guardarMedico(this.medico);
        Medico medicoObtenido = this.clinicaDao.obtenerMedico(this.medico.getDni());
        assertEquals(this.medico, medicoObtenido);
    }

    @Test
    void testActualizarMedico() {
        this.clinicaDao.guardarMedico(this.medico);
        this.medico.setNombre("Maria");
        this.clinicaDao.actualizarMedico(this.medico);
        Medico medicoActualizado = this.clinicaDao.obtenerMedico(this.medico.getDni());
        assertEquals("Maria", medicoActualizado.getNombre());
    }

    @Test
    void testEliminarMedico() {
        this.clinicaDao.guardarMedico(this.medico);
        this.clinicaDao.eliminarMedico(this.medico.getDni());
        Medico medicoEliminado = this.clinicaDao.obtenerMedico(this.medico.getDni());
        assertNull(medicoEliminado);
    }

    @Test
    void testObtenerMedicos() {
        this.clinicaDao.guardarMedico(this.medico);
        List<Medico> medicos = this.clinicaDao.obtenerMedicos();
        assertTrue(medicos.contains(this.medico));
    }

    @Test
    void testObtenerMedicosPorEspecialidad() {
        this.clinicaDao.guardarMedico(this.medico);
        List<Medico> medicos = this.clinicaDao.obtenerMedicosPorEspecialidad(this.especialidad);
        assertTrue(medicos.contains(this.medico));
    }


    @Test
    void testObtenerMedicosPorEspecialidadParticulares() {
        this.clinicaDao.guardarMedico(this.medico);
        List<Medico> medicos = this.clinicaDao.obtenerMedicosPorEspecialidadParticulares(especialidad);
        assertTrue(medicos.contains(this.medico));
    }

    @Test
    void testEncontrarMedicosPorEspecialidadObraSocial() {
        this.clinicaDao.guardarMedico(this.medico);
        List<Medico> medicos = this.clinicaDao.encontrarMedicosPorEspecialidadObraSocial(this.especialidad, this.obraSocial);
        assertTrue(medicos.contains(this.medico));
    }

    @Test
    void testGuardarYObtenerEspecialidad() {
        this.clinicaDao.guardarEspecialidad(this.especialidad);
        Especialidad especialidadObtenida = this.clinicaDao.obtenerEspecialidad(this.especialidad.getNombre());
        assertEquals(this.especialidad, especialidadObtenida);
    }

    @Test
    void testActualizarEspecialidad() {
        this.clinicaDao.guardarEspecialidad(this.especialidad);
        this.especialidad.setNombre("Neurología");
        this.clinicaDao.actualizarEspecialidad(this.especialidad);
        Especialidad especialidadActualizada = this.clinicaDao.obtenerEspecialidad("Neurología");
        assertEquals(this.especialidad, especialidadActualizada);
    }

    @Test
    void testEliminarEspecialidad() {
        this.clinicaDao.guardarEspecialidad(this.especialidad);
        this.clinicaDao.eliminarEspecialidad(this.especialidad);
        Especialidad especialidadEliminada = this.clinicaDao.obtenerEspecialidad(this.especialidad.getNombre());
        assertNull(especialidadEliminada);
    }

    @Test
    void testObtenerEspecialidades() {
        this.clinicaDao.guardarEspecialidad(this.especialidad);
        List<Especialidad> especialidades = this.clinicaDao.obtenerEspecialidades();
        assertTrue(especialidades.contains(this.especialidad));
    }
}