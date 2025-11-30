package parque.comedor;

import parque.Visitante;
import util.Salida;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Comedor del parque con múltiples mesas.
 * Los visitantes se sientan en mesas de 4 personas.
 * Todos en una mesa empiezan a comer al mismo tiempo cuando la mesa se llena
 */
public class Comedor {
    private final List<Mesa> mesas;
    private final Lock lock = new ReentrantLock(true);
    
    public Comedor(int cantidadMesas) {
        this.mesas = new ArrayList<>();
        for (int i = 0; i < cantidadMesas; i++) {
            mesas.add(new Mesa(i));
        }
        Salida.log("SISTEMA", "Comedor abierto con " + cantidadMesas + " mesas | COMEDOR");
    }
    
    /**
     * Visitante intenta almorzar en el comedor
     * @return true si pudo comer, false si está lleno y se fue
     */
    public boolean almorzar(Visitante v) throws InterruptedException {
        Mesa mesaAsignada = buscarMesaDisponible(v);
        
        if (mesaAsignada == null) {
            Salida.log(v.getIdVisitante(), 
                "comedor lleno, se va sin comer | COMEDOR");
            return false;
        }
        
   
        mesaAsignada.esperarYComer(v);
        
   
        mesaAsignada.notificarMesaCompleta();
        
        return true;
    }
    

    private Mesa buscarMesaDisponible(Visitante v) {
        lock.lock();
        try {
            // Buscar una mesa con espacio
            for (Mesa mesa : mesas) {
                if (mesa.estaDisponible() && mesa.intentarSentarse(v)) {
                    return mesa;
                }
            }
            
         
            return null;
            
        } finally {
            lock.unlock();
        }
    }
    

    public int getCantidadMesas() {
        return mesas.size();
    }
}