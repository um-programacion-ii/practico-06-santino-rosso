package TestServicios;

import ar.edu.um.Dao.ClinicaDao;
import ar.edu.um.Entidades.*;
import ar.edu.um.Excepciones.EspecialidadNoEncontradaException;
import ar.edu.um.Excepciones.MedicamentosNoDisponiblesException;
import ar.edu.um.Servicios.ClinicaService;
import ar.edu.um.Servicios.GestionFarmaciaService;
import ar.edu.um.Servicios.PacienteService;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PacienteServiceTest {

    @Test
    void testRunEspecialidadNoEncontrada() {
        Paciente paciente = mock(Paciente.class);
        ClinicaDao clinicaDao = mock(ClinicaDao.class);
        when(clinicaDao.obtenerEspecialidad(anyString())).thenReturn(null);

        ByteArrayInputStream in = new ByteArrayInputStream("Odonto".getBytes());
        System.setIn(in);

        assertThrows(EspecialidadNoEncontradaException.class, () -> {
            PacienteService pacienteService = new PacienteService(paciente);
            pacienteService.run();
        });

        System.setIn(System.in);
    }

    @Test
    void testRun_MedicamentosNoDisponibles() {
        Paciente paciente = mock(Paciente.class);
        Especialidad especialidad = new Especialidad("Odontologia");
        GestionFarmaciaService gestionFarmaciaService = mock(GestionFarmaciaService.class);
        ClinicaService clinicaService = mock(ClinicaService.class);
        ClinicaDao clinicaDao = ClinicaDao.getInstance();
        clinicaDao.guardarEspecialidad(1,especialidad);


        ByteArrayInputStream in = new ByteArrayInputStream("Odontologia".getBytes());
        System.setIn(in);
        when(clinicaService.pedirTurno(any(), any(), any())).thenReturn(new Turno());
        when(clinicaService.puedeIniciarTurno(any(), any())).thenReturn(new Receta());
        when(gestionFarmaciaService.ventaMedicamentos(any())).thenReturn(null);

        assertThrows(MedicamentosNoDisponiblesException.class, () -> {
            PacienteService pacienteService = new PacienteService(paciente);
            pacienteService.run();
        });

        System.setIn(System.in);
    }

    @Test
    void testRun_SinReceta() {
        Paciente paciente = mock(Paciente.class);
        Especialidad especialidad = new Especialidad("Odontología");
        ClinicaDao clinicaDao = mock(ClinicaDao.class);
        ClinicaService clinicaService = mock(ClinicaService.class);

        when(clinicaDao.obtenerEspecialidad(anyString())).thenReturn(especialidad);
        when(clinicaService.pedirTurno(any(), any(), any())).thenReturn(new Turno());
        when(clinicaService.puedeIniciarTurno(any(), any())).thenReturn(null);

        PacienteService pacienteService = new PacienteService(paciente);
        pacienteService.run();

        assertNull(paciente.getRecetas());
    }

    @Test
    void testRun_ConReceta() {
        Paciente paciente = mock(Paciente.class);
        Especialidad especialidad = new Especialidad("Odontología");
        ClinicaDao clinicaDao = mock(ClinicaDao.class);
        GestionFarmaciaService gestionFarmaciaService = mock(GestionFarmaciaService.class);
        ClinicaService clinicaService = mock(ClinicaService.class);

        when(clinicaDao.obtenerEspecialidad(anyString())).thenReturn(especialidad);
        when(clinicaService.pedirTurno(any(), any(), any())).thenReturn(new Turno());
        when(clinicaService.puedeIniciarTurno(any(), any())).thenReturn(new Receta());

        Map<Medicamento, Integer> medicamentos = new HashMap<>();
        medicamentos.put(new Medicamento(), 1);
        when(gestionFarmaciaService.ventaMedicamentos(any())).thenReturn(medicamentos);

        PacienteService pacienteService = new PacienteService(paciente);
        pacienteService.run();

        assertNotNull(paciente.getRecetas());
    }

    @Test
    void testRun_ConMedicamentosDisponibles() {
        Paciente paciente = mock(Paciente.class);
        Especialidad especialidad = new Especialidad("Odontología");
        ClinicaDao clinicaDao = mock(ClinicaDao.class);
        GestionFarmaciaService gestionFarmaciaService = mock(GestionFarmaciaService.class);
        ClinicaService clinicaService = mock(ClinicaService.class);
        Map<Medicamento, Integer> medicamentos = new HashMap<>();
        medicamentos.put(new Medicamento(), 5);

        when(clinicaDao.obtenerEspecialidad(anyString())).thenReturn(especialidad);
        when(clinicaService.pedirTurno(any(), any(), any())).thenReturn(new Turno());
        when(clinicaService.puedeIniciarTurno(any(), any())).thenReturn(new Receta(medicamentos));

        PacienteService pacienteService = new PacienteService(paciente);
        pacienteService.run();

        assertDoesNotThrow(() -> pacienteService.run());
    }
}
