package parque.carreraDeGomones;

import parque.Visitante;
import util.Salida;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Sistema de Gomones - Gestiona asignación de gomones
 * 
 * RESPONSABILIDAD:
 * - Administrar pool de gomones individuales y dobles
 * - Asignar gomones a visitantes
 * - Liberar gomones cuando terminan
 */
public class SistemaGomones {
    
    private final List<Gomon> gomonesIndividuales;
    private final List<Gomon> gomonesDobles;
    
    private final Semaphore semaforoIndividuales;
    private final Semaphore semaforoDobles;
    
    public SistemaGomones(int cantIndividuales, int cantDobles) {
        this.gomonesIndividuales = new ArrayList<>();
        this.gomonesDobles = new ArrayList<>();
        
        // Crear gomones individuales
        for (int i = 0; i < cantIndividuales; i++) {
            gomonesIndividuales.add(new Gomon(i + 1, Gomon.Tipo.INDIVIDUAL));
        }
        
        // Crear gomones dobles
        for (int i = 0; i < cantDobles; i++) {
            gomonesDobles.add(new Gomon(i + 1, Gomon.Tipo.DOBLE));
        }
        
        this.semaforoIndividuales = new Semaphore(cantIndividuales, true);
        this.semaforoDobles = new Semaphore(cantDobles, true);
        
        Salida.log("SISTEMA", 
            "Sistema de Gomones: " + cantIndividuales + " individuales, " + 
            cantDobles + " dobles | CARRERA GOMONES");
    }
    
    /**
     * Visitante obtiene un gomón
     * Prioridad: individual primero, luego doble
     */
    public Gomon obtenerGomon(Visitante v) {
        // Intentar gomón individual primero
        if (semaforoIndividuales.tryAcquire()) {
            Gomon gomon = buscarGomonDisponible(gomonesIndividuales);
            gomon.marcarEnUso();
            
            Salida.log(v.getIdVisitante(), 
                "obtiene " + gomon + " | CARRERA GOMONES");
            return gomon;
        }
        
        // Si no hay individual, intentar doble
        if (semaforoDobles.tryAcquire()) {
            Gomon gomon = buscarGomonDisponible(gomonesDobles);
            gomon.marcarEnUso();
            
            Salida.log(v.getIdVisitante(), 
                "obtiene " + gomon + " | CARRERA GOMONES");
            return gomon;
        }
        
        // No hay gomones disponibles
        Salida.log(v.getIdVisitante(), 
            "no hay gomones disponibles | CARRERA GOMONES");
        return null;
    }
    
    /**
     * Devuelve gomón al sistema
     */
    public void devolverGomon(Gomon gomon, Visitante v) {
        gomon.marcarDisponible();
        
        if (gomon.getTipo() == Gomon.Tipo.INDIVIDUAL) {
            semaforoIndividuales.release();
        } else {
            semaforoDobles.release();
        }
        
        Salida.log(v.getIdVisitante(), 
            "devuelve " + gomon + " | CARRERA GOMONES");
    }
    
    /**
     * Busca el primer gomón disponible en la lista
     */
    private synchronized Gomon buscarGomonDisponible(List<Gomon> lista) {
        for (Gomon g : lista) {
            if (!g.isEnUso()) {
                return g;
            }
        }
        return lista.get(0);
    }
    
    public int getTotalIndividuales() {
        return gomonesIndividuales.size();
    }
    
    public int getTotalDobles() {
        return gomonesDobles.size();
    }
}