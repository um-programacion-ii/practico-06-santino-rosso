package ar.edu.um.Servicios;


import ar.edu.um.Entidades.Medicamento;
import ar.edu.um.Entidades.Receta;

import java.util.Map;

public class GestionFarmaciaService {
    private static GestionFarmaciaService instancia;



    private GestionFarmaciaService() {
    }

    public static synchronized GestionFarmaciaService getInstance() {
        if (instancia == null) {
            instancia = new GestionFarmaciaService();
        }
        return instancia;
    }

    public synchronized Map<Medicamento, Integer> ventaMedicamentos(Receta receta){
        Map<Medicamento, Integer> medicamentosVendidos = receta.getMedicamentos();
        return medicamentosVendidos;
    }
}
