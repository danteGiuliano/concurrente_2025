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
        Gomon gomon = null;
        
        if (semaforoIndividuales.tryAcquire()) {
            gomon = buscarYMarcarGomon(gomonesIndividuales);
        } else if (semaforoDobles.tryAcquire()) {
            gomon = buscarYMarcarGomon(gomonesDobles);
        }
        
        if (gomon != null) {
            Salida.log(v.getIdVisitante(), 
                "obtiene " + gomon + " | CARRERA GOMONES");
        } else {
            Salida.log(v.getIdVisitante(), 
                "no hay gomones disponibles | CARRERA GOMONES");
        }
        return gomon;
    }
    
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
     * Busca y marca el gomón de forma atómica
     */
    private synchronized Gomon buscarYMarcarGomon(List<Gomon> lista) {
        for (Gomon g : lista) {
            if (g.markEnUso()) {
                return g;
            }
        }
        return null;
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