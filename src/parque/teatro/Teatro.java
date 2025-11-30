package parque.teatro;

import parque.Visitante;
import util.Salida;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Teatro del parque con espectáculos periódicos.
 * Los asistentes forman grupos de 5 visitantes.
 * Capacidad total: 20 personas (4 grupos)
 */
public class Teatro {
    private final int TAMANIO_GRUPO = 5;
    private final int CANTIDAD_GRUPOS = 4;
    private final int CAPACIDAD_TOTAL = TAMANIO_GRUPO * CANTIDAD_GRUPOS;
    
    // Lock y condiciones
    private final Lock lock = new ReentrantLock(true);
    private final Condition esperandoGrupo = lock.newCondition();
    private final Condition esperandoEspectaculo = lock.newCondition();
    
    // Estado del teatro
    private int visitantesEnLobby = 0;
    private int gruposFormados = 0;
    private int visitantesDentro = 0;
    private boolean espectaculoActivo = false;
    
    // Asistentes
    private final Asistente[] asistentes;
    
    public Teatro(int cantidadAsistentes) {
        this.asistentes = new Asistente[cantidadAsistentes];
        for (int i = 0; i < cantidadAsistentes; i++) {
            asistentes[i] = new Asistente(i, this);
            asistentes[i].start();
        }
    }
    
    /**
     * Visitante intenta ingresar al teatro
     */
    public boolean intentarEntrar(Visitante v) throws InterruptedException {
        lock.lock();
        try {
            // Verificar disponibilidad
            if (visitantesDentro >= CAPACIDAD_TOTAL || espectaculoActivo) {
                Salida.log(v.getIdVisitante(), "teatro lleno o espectaculo en curso | TEATRO");
                return false;
            }
            
            visitantesEnLobby++;
            Salida.log(v.getIdVisitante(), "llega al lobby del teatro (" + visitantesEnLobby + " esperando) | TEATRO");
            
            // Notificar a asistentes que hay visitantes
            esperandoGrupo.signalAll();
            
            // Esperar hasta que un asistente lo asigne a un grupo
            while (visitantesEnLobby > 0 && !espectaculoActivo) {
                esperandoGrupo.await();
            }
            
            // Si el espectáculo inició sin poder entrar
            if (espectaculoActivo && visitantesDentro >= CAPACIDAD_TOTAL) {
                Salida.log(v.getIdVisitante(), "no pudo ingresar a tiempo | TEATRO");
                return false;
            }
            
            // Visitante dentro, espera el espectáculo
            Salida.log(v.getIdVisitante(), "dentro del teatro, espera inicio | TEATRO");
            
            while (!espectaculoActivo) {
                esperandoEspectaculo.await();
            }
            
            Salida.log(v.getIdVisitante(), "disfruta del espectáculo  | TEATRO");
            
       
            while (espectaculoActivo) {
                esperandoEspectaculo.await();
            }
            
            Salida.log(v.getIdVisitante(), "sale del teatro satisfecho | TEATRO");
            return true;
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Asistente forma un grupo de visitantes
     */
    boolean formarGrupo(int idAsistente) throws InterruptedException {
        lock.lock();
        try {
            // Esperar visitantes
            while (visitantesEnLobby < TAMANIO_GRUPO && !espectaculoActivo) {
                esperandoGrupo.await();
            }
            
            if (espectaculoActivo || gruposFormados >= CANTIDAD_GRUPOS) {
                return false;
            }
            
            // Formar grupo
            if (visitantesEnLobby >= TAMANIO_GRUPO) {
                visitantesEnLobby -= TAMANIO_GRUPO;
                gruposFormados++;
                visitantesDentro += TAMANIO_GRUPO;
                
                Salida.log("ASISTENTE-" + idAsistente, 
                    "formo grupo " + gruposFormados + " (" + TAMANIO_GRUPO + " personas) | TEATRO");
                
                esperandoGrupo.signalAll();
                

                if (gruposFormados == CANTIDAD_GRUPOS) {
                    iniciarEspectaculo();
                }
                
                return true;
            }
            
            return false;
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Inicia el espectáculo
     */
    private void iniciarEspectaculo() {
        espectaculoActivo = true;
        
        esperandoEspectaculo.signalAll();
        
        // Hilo para finalizar espectáculo
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                finalizarEspectaculo();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Espectaculo-" ).start();
    }
    
   
    private void finalizarEspectaculo() {
        lock.lock();
        try {
            Salida.log("SISTEMA", "Espectaculo  | TEATRO");
            
            espectaculoActivo = false;
            visitantesDentro = 0;
            gruposFormados = 0;
            
            esperandoEspectaculo.signalAll();
            esperandoGrupo.signalAll();
            
        } finally {
            lock.unlock();
        }
    }
}