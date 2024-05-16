package ar.edu.um.Servicios;

import ar.edu.um.Dao.ClinicaDao;
import ar.edu.um.Entidades.*;
import ar.edu.um.Excepciones.EspecialidadNoEncontradaException;
import ar.edu.um.Excepciones.MedicamentosNoDisponiblesException;

import java.util.Map;
import java.util.Scanner;

public class PacienteService implements Runnable{
    private Paciente paciente;
    private ClinicaDao clinicaDao;
    ClinicaService clinicaService;
    GestionFarmaciaService gestionFarmaciaService;



    public PacienteService(Paciente paciente){
        this.paciente = paciente;
        this.clinicaDao = ClinicaDao.getInstance();
        this.clinicaService = ClinicaService.getInstance();
        this.gestionFarmaciaService = GestionFarmaciaService.getInstance();
    }

    @Override
    public void run(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese la especialidad solicitada para el turno: ");
        String especialidadTexto = scanner.nextLine();
        Especialidad especialidad = this.clinicaDao.obtenerEspecialidad(especialidadTexto);
        if (especialidad == null) {
            throw new EspecialidadNoEncontradaException("Especialidad no encontrada.");
        }
        Turno turno = clinicaService.pedirTurno(this.paciente, especialidad, this.paciente.getObraSocial());
        Receta receta = clinicaService.puedeIniciarTurno(turno,this.paciente);
        clinicaService.puedeTerminarTurno(turno);
        if (receta != null){
            Map<Medicamento, Integer> medicamentosComprados= gestionFarmaciaService.ventaMedicamentos(receta);
            if (medicamentosComprados.isEmpty()) {
                throw new MedicamentosNoDisponiblesException("No se pudieron comprar los medicamentos.");
            }
            System.out.println("Los medicamentos comprados son:");
            for (Map.Entry<Medicamento, Integer> entry : medicamentosComprados.entrySet()) {
                Medicamento medicamento = entry.getKey();
                Integer cantidad = entry.getValue();
                System.out.println("Medicamento: " + medicamento.getNombreComercial() + ", Cantidad: " + cantidad);
            }
        }
    }
}
