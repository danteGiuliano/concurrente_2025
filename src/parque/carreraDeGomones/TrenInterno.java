package parque.carreraDeGomones;

import parque.Visitante;
import util.Salida;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

/**
 * Tren Interno - Transporte al inicio del recorrido
 * 
 * RESPONSABILIDAD:
 * - Transportar visitantes al inicio
 * - Capacidad máxima 15 personas
 * - Usar CyclicBarrier para salida sincronizada
 * - Partir cuando se llena o por timeout
 */
public class TrenInterno extends Thread {
    
    private static final int CAPACIDAD_MAXIMA = 15;
    private static final int TIEMPO_ESPERA_MS = 8000;
    
    private final Semaphore asientos;
    private final Semaphore mutexEmbarque;
    
    private CyclicBarrier barrierSalida;
    private int pasajerosAbordo;
    private int numeroViaje;
    private boolean operativo;
    
    public TrenInterno() {
        super("TrenInterno");
        this.asientos = new Semaphore(CAPACIDAD_MAXIMA, true);
        this.mutexEmbarque = new Semaphore(1, true);
        this.pasajerosAbordo = 0;
        this.numeroViaje = 0;
        this.operativo = true;
        
        inicializarBarrier();
        
        Salida.log("SISTEMA", 
            "Tren Interno operativo - Cap: " + CAPACIDAD_MAXIMA + " | CARRERA GOMONES");
    }
    
    /**
     * Inicializa la barrera con acción al completarse
     */
    private void inicializarBarrier() {
        barrierSalida = new CyclicBarrier(CAPACIDAD_MAXIMA, () -> {
            Salida.log("TREN", "═══════════════════════════════════════");
            Salida.log("TREN", "  VIAJE N" + (++numeroViaje) + " - TREN COMPLETO");
            Salida.log("TREN", "  Partiendo con " + CAPACIDAD_MAXIMA + " pasajeros");
            Salida.log("TREN", "═══════════════════════════════════════");
        });
    }
    
    @Override
    public void run() {
        Salida.log("TREN", "inicia operaciones | CARRERA GOMONES");
        
        while (operativo) {
            try {
                // Esperar a que se acumulen pasajeros
                Thread.sleep(TIEMPO_ESPERA_MS);
                
                mutexEmbarque.acquire();
                try {
                    // Si hay pasajeros pero no se llenó, partir igual
                    if (pasajerosAbordo > 0 && pasajerosAbordo < CAPACIDAD_MAXIMA) {
                        partirConPasajerosIncompleto();
                    }
                } finally {
                    mutexEmbarque.release();
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                operativo = false;
            }
        }
        
        Salida.log("TREN", "finaliza operaciones | CARRERA GOMONES");
    }
    
    /**
     * Visitante intenta viajar en el tren
     */
    public boolean viajar(Visitante v) throws InterruptedException {
        // Intentar obtener asiento
        if (!asientos.tryAcquire()) {
            Salida.log(v.getIdVisitante(), 
                "tren completo, no puede abordar | CARRERA GOMONES");
            return false;
        }
        
        // Embarcar
        mutexEmbarque.acquire();
        try {
            pasajerosAbordo++;
            
            Salida.log(v.getIdVisitante(), 
                "aborda el tren (" + pasajerosAbordo + "/" + CAPACIDAD_MAXIMA + 
                ") | CARRERA GOMONES");
            
        } finally {
            mutexEmbarque.release();
        }
        
        try {
            // Esperar salida (barrier o timeout)
            if (pasajerosAbordo == CAPACIDAD_MAXIMA) {
                // Tren completo, usar barrier
                barrierSalida.await();
            } else {
                // Esperar a que el tren parta
                esperarSalida();
            }
            
            // Simular viaje
            realizarViaje(v);
            
            // Desembarcar
            desembarcar(v);
            
            return true;
            
        } catch (BrokenBarrierException e) {
            Salida.log(v.getIdVisitante(), 
                "ERROR en barrier del tren | CARRERA GOMONES");
            return false;
        }
    }
    
    /**
     * Espera a que el tren parta (por timeout o llenado)
     */
    private void esperarSalida() throws InterruptedException {
        while (pasajerosAbordo < CAPACIDAD_MAXIMA && operativo) {
            Thread.sleep(500);
            
            // Verificar si el tren partió por timeout
            if (pasajerosAbordo == 0) {
                break;
            }
        }
    }
    
    /**
     * Realiza el viaje al inicio
     */
    private void realizarViaje(Visitante v) throws InterruptedException {
        Salida.log(v.getIdVisitante(), 
            "viaja en tren hacia el inicio | CARRERA GOMONES");
        
        Thread.sleep(3000);
        
        Salida.log(v.getIdVisitante(), 
            "llega al inicio en tren | CARRERA GOMONES");
    }
    
    /**
     * Desembarca del tren
     */
    private void desembarcar(Visitante v) throws InterruptedException {
        mutexEmbarque.acquire();
        try {
            pasajerosAbordo--;
            asientos.release();
            
            Salida.log(v.getIdVisitante(), 
                "desembarca del tren | CARRERA GOMONES");
            
            // Si soy el último, resetear para próximo viaje
            if (pasajerosAbordo == 0) {
                Salida.log("TREN", 
                    "todos desembarcaron, listo para nuevo viaje | CARRERA GOMONES");
                inicializarBarrier();
            }
        } finally {
            mutexEmbarque.release();
        }
    }
    
    /**
     * Partir con pasajeros incompleto (por timeout)
     */
    private void partirConPasajerosIncompleto() {
        numeroViaje++;
        
        Salida.log("TREN", 
            "parte con " + pasajerosAbordo + " pasajeros (timeout) - Viaje N" + 
            numeroViaje + " | CARRERA GOMONES");
        
        // Resetear barrier para próximo grupo
        inicializarBarrier();
    }
    
    /**
     * Detiene el tren
     */
    public void detener() {
        operativo = false;
        this.interrupt();
    }
    
    public int getPasajerosAbordo() {
        return pasajerosAbordo;
    }
    
    public int getNumeroViaje() {
        return numeroViaje;
    }
}