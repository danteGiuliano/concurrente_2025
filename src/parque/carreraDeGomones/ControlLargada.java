package parque.carreraDeGomones;

import parque.Visitante;
import util.Salida;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ControlLargada {
    
    private final int GOMONES_NECESARIOS; 
    
    private final Lock lock;
    private final Condition esperandoInicio;
    
    private int gomonesListos;
    private int numeroCarrera;
    private AtomicInteger posicionLlegada;
    private boolean carreraEnCurso;
    
    public ControlLargada(int gomonesNecesarios) {
        this.GOMONES_NECESARIOS = gomonesNecesarios;
        this.lock = new ReentrantLock(true);
        this.esperandoInicio = lock.newCondition();
        this.gomonesListos = 0;
        this.numeroCarrera = 0;
        this.posicionLlegada = new AtomicInteger(0);
        this.carreraEnCurso = false;
        
        Salida.log("SISTEMA", 
            "Control de Largada: " + GOMONES_NECESARIOS + " gomones por carrera | CARRERA GOMONES");
    }
    
    /**
     * Visitante espera hasta que se complete el grupo de largada
     */
    public void esperarLargada(Visitante v, Gomon gomon) throws InterruptedException {
        lock.lock();
        try {
            gomonesListos++;
            
            Salida.log(v.getIdVisitante(), 
                "listo en línea de largada (" + gomonesListos + "/" + 
                GOMONES_NECESARIOS + ") | CARRERA GOMONES");
            
            // Esperar hasta completar G gomones
            while (gomonesListos < GOMONES_NECESARIOS) {
                esperandoInicio.await();
            }
            
            // Si soy el último en llegar, inicio la carrera
            if (!carreraEnCurso) {
                iniciarCarrera();
            }
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Inicia la carrera cuando se alcanza G gomones
     */
    private void iniciarCarrera() {
        numeroCarrera++;
        carreraEnCurso = true;
        posicionLlegada.set(0);
        
        Salida.log("SISTEMA CONTROL DE LARGADA", "INICIA CARRERA | CARRERA GOMONES");
        
        // Despertar a todos los participantes
        esperandoInicio.signalAll();
        
        // Resetear para próxima carrera
        gomonesListos = 0;
    }
    
    /**
     * Registra la llegada de un visitante
     * @return posición en la que llegó 
     */
    public int registrarLlegada(Visitante v) {
        int posicion = posicionLlegada.incrementAndGet();
        
        // Si es el último en llegar, resetear estado
        lock.lock();
        try {
            if (posicion == GOMONES_NECESARIOS) {
                carreraEnCurso = false;
                
                Salida.log("SISTEMA", 
                    "Carrera #" + numeroCarrera + " FINALIZADA | CARRERA GOMONES");
            }
        } finally {
            lock.unlock();
        }
        
        return posicion;
    }
    
    public int getNumeroCarrera() {
        return numeroCarrera;
    }
    
    public boolean isCarreraEnCurso() {
        return carreraEnCurso;
    }
}