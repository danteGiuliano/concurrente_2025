package parque.carreraDeGomones;

import parque.Visitante;
import util.Salida;
import java.util.concurrent.Semaphore;

/**
 * Stand de Bicicletas - Transporte al inicio del recorrido
 * 
 * RESPONSABILIDAD:
 * - Prestar bicicletas a visitantes
 * - Controlar disponibilidad
 * - Recibir bicicletas devueltas
 */
public class StandBicicletas {
    
    private final Semaphore bicicletasDisponibles;
    private final int CANTIDAD_TOTAL;
    
    public StandBicicletas(int cantidad) {
        this.CANTIDAD_TOTAL = cantidad;
        this.bicicletasDisponibles = new Semaphore(cantidad, true);
        
        Salida.log("SISTEMA", 
            "Stand de Bicicletas: " + cantidad + " disponibles | CARRERA GOMONES");
    }
    
    /**
     * Visitante usa bicicleta para llegar al inicio
     */
    public boolean usarBicicleta(Visitante v) throws InterruptedException {
        if (bicicletasDisponibles.tryAcquire()) {
            int disponibles = bicicletasDisponibles.availablePermits();
            
            Salida.log(v.getIdVisitante(), 
                "toma bicicleta (" + disponibles + "/" + CANTIDAD_TOTAL + 
                " restantes) | CARRERA GOMONES");
            
            // Simular viaje al inicio
            Thread.sleep(2000);
            
            Salida.log(v.getIdVisitante(), 
                "llega al inicio en bicicleta | CARRERA GOMONES");
            
            // Devolver bicicleta
            devolverBicicleta(v);
            
            return true;
        } else {
            Salida.log(v.getIdVisitante(), 
                "no hay bicicletas, intentará tren | CARRERA GOMONES");
            return false;
        }
    }
    
    /**
     * Devuelve bicicleta al stand
     */
    private void devolverBicicleta(Visitante v) {
        bicicletasDisponibles.release();
        
        Salida.log(v.getIdVisitante(), 
            "devuelve bicicleta | CARRERA GOMONES");
    }
    
    public int getCantidadDisponible() {
        return bicicletasDisponibles.availablePermits();
    }
    
    public int getCantidadTotal() {
        return CANTIDAD_TOTAL;
    }
}