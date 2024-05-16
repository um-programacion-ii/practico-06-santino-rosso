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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    void testRun(){


    }
}
