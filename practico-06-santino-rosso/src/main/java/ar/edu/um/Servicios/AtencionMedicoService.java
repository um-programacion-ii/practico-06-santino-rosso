package ar.edu.um.Servicios;

import ar.edu.um.Entidades.Medicamento;
import ar.edu.um.Entidades.Receta;
import ar.edu.um.Excepciones.RecetaNoCreadaException;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AtencionMedicoService {
    private static AtencionMedicoService instancia;

    private AtencionMedicoService() {
    }

    public static synchronized AtencionMedicoService getInstance() {
        if (instancia == null) {
            instancia = new AtencionMedicoService();
        }
        return instancia;
    }

    public synchronized Receta crearReceta(Random random){
        if (random.nextBoolean()) {
            try {
                Map<Medicamento, Integer> medicamentos = new HashMap<>();;
                int numeroMedicamentos = random.nextInt(5);
                for (int i = 0; i < numeroMedicamentos; i++) {
                    Medicamento medicamento = new Medicamento();
                    medicamento.setNombreComercial("Medicamento " + (i + 1));
                    medicamento.setNombreDroga("Droga " + (i + 1));
                    int cantidadMedicamento = random.nextInt(8);
                    medicamentos.put(medicamento, cantidadMedicamento);
                }
                Receta receta = new Receta(medicamentos);
                return receta;
            } catch (Exception e) {
                throw new RecetaNoCreadaException("Error al crear la receta");
            }
        } else {
            return null;
        }
    }
}