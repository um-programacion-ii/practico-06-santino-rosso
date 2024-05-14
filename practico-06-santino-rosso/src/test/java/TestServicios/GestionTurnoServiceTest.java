package TestServicios;

import ar.edu.um.Entidades.*;
import ar.edu.um.Excepciones.TurnoIniciadoException;
import ar.edu.um.Excepciones.TurnoTerminadoException;
import ar.edu.um.Servicios.GestionTurnoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GestionTurnoServiceTest {
    Medico mockMedico;
    Especialidad mockEspecialidad;
    GestionTurnoService gestionTurnoService;
    Turno mockTurno;

    @BeforeEach
    public void preparacion() {
        this.mockMedico = mock(Medico.class);
        this.mockEspecialidad = mock(Especialidad.class);
        when(this.mockEspecialidad.getNombre()).thenReturn("Odontología");
        when(this.mockMedico.getEspecialidad()).thenReturn(this.mockEspecialidad);
        when(this.mockMedico.getNombre()).thenReturn("Dr. Jorge");
        this.gestionTurnoService = GestionTurnoService.getInstance();
        this.mockTurno = mock(Turno.class);
    }

    @Test
    void testCrearTurno() {
        Especialidad especialidad = new Especialidad("Odontologia");
        ObraSocial obraSocial1 = new ObraSocial("Sancor");
        ObraSocial obraSocial2 = new ObraSocial("Pami");
        List<ObraSocial> obraSocialesAceptadas = new ArrayList<>();
        obraSocialesAceptadas.add(obraSocial1);
        obraSocialesAceptadas.add(obraSocial2);
        Medico medico = new Medico(32914986,"Jorge", "Rodriguez",especialidad, obraSocialesAceptadas,true,true);
        Paciente paciente = new Paciente(45698712,"Alvaro","Silvado",obraSocial1, null, null);

        Turno turno = this.gestionTurnoService.crearTurno(medico, paciente, true);

        assertNotNull(turno);
        assertEquals(medico, turno.getMedico());
        assertEquals(paciente, turno.getPaciente());
        assertTrue(turno.getConObraSocial());
        assertFalse(turno.isHaComenzado());
        assertFalse(turno.isHaTerminado());
    }

    @Test
    void testIniciarTurno() {
        when(this.mockTurno.isHaComenzado()).thenReturn(false).thenCallRealMethod();
        when(this.mockTurno.getMedico()).thenReturn(this.mockMedico);

        assertDoesNotThrow(() -> this.gestionTurnoService.iniciarTurno(this.mockTurno));
        verify(this.mockTurno, times(1)).setHaComenzado(true);

    }

    @Test
    void testIniciarTurnoTurnoIniciadoException() {
        when(this.mockTurno.isHaComenzado()).thenReturn(true);

        assertThrows(TurnoIniciadoException.class, () -> {
            this.gestionTurnoService.iniciarTurno(this.mockTurno);
        });
    }

    @Test
    void testTerminarTurno() {
        when(this.mockTurno.isHaComenzado()).thenReturn(true);
        when(this.mockTurno.isHaTerminado()).thenReturn(false);
        when(this.mockTurno.getMedico()).thenReturn(this.mockMedico);

        this.gestionTurnoService.terminarTurno(this.mockTurno);

        verify(this.mockTurno, times(1)).setHaTerminado(true);
    }

    @Test
    void testTerminarTurnoTurnoIniciadoException() {
        when(this.mockTurno.isHaComenzado()).thenReturn(false);
        when(this.mockTurno.getMedico()).thenReturn(this.mockMedico);


        assertThrows(TurnoIniciadoException.class, () -> {
            this.gestionTurnoService.terminarTurno(this.mockTurno);
        });
    }

    @Test
    void testTerminarTurnoTurnoTerminadoException() {
        when(this.mockTurno.isHaComenzado()).thenReturn(true);
        when(this.mockTurno.isHaTerminado()).thenReturn(true);
        when(this.mockTurno.getMedico()).thenReturn(this.mockMedico);

        assertThrows(TurnoTerminadoException.class, () -> {
            this.gestionTurnoService.terminarTurno(this.mockTurno);
        });
    }
}
