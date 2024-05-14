package TestServicios;
import ar.edu.um.Entidades.Medicamento;
import ar.edu.um.Entidades.Receta;
import ar.edu.um.Excepciones.RecetaNoCreadaException;
import ar.edu.um.Servicios.AtencionMedicoService;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AtencionMedicoServiceTest {

    @Test
    void testCrearReceta() {
        Random random = mock(Random.class);
        when(random.nextBoolean()).thenReturn(true);
        AtencionMedicoService atencionMedicoService = AtencionMedicoService.getInstance();

        Receta receta = atencionMedicoService.crearReceta(random);
        assertNotNull(receta);
    }

    @Test
    void testNoCrearReceta() {
        Random random = mock(Random.class);
        when(random.nextBoolean()).thenReturn(false);
        AtencionMedicoService atencionMedicoService = AtencionMedicoService.getInstance();

        Receta receta = atencionMedicoService.crearReceta(random);
        assertNull(receta);
    }


    @Test
    void testCrearRecetaNoCreadaException() {
        Random random = mock(Random.class);
        AtencionMedicoService mockService = mock(AtencionMedicoService.class);

        when(mockService.crearReceta(random)).thenThrow(new RecetaNoCreadaException("Error al crear la receta"));

        assertThrows(RecetaNoCreadaException.class, () -> mockService.crearReceta(random));
    }
}
