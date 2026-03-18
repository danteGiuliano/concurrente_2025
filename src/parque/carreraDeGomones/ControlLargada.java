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
    
    // El visitante se registra y espera hasta que haya G gomones listos
    public void esperarLargada(Visitante v, Gomon gomon) throws InterruptedException {
        lock.lock();
        try {
            gomonesListos++;
            
            Salida.log(v.getIdVisitante(), 
                "listo en linea de largada (" + gomonesListos + "/" + 
                GOMONES_NECESARIOS + ") | CARRERA GOMONES");
            
            // Esperar a que se complete el grupo de G gomones y se inicie la carrera
            while (gomonesListos < GOMONES_NECESARIOS || !carreraEnCurso) {
                esperandoInicio.await();
            }
            
        } finally {
            lock.unlock();
        }
    }
    
    // Cuando se alcanza G gomones, se inicia la carrera
    public synchronized void iniciarCarrera() {
        lock.lock();
        try {
            numeroCarrera++;
            carreraEnCurso = true;
            posicionLlegada.set(0);
            
            Salida.log("SISTEMA CONTROL DE LARGADA", "INICIA CARRERA | CARRERA GOMONES");
            
            gomonesListos = 0;
            // Despertar a todos los visitantes que estaban esperando
            esperandoInicio.signalAll();
        } finally {
            lock.unlock();
        }
    }
    
    // Verifica si hay suficientes gomones y lanza la carrera
    public void verificarYLanzarCarrera() {
        lock.lock();
        try {
            if (gomonesListos >= GOMONES_NECESARIOS && !carreraEnCurso) {
                iniciarCarrera();
            }
        } finally {
            lock.unlock();
        }
    }
    
    // Registra la llegada y determina posición (el primero en llegar es el ganador)
    public int registrarLlegada(Visitante v) {
        int posicion = posicionLlegada.incrementAndGet();
        
        lock.lock();
        try {
            // Si es el último en llegar, la carrera termina
            if (posicion == GOMONES_NECESARIOS) {
                carreraEnCurso = false;
                
                Salida.log("SISTEMA", 
                    "Carrera N:" + numeroCarrera + " FINALIZADA | CARRERA GOMONES");
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