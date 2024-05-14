package ar.edu.um.Dao;

import ar.edu.um.Entidades.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClinicaDao {

    private Map<Integer, Paciente> pacientes;
    private Map<Integer, Medico> medicos;
    private Map<Integer, Especialidad> especialidades;
    private static ClinicaDao instancia;

    private ClinicaDao() {
        this.pacientes = new HashMap<>();
        this.medicos = new HashMap<>();
        this.especialidades = new HashMap<>();
    }

    public static synchronized ClinicaDao getInstance() {
        if (instancia == null) {
            instancia = new ClinicaDao();
        }
        return instancia;
    }



//----------------------------Paciente--------------------------//
    public synchronized void guardarPaciente(Paciente paciente) {
        this.pacientes.put(paciente.getDni(), paciente);
    }

    public synchronized void guardarTurnoPaciente(Paciente paciente, Turno turno){
        List<Turno> turnosPaciente = new ArrayList<>();
        if (paciente.getTurnos() != null){
           turnosPaciente = paciente.getTurnos();
        }
        turnosPaciente.add(turno);
        paciente.setTurnos(turnosPaciente);
        actualizarPaciente(paciente);
    }

    public synchronized void guardarRecetaPaciente(Paciente paciente, Receta receta){
        List<Receta> recetasPaciente = new ArrayList<>();
        if (paciente.getRecetas() != null){
            recetasPaciente = paciente.getRecetas();
        }
        recetasPaciente.add(receta);
        paciente.setRecetas(recetasPaciente);
        actualizarPaciente(paciente);
    }

    public synchronized Paciente obtenerPaciente(int dni) {
        return this.pacientes.get(dni);
    }

    public synchronized void actualizarPaciente(Paciente paciente) {
        this.pacientes.put(paciente.getDni(), paciente);
    }

    public synchronized void eliminarPaciente(int dni) {
        this.pacientes.remove(dni);
    }
//------------------------------------------------------------//
// ----------------------------Medico--------------------------//
    public synchronized void guardarMedico(Medico medico) {
    this.medicos.put(medico.getDni(), medico);
}

    public synchronized Medico obtenerMedico(int dni) {
        return this.medicos.get(dni);
    }

    public synchronized void actualizarMedico(Medico medico) {
        this.medicos.put(medico.getDni(), medico);
    }

    public synchronized void eliminarMedico(int dni) {
        this.medicos.remove(dni);
    }

    public synchronized List<Medico> obtenerMedicos(){
        return new ArrayList<>(this.medicos.values());
    }

    public synchronized List<Medico> obtenerMedicosPorEspecialidad(Especialidad especialidad) {
        List<Medico> medicos = obtenerMedicos();
        return medicos.stream()
                .filter(m -> m.getEspecialidad().equals(especialidad))
                .collect(Collectors.toList());
    }

    public synchronized List<Medico> obtenerMedicosPorEspecialidadParticulares(Especialidad especialidad) {
        List<Medico> medicos = obtenerMedicosPorEspecialidad(especialidad);
        return medicos.stream()
                .filter(m -> m.getParticular().equals(true))
                .collect(Collectors.toList());
    }

    public synchronized List<Medico> encontrarMedicosPorEspecialidadObraSocial(Especialidad especialidad, ObraSocial obraSocial) {
        List<Medico> medicos = obtenerMedicosPorEspecialidad(especialidad);
        return medicos.stream()
                .filter(m -> m.getObrasSocialesAceptadas().contains(obraSocial))
                .collect(Collectors.toList());
    }
//------------------------------------------------------------//
// ----------------------------Especialidad--------------------------//
    public Especialidad obtenerEspecialidad(String nombre) {
        for (Especialidad especialidad : this.especialidades.values()) {
            if (especialidad.getNombre().equalsIgnoreCase(nombre)) {
                return especialidad;
            }
        }
        return null;
    }

    public void guardarEspecialidad(int clave, Especialidad especialidad) {
        especialidades.put(clave, especialidad);
    }




}
