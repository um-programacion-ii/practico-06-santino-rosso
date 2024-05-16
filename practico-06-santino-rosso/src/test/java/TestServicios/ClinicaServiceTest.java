package TestServicios;

import ar.edu.um.Dao.ClinicaDao;
import ar.edu.um.Entidades.*;
import ar.edu.um.Excepciones.*;
import ar.edu.um.Servicios.ClinicaService;
import ar.edu.um.Servicios.GestionTurnoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClinicaServiceTest {
    Paciente paciente1;
    Especialidad especialidad1;
    ObraSocial obraSocial1;
    ObraSocial obraSocial2;
    List<ObraSocial> obrasSocialses;
    ClinicaDao clinicaDaoInstancia;
    ClinicaService clinicaServiceInstancia;

    List<Medico> medicos;
    GestionTurnoService gestionTurnoServiceMock;
    ClinicaDao clinicaDaoMock;
    Turno turnoMock;
    Medico medicoMock;
    Especialidad especialidadMock;
    Medico medico1;
    Medico medico2;


    @BeforeEach
    void preparar(){
        this.paciente1 = new Paciente();
        this.especialidad1 = new Especialidad("Odontología");
        this.obraSocial1 = new ObraSocial("Pami");
        this.obraSocial2 = new ObraSocial("Sancor");
        this.obrasSocialses = new ArrayList<>();
        obrasSocialses.add(obraSocial1);
        obrasSocialses.add(obraSocial2);
        this.clinicaDaoInstancia = ClinicaDao.getInstance();
        this.medicos = new ArrayList<>();
        this.clinicaServiceInstancia = ClinicaService.getInstance();
        this.gestionTurnoServiceMock = mock(GestionTurnoService.class);
        this.clinicaDaoMock = mock(ClinicaDao.class);
        this.turnoMock = mock(Turno.class);
        this.medicoMock = mock(Medico.class);
        this.especialidadMock = mock(Especialidad.class);
        this.medico1 = new Medico(64694,"Jorge","Rodriguez",this.especialidad1,this.obrasSocialses,true,true,false);
        this.medico2 = new Medico(1654236,"Alberto","Rodriguez",this.especialidad1,this.obrasSocialses,true,true,false );
        this.clinicaDaoInstancia.guardarMedico(this.medico1);
        this.clinicaDaoInstancia.guardarMedico(this.medico2);
    }

    @Test
    void testPedirTurnoMedicosParticulares() {
        this.medicos.add(this.medico1);
        this.medicos.add(this.medico2);

        GestionTurnoService gestionTurnoService = mock(GestionTurnoService.class);
        when(gestionTurnoService.crearTurno(any(), any(), anyBoolean())).thenReturn(new Turno());

        Turno turnoPedido = this.clinicaServiceInstancia.pedirTurno(this.paciente1, this.especialidad1, null);

        assertNotNull(turnoPedido);
        assertEquals(this.paciente1, turnoPedido.getPaciente());
        assertFalse(turnoPedido.getConObraSocial());
        assertNotNull(this.paciente1.getTurnos());
        assertEquals(true,turnoPedido.getMedico().getParticular());
    }


    @Test
    void testPedirTurnoObraSocial() {
        this.medicos.add(this.medico1);

        when(this.gestionTurnoServiceMock.crearTurno(any(), any(), anyBoolean())).thenReturn(new Turno());

        Turno turnoPedido = this.clinicaServiceInstancia.pedirTurno(this.paciente1, this.especialidad1, this.obraSocial1);

        assertNotNull(turnoPedido);
        assertEquals(this.paciente1, turnoPedido.getPaciente());
        assertTrue(turnoPedido.getConObraSocial());
        assertNotNull(this.paciente1.getTurnos());
        assertTrue(turnoPedido.getConObraSocial());
    }

    @Test
    void testPedirTurnoSinMedicosParticulares() {
        clinicaDaoInstancia.eliminarMedico(64694);
        clinicaDaoInstancia.eliminarMedico(1654236);
        when(this.clinicaDaoMock.obtenerMedicosPorEspecialidadParticulares(this.especialidad1)).thenReturn(Collections.emptyList());

        assertThrows(MedicosNoEncontradosException.class, () -> {
            this.clinicaServiceInstancia.pedirTurno(this.paciente1, this.especialidad1, null);
        });
    }

    @Test
    void testPedirTurnoSinMedicosObraSocial() {
        clinicaDaoInstancia.eliminarMedico(64694);
        clinicaDaoInstancia.eliminarMedico(1654236);
        when(this.clinicaDaoMock.encontrarMedicosPorEspecialidadObraSocial(this.especialidad1,this.obraSocial1)).thenReturn(Collections.emptyList());

        assertThrows(MedicosNoEncontradosException.class, () -> {
            this.clinicaServiceInstancia.pedirTurno(this.paciente1, this.especialidad1, this.obraSocial1);
        });
    }

    @Test
    void testPuedeIniciarTurno_HaComenzado() {
        when(this.turnoMock.isHaComenzado()).thenReturn(true);

        assertThrows(TurnoIniciadoException.class, () -> this.clinicaServiceInstancia.puedeIniciarTurno(this.turnoMock, this.paciente1));
    }

    @Test
    void testPuedeIniciarTurnoMedicoNoDisponible() throws InterruptedException {
        when(this.medicoMock.getEspecialidad()).thenReturn(this.especialidadMock);
        when(this.turnoMock.isHaComenzado()).thenReturn(false);
        when(this.turnoMock.getMedico()).thenReturn(this.medicoMock);
        when(this.medicoMock.isDisponible()).thenReturn(false).thenReturn(true).thenCallRealMethod();
        doAnswer(invocation -> {
            Thread.sleep(300);
            return null;
        }).when(this.medicoMock).wait();

        assertDoesNotThrow(() -> {
            this.clinicaServiceInstancia.puedeIniciarTurno(this.turnoMock, this.paciente1);
        });

        verify(this.medicoMock).wait();
        assertFalse(this.medicoMock.isDisponible());
    }

    @Test
    void testPuedeIniciarTurnoMedicoDisponible() throws InterruptedException {
        when(this.medicoMock.getEspecialidad()).thenReturn(this.especialidadMock);
        when(this.turnoMock.isHaComenzado()).thenReturn(false);
        when(this.turnoMock.getMedico()).thenReturn(this.medicoMock);
        when(this.medicoMock.isDisponible()).thenReturn(true).thenCallRealMethod();

        assertDoesNotThrow(() -> {
            this.clinicaServiceInstancia.puedeIniciarTurno(this.turnoMock, this.paciente1);
        });

        verify(this.medicoMock, never()).wait();
        assertFalse(this.medicoMock.isDisponible());
    }

    @Test
    void testPuedeIniciarTurnoExcepcionEnEspera() throws InterruptedException {
        when(this.turnoMock.isHaComenzado()).thenReturn(false);
        when(this.turnoMock.getMedico()).thenReturn(this.medicoMock);
        when(this.medicoMock.isDisponible()).thenReturn(false);
        doThrow(InterruptedException.class).when(this.medicoMock).wait();

        assertThrows(RuntimeException.class, () -> {
            this.clinicaServiceInstancia.puedeIniciarTurno(this.turnoMock, new Paciente());
        });
    }

    @Test
    void testPuedeTerminarTurnoTurnoNoIniciado() {
        when(this.turnoMock.isHaComenzado()).thenReturn(false);

        assertThrows(TurnoIniciadoException.class, () -> {
            this.clinicaServiceInstancia.puedeTerminarTurno(this.turnoMock);
        });
    }

    @Test
    void testPuedeTerminarTurnoTurnoTerminado() {
        when(this.turnoMock.isHaComenzado()).thenReturn(true);
        when(this.turnoMock.isHaTerminado()).thenReturn(true);

        assertThrows(TurnoTerminadoException.class, () -> {
            this.clinicaServiceInstancia.puedeTerminarTurno(this.turnoMock);
        });
    }

    @Test
    void testPuedeTerminarTurno(){
        when(this.medicoMock.getEspecialidad()).thenReturn(this.especialidad1);
        when(this.medicoMock.getNombre()).thenReturn("Jorge");

        when(this.turnoMock.isHaComenzado()).thenReturn(false);
        when(this.turnoMock.isHaTerminado()).thenReturn(false);
        when(this.turnoMock.getMedico()).thenReturn(this.medicoMock);

        ClinicaService clinicaService = ClinicaService.getInstance();
        clinicaService.puedeIniciarTurno(this.turnoMock,this.paciente1);
        clinicaService.puedeTerminarTurno(this.turnoMock);

        verify(this.gestionTurnoServiceMock).terminarTurno(this.turnoMock);
        verify(this.medicoMock).setDisponible(true);
        verify(this.medicoMock).notifyAll();
    }

    @Test
    void mostrarMédicosDeUnaEspecialidadTest() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        this.clinicaServiceInstancia.mostrarMédicosDeUnaEspecialidad(this.especialidad1);

        System.setOut(System.out);

        String expected = "Los medicos de la especialidad Odontología son:\n" +
                "Alberto, Rodriguez. Las obras sociales que acepta son: Pami, Sancor\n" +
                "Jorge, Rodriguez. Las obras sociales que acepta son: Pami, Sancor";
        assertEquals(expected, outputStream.toString().trim());
    }

    @Test
    void mostrarTodosLosMedicosTest() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        Especialidad especialidad2 = new Especialidad("Cardiología");
        Medico medico2 = new Medico();
        medico2.setNombre("Raul");
        medico2.setApellido("Sanchez");
        medico2.setDni(464894);
        medico2.setEspecialidad(especialidad2);
        this.clinicaDaoInstancia.guardarMedico(medico2);

        this.clinicaServiceInstancia.mostrarTodosLosMedicos();

        System.setOut(System.out);

        String expected = "Todos los medicos son:\n" +
                "Alberto, Rodriguez. Especialidad: Odontología\n" +
                "Jorge, Rodriguez. Especialidad: Odontología\n" +
                "Raul, Sanchez. Especialidad: Cardiología";
        assertEquals(expected, outputStream.toString().trim());
    }



}
