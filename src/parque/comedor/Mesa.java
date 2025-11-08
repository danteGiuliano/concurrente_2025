package parque.comedor;

import parque.Visitante;
import util.Salida;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Mesa del comedor con capacidad para 4 personas.
 * Todos empiezan a comer al mismo tiempo cuando la mesa se llena.
 */
public class Mesa {
    private final int id;
    private final int CAPACIDAD = 4;
    
    private final Lock lock = new ReentrantLock(true);
    private final Condition mesaCompleta = lock.newCondition();
    
    private int visitantesSentados = 0;
    private boolean comiendo = false;
    
    public Mesa(int id) {
        this.id = id;
    }
    
    /**
     * Intenta sentarse en la mesa
     * @return true si logró sentarse, false si está llena
     */
    public boolean intentarSentarse(Visitante v) {
        lock.lock();
        try {
            // Si la mesa está llena o están comiendo, no puede sentarse
            if (visitantesSentados >= CAPACIDAD || comiendo) {
                return false;
            }
            
            visitantesSentados++;
            Salida.log(v.getIdVisitante(), 
                "se sienta en mesa #" + id + " (" + visitantesSentados + "/" + CAPACIDAD + ") | COMEDOR");
            
            return true;
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Espera a que la mesa se complete y come
     */
    public void esperarYComer(Visitante v) throws InterruptedException {
        lock.lock();
        try {
            // Esperar hasta que la mesa esté completa
            while (visitantesSentados < CAPACIDAD) {
                mesaCompleta.await();
            }
            
            // Si soy el último en llegar, inicio la comida
            if (!comiendo) {
                comiendo = true;
                Salida.log("SISTEMA", 
                    "Mesa #" + id + " completa, todos comienzan a comer | COMEDOR");
                mesaCompleta.signalAll(); // Despertar a todos para que coman
            }
            
            // Todos comen juntos
            Salida.log(v.getIdVisitante(), 
                "comienza a almorzar en mesa #" + id + " | COMEDOR");
            
        } finally {
            lock.unlock();
        }
        
        // Simular tiempo de comida (fuera del lock para no bloquear)
        Thread.sleep(5000);
        
        lock.lock();
        try {
            visitantesSentados--;
            Salida.log(v.getIdVisitante(), 
                "termina de comer y se levanta de mesa #" + id + " | COMEDOR");
            
            // Si soy el último en irme, resetear la mesa
            if (visitantesSentados == 0) {
                comiendo = false;
                Salida.log("SISTEMA", 
                    "Mesa #" + id + " disponible nuevamente | COMEDOR");
            }
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Notifica que la mesa está completa
     */
    public void notificarMesaCompleta() {
        lock.lock();
        try {
            if (visitantesSentados == CAPACIDAD) {
                mesaCompleta.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }
    
    public int getId() {
        return id;
    }
    
    public boolean estaDisponible() {
        lock.lock();
        try {
            return visitantesSentados < CAPACIDAD && !comiendo;
        } finally {
            lock.unlock();
        }
    }
}