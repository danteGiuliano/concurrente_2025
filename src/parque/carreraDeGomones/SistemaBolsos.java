package parque.carreraDeGomones;

import parque.Visitante;
import util.Salida;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;


public class SistemaBolsos {
    
    private final List<Bolso> bolsos;
    private final Semaphore bolsosDisponibles;
    
    public SistemaBolsos(int cantidad) {
        this.bolsos = new ArrayList<>();
        
        for (int i = 1; i <= cantidad; i++) {
            bolsos.add(new Bolso(i));
        }
        
        this.bolsosDisponibles = new Semaphore(cantidad, true);
        
        Salida.log("SISTEMA", 
            "Sistema de Bolsos: " + cantidad + " disponibles | CARRERA GOMONES");
    }
    
    /**
     * Visitante obtiene un bolso para guardar pertenencias
     */
    public Bolso obtenerBolso(Visitante v) throws InterruptedException {
        if (!bolsosDisponibles.tryAcquire()) {
            Salida.log(v.getIdVisitante(), 
                "no hay bolsos disponibles | CARRERA GOMONES");
            return null;
        }
        
        Bolso bolso = buscarBolsoDisponible();
        bolso.setEstado(Bolso.Estado.EN_USO);
        
        Salida.log(v.getIdVisitante(), 
            "recibe " + bolso + " con llave | CARRERA GOMONES");
        
        // Simular guardar pertenencias
        Thread.sleep(1000);
        
        return bolso;
    }
    
    /**
     * Visitante devuelve el bolso
     */
    public void devolverBolso(Bolso bolso, Visitante v) {
        synchronized (bolso) {
            bolso.setEstado(Bolso.Estado.DISPONIBLE);
        }
        
        bolsosDisponibles.release();
        
        Salida.log(v.getIdVisitante(), 
            "devuelve " + bolso + " | CARRERA GOMONES");
    }
    
    /**
     * Busca el primer bolso disponible
     */
    private synchronized Bolso buscarBolsoDisponible() {
        for (Bolso b : bolsos) {
            if (b.isDisponible()) {
                return b;
            }
        }
        return bolsos.get(0); 
    }
    
    public int getCantidadTotal() {
        return bolsos.size();
    }
    
    public int getCantidadDisponible() {
        return bolsosDisponibles.availablePermits();
    }
}