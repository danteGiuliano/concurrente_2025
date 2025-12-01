package parque.teatro;

import parque.Visitante;
import util.Salida;
import java.util.*;
import java.util.concurrent.locks.*;

public class Teatro {

    private final int TAMANIO_GRUPO = 5;
    private final int TOTAL_GRUPOS = 4;

    private final Lock lock = new ReentrantLock(true);

    private final Condition hayVisitantes = lock.newCondition();
    private final Condition visitantesLiberados = lock.newCondition();
    private final Condition inicioEspectaculo = lock.newCondition();
    private final Condition finEspectaculo = lock.newCondition();

    // Cola de espera REAL
    private final Queue<Visitante> colaLobby = new LinkedList<>();

    // Visitantes habilitados a avanzar
    private final Set<Visitante> habilitados = new HashSet<>();

    // Estado
    private int gruposFormados = 0;
    private boolean espectaculoActivo = false;

    private final Asistente[] asistentes;

    public Teatro(int cantidadAsistentes) {
        asistentes = new Asistente[cantidadAsistentes];
        for (int i = 0; i < cantidadAsistentes; i++) {
            asistentes[i] = new Asistente(i, this);
            asistentes[i].start();
        }
    }


    // ================================
    //  VISITANTE INTENTA ENTRAR
    // ================================
    public boolean intentarEntrar(Visitante v) throws InterruptedException {
        lock.lock();
        try {
            // Si ya hay espectáculo no entra
            if (espectaculoActivo) {
                Salida.log(v.getIdVisitante(), "no puede entrar, espectaculo en curso | TEATRO");
                return false;
            }

            // Entra al lobby
            colaLobby.add(v);
            Salida.log(v.getIdVisitante(), "entra al lobby (" + colaLobby.size() + " en cola) | TEATRO");

            // Notifica asistentes
            hayVisitantes.signalAll();

            // Espera hasta que el Teatro lo libere para entrar al grupo
            while (!habilitados.contains(v)) {
                visitantesLiberados.await();
            }

            // Ya entró a la sala
            Salida.log(v.getIdVisitante(), "se ubica en su asiento y espera el show | TEATRO");

            // Espera inicio
            while (!espectaculoActivo) {
                inicioEspectaculo.await();
            }

            Salida.log(v.getIdVisitante(), "disfrutando del espectáculo | TEATRO");

            // Espera fin
            while (espectaculoActivo) {
                finEspectaculo.await();
            }

            Salida.log(v.getIdVisitante(), "sale satisfecho | TEATRO");
            return true;

        } finally {
            lock.unlock();
        }
    }


    // ================================
    //  ASISTENTE FORMA GRUPO
    // ================================
    boolean formarGrupo(int idAsistente) throws InterruptedException {
        lock.lock();
        try {
            if (gruposFormados >= TOTAL_GRUPOS || espectaculoActivo)
                return false;

            // Esperar hasta que haya gente suficiente
            while (colaLobby.size() < TAMANIO_GRUPO && !espectaculoActivo) {
                hayVisitantes.await();
            }

            if (espectaculoActivo) return false;

            // Sacar 5 reales de la cola
            List<Visitante> grupo = new ArrayList<>();

            for (int i = 0; i < TAMANIO_GRUPO; i++) {
                grupo.add(colaLobby.poll());
            }

            // Marcar esos visitantes como habilitados
            habilitados.addAll(grupo);

            gruposFormados++;

            Salida.log("ASISTENTE-" + idAsistente,
                    "formo grupo " + gruposFormados + " con 5 personas | TEATRO");

            // Liberar a los visitantes de ese grupo
            visitantesLiberados.signalAll();

            if (gruposFormados == TOTAL_GRUPOS) {
                iniciarEspectaculo(idAsistente);
            }

            return true;

        } finally {
            lock.unlock();
        }
    }


    // ================================
    //     INICIAR ESPECTÁCULO
    // ================================
    private void iniciarEspectaculo(int asistente) throws InterruptedException {
        espectaculoActivo = true;

        Salida.log("ASISTENTE-" + asistente, "inicia el espectáculo | TEATRO");
        inicioEspectaculo.signalAll();

              Thread.sleep(10000);
                finalizarEspectaculo(asistente);

  
    }


    // ================================
    //     FINALIZAR ESPECTÁCULO
    // ================================
    private void finalizarEspectaculo(int asistente) {
        lock.lock();
        try {
            espectaculoActivo = false;
            gruposFormados = 0;
            habilitados.clear();

            Salida.log("ASISTENTE-" + asistente, "finaliza el espectáculo | TEATRO");

            finEspectaculo.signalAll();
            hayVisitantes.signalAll(); // permitir nueva tanda

        } finally {
            lock.unlock();
        }
    }
}
