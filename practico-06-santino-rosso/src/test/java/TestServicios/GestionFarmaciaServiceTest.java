package TestServicios;

import ar.edu.um.Entidades.Medicamento;
import ar.edu.um.Entidades.Receta;
import ar.edu.um.Servicios.GestionFarmaciaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class GestionFarmaciaServiceTest {

    @Test
    void testVentaMedicamentos() {
        GestionFarmaciaService gestionFarmaciaService = GestionFarmaciaService.getInstance();

        Map<Medicamento, Integer> medicamentosRecetados = new HashMap<>();
        Medicamento medicamento1 = new Medicamento("Paracetamol", "Acetaminofén");
        Medicamento medicamento2 = new Medicamento("Ibuprofeno", "Ibuprofeno");
        medicamentosRecetados.put(medicamento1, 2);
        medicamentosRecetados.put(medicamento2, 1);
        Receta receta = new Receta(medicamentosRecetados);

        Map<Medicamento, Integer> medicamentosVendidos = gestionFarmaciaService.ventaMedicamentos(receta);

        assertNotNull(medicamentosVendidos);
        assertEquals(medicamentosRecetados.size(), medicamentosVendidos.size());
        for (Map.Entry<Medicamento, Integer> entry : medicamentosRecetados.entrySet()) {
            Medicamento medicamentoRecetado = entry.getKey();
            Integer cantidadRecetada = entry.getValue();
            assertTrue(medicamentosVendidos.containsKey(medicamentoRecetado));
            assertEquals(cantidadRecetada, medicamentosVendidos.get(medicamentoRecetado));
        }
    }
}
