package TestServicios;

import ar.edu.um.Dao.ClinicaDao;
import ar.edu.um.Entidades.*;
import ar.edu.um.Excepciones.*;
import ar.edu.um.Servicios.ClinicaService;
import ar.edu.um.Servicios.GestionTurnoService;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClinicaServiceTest {

    @Test
    void testPedirTurnoMedicosParticulares() {
        Paciente paciente = new Paciente();
        Especialidad especialidad = new Especialidad("Odontología");
        Medico medico = new Medico(64694,"jorge","rodriguez",especialidad,null,true,true);

        ClinicaDao clinicaDao = ClinicaDao.getInstance();
        clinicaDao.guardarMedico(medico);
        List<Medico> medicos = new ArrayList<>();
        medicos.add(medico);

        GestionTurnoService gestionTurnoService = mock(GestionTurnoService.class);
        when(gestionTurnoService.crearTurno(any(), any(), anyBoolean())).thenReturn(new Turno());

        ClinicaService clinicaService = ClinicaService.getInstance();
        Turno turnoPedido = clinicaService.pedirTurno(paciente, especialidad, null);

        assertEquals(medicos,clinicaDao.obtenerMedicosPorEspecialidadParticulares(especialidad));
        assertNotNull(turnoPedido);
        assertEquals(medico, turnoPedido.getMedico());
        assertEquals(paciente, turnoPedido.getPaciente());
        assertFalse(turnoPedido.getConObraSocial());
        assertNotNull(paciente.getTurnos());
    }


    @Test
    void testPedirTurnoObraSocial() {
        Paciente paciente = new Paciente();
        ObraSocial obraSocial1 = new ObraSocial("Pami");
        List<ObraSocial> obrasSociales = new ArrayList<>();
        obrasSociales.add(obraSocial1);
        Especialidad especialidad = new Especialidad("Odontología");
        Medico medico = new Medico(64694,"jorge","rodriguez",especialidad,obrasSociales,true,true);

        ClinicaDao clinicaDao = ClinicaDao.getInstance();
        clinicaDao.guardarMedico(medico);
        List<Medico> medicos = new ArrayList<>();
        medicos.add(medico);

        GestionTurnoService gestionTurnoService = mock(GestionTurnoService.class);
        when(gestionTurnoService.crearTurno(any(), any(), anyBoolean())).thenReturn(new Turno());

        ClinicaService clinicaService = ClinicaService.getInstance();
        Turno turnoPedido = clinicaService.pedirTurno(paciente, especialidad, obraSocial1);

        assertEquals(medicos,clinicaDao.encontrarMedicosPorEspecialidadObraSocial(especialidad,obraSocial1));
        assertNotNull(turnoPedido);
        assertEquals(medico, turnoPedido.getMedico());
        assertEquals(paciente, turnoPedido.getPaciente());
        assertTrue(turnoPedido.getConObraSocial());
        assertNotNull(paciente.getTurnos());
    }

    @Test
    void testPedirTurnoSinMedicosParticulares() {
        Paciente paciente = new Paciente();
        Especialidad especialidad = new Especialidad("Odontología");
        ClinicaDao clinicaDao = mock(ClinicaDao.class);
        when(clinicaDao.obtenerMedicosPorEspecialidadParticulares(especialidad)).thenReturn(Collections.emptyList());

        ClinicaService clinicaService = ClinicaService.getInstance();

        assertThrows(MedicosNoEncontradosException.class, () -> {
            clinicaService.pedirTurno(paciente, especialidad, null);
        });
    }

    @Test
    void testPedirTurnoSinMedicosObraSocial() {
        Paciente paciente = new Paciente();
        Especialidad especialidad = new Especialidad("Odontología");
        ObraSocial obraSocial1 = new ObraSocial("Pami");
        ClinicaDao clinicaDao = mock(ClinicaDao.class);
        when(clinicaDao.encontrarMedicosPorEspecialidadObraSocial(especialidad,obraSocial1)).thenReturn(Collections.emptyList());

        ClinicaService clinicaService = ClinicaService.getInstance();

        assertThrows(MedicosNoEncontradosException.class, () -> {
            clinicaService.pedirTurno(paciente, especialidad, obraSocial1);
        });
    }

    @Test
    void testPuedeIniciarTurno_HaComenzado() {
        Paciente paciente = new Paciente();
        Turno turno = mock(Turno.class);
        when(turno.isHaComenzado()).thenReturn(true);
        ClinicaService clinicaService = ClinicaService.getInstance();

        assertThrows(TurnoIniciadoException.class, () -> clinicaService.puedeIniciarTurno(turno, paciente));
    }

    @Test
    void testPuedeIniciarTurnoMedicoNoDisponible() throws InterruptedException {
        Turno turno = mock(Turno.class);
        Medico medico = mock(Medico.class);
        Paciente paciente = new Paciente();
        Especialidad especialidad = mock(Especialidad.class);

        when(medico.getEspecialidad()).thenReturn(especialidad);
        when(turno.isHaComenzado()).thenReturn(false);
        when(turno.getMedico()).thenReturn(medico);
        when(medico.isDisponible()).thenReturn(false).thenReturn(true).thenCallRealMethod();
        doAnswer(invocation -> {
            Thread.sleep(300);
            return null;
        }).when(medico).wait();

        ClinicaService clinicaService = ClinicaService.getInstance();

        assertDoesNotThrow(() -> {
            clinicaService.puedeIniciarTurno(turno, paciente);
        });

        verify(medico).wait();
        assertFalse(medico.isDisponible());
    }

    @Test
    void testPuedeIniciarTurnoMedicoDisponible() throws InterruptedException {
        Turno turno = mock(Turno.class);
        Medico medico = mock(Medico.class);
        Paciente paciente = new Paciente();
        Especialidad especialidad = mock(Especialidad.class);

        when(medico.getEspecialidad()).thenReturn(especialidad);
        when(turno.isHaComenzado()).thenReturn(false);
        when(turno.getMedico()).thenReturn(medico);
        when(medico.isDisponible()).thenReturn(true).thenCallRealMethod();

        ClinicaService clinicaService = ClinicaService.getInstance();

        assertDoesNotThrow(() -> {
            clinicaService.puedeIniciarTurno(turno, paciente);
        });

        verify(medico, never()).wait();
        assertFalse(medico.isDisponible());
    }

    @Test
    void testPuedeIniciarTurnoExcepcionEnEspera() throws InterruptedException {
        Turno turno = mock(Turno.class);
        Medico medico = mock(Medico.class);

        when(turno.isHaComenzado()).thenReturn(false);
        when(turno.getMedico()).thenReturn(medico);
        when(medico.isDisponible()).thenReturn(false);
        doThrow(InterruptedException.class).when(medico).wait();

        ClinicaService clinicaService = ClinicaService.getInstance();

        assertThrows(RuntimeException.class, () -> {
            clinicaService.puedeIniciarTurno(turno, new Paciente());
        });
    }

    @Test
    void testPuedeTerminarTurnoTurnoNoIniciado() {
        Turno turno = mock(Turno.class);
        when(turno.isHaComenzado()).thenReturn(false);

        ClinicaService clinicaService = ClinicaService.getInstance();

        assertThrows(TurnoIniciadoException.class, () -> {
            clinicaService.puedeTerminarTurno(turno);
        });
    }

    @Test
    void testPuedeTerminarTurnoTurnoTerminado() {
        Turno turno = mock(Turno.class);
        when(turno.isHaComenzado()).thenReturn(true);
        when(turno.isHaTerminado()).thenReturn(true);

        ClinicaService clinicaService = ClinicaService.getInstance();

        assertThrows(TurnoTerminadoException.class, () -> {
            clinicaService.puedeTerminarTurno(turno);
        });
    }

    @Test
    void testPuedeTerminarTurno(){
        Turno turno = mock(Turno.class);
        Medico medico = mock(Medico.class);
        Especialidad especialidad = new Especialidad("Odontologia");
        when(medico.getEspecialidad()).thenReturn(especialidad);
        when(medico.getNombre()).thenReturn("Jorge");

        when(turno.isHaComenzado()).thenReturn(false);
        when(turno.isHaTerminado()).thenReturn(false);
        when(turno.getMedico()).thenReturn(medico);

        GestionTurnoService gestionTurnoService = mock(GestionTurnoService.class);

        ClinicaService clinicaService = ClinicaService.getInstance();
        clinicaService.puedeIniciarTurno(turno,new Paciente());
        clinicaService.puedeTerminarTurno(turno);

        verify(gestionTurnoService).terminarTurno(turno);
        verify(medico).setDisponible(true);
        verify(medico).notifyAll();
    }

    @Test
    void mostrarMédicosDeUnaEspecialidadTest() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        ClinicaDao clinicaDao = ClinicaDao.getInstance();
        List<ObraSocial> obrasSociales = new ArrayList<>();
        Especialidad especialidad = new Especialidad("Odontologia");
        ObraSocial obraSocial1 = new ObraSocial("Pami");
        ObraSocial obraSocial2 = new ObraSocial("Sancor");
        obrasSociales.add(obraSocial1);
        obrasSociales.add(obraSocial2);
        Medico medico1 = new Medico();
        Medico medico2 = new Medico();
        medico1.setNombre("Juan");
        medico1.setDni(4894);
        medico1.setApellido("Rodriguez");
        medico1.setEspecialidad(especialidad);
        medico1.setObrasSocialesAceptadas(obrasSociales);
        medico2.setNombre("Alberto");
        medico2.setApellido("Rodriguez");
        medico2.setDni(464894);
        medico2.setEspecialidad(especialidad);
        medico2.setObrasSocialesAceptadas(obrasSociales);
        clinicaDao.guardarMedico(medico1);
        clinicaDao.guardarMedico(medico2);

        ClinicaService clinicaService = ClinicaService.getInstance();
        clinicaService.mostrarMédicosDeUnaEspecialidad(especialidad);

        System.setOut(System.out);

        String expected = "Los medicos de la especialidad Odontologia son:\n" +
                "Alberto, Rodriguez. Las obras sociales que acepta son: Pami, Sancor\n" +
                "Juan, Rodriguez. Las obras sociales que acepta son: Pami, Sancor";
        assertEquals(expected, outputStream.toString().trim());
    }

    @Test
    void mostrarTodosLosMedicosTest() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        ClinicaDao clinicaDao = ClinicaDao.getInstance();
        Especialidad especialidad1 = new Especialidad("Odontologia");
        Especialidad especialidad2 = new Especialidad("Cardiología");
        Medico medico1 = new Medico();
        Medico medico2 = new Medico();
        medico1.setNombre("Juan");
        medico1.setDni(4894);
        medico1.setApellido("Rodriguez");
        medico1.setEspecialidad(especialidad1);
        medico2.setNombre("Alberto");
        medico2.setApellido("Rodriguez");
        medico2.setDni(464894);
        medico2.setEspecialidad(especialidad2);
        clinicaDao.guardarMedico(medico1);
        clinicaDao.guardarMedico(medico2);

        ClinicaService clinicaService = ClinicaService.getInstance();
        clinicaService.mostrarTodosLosMedicos();

        System.setOut(System.out);

        String expected = "Todos los medicos son:\n" +
                "Alberto, Rodriguez. Especialidad: Cardiología\n" +
                "Juan, Rodriguez. Especialidad: Odontologia";
        assertEquals(expected, outputStream.toString().trim());
    }



}
