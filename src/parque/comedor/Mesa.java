package parque.comedor;

import parque.Visitante;
import util.Salida;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Mesa {

    private final int id;
    private final int CAPACIDAD = 4;

    private final Lock lock = new ReentrantLock(true);
    private final Condition mesaCompleta = lock.newCondition();

    private int visitantesSentados = 0;
    private boolean comiendo = false;

    private final Comedor comedor;

    public Mesa(int id, Comedor comedor) {
        this.id = id;
        this.comedor = comedor;
    }

    public boolean intentarSentarse(Visitante v) {
        lock.lock();
        try {
            if (visitantesSentados >= CAPACIDAD || comiendo) {
                return false;
            }

            visitantesSentados++;

            Salida.log(v.getIdVisitante(),
                    "se sienta en mesa N:" + id + " (" + visitantesSentados + "/" + CAPACIDAD + ") | COMEDOR");

            // avisamos a quienes esperan en la mesa (por si con esto se completa)
            mesaCompleta.signalAll();

            // **IMPORTANTE**: NO llamar a comedor.notificarLugarDisponible() desde aquí
            // porque eso intentaría tomar comedor.lock mientras se tiene mesa.lock
            // → riesgo de inversión de locks / deadlock.

            return true;

        } finally {
            lock.unlock();
        }
    }

    public void esperarYComer(Visitante v) throws InterruptedException {
        // esperar a que la mesa esté completa (política original)
        lock.lock();
        try {
            while (visitantesSentados < CAPACIDAD) {
                mesaCompleta.await();
            }

            if (!comiendo) {
                comiendo = true;
                Salida.log("SISTEMA",
                        "Mesa N:" + id + " completa → todos comienzan a comer | COMEDOR");

                // avisamos a los demás de la mesa que ya pueden proceder
                mesaCompleta.signalAll();
            }

            Salida.log(v.getIdVisitante(),
                    "comienza a almorzar en mesa N:" + id + " | COMEDOR");

        } finally {
            lock.unlock();
        }

        // simulamos comida (fuera del lock de mesa)
        Thread.sleep(5000);

        boolean debeNotificarComedor = false;

        lock.lock();
        try {
            visitantesSentados--;

            Salida.log(v.getIdVisitante(),
                    "termina de comer y se levanta de mesa N:" + id + " | COMEDOR");

            if (visitantesSentados == 0) {
                comiendo = false;

                Salida.log("SISTEMA",
                        "Mesa N:" + id + " ahora esta libre | COMEDOR");

                // marcamos la necesidad de notificar AL COMEDOR, pero lo haremos fuera del lock
                debeNotificarComedor = true;
            }

        } finally {
            lock.unlock();
        }

        if (debeNotificarComedor) {
            // llamada fuera del lock de mesa: evita inversión de locks
            comedor.notificarLugarDisponible();
        }
    }

    public boolean estaDisponible() {
        lock.lock();
        try {
            return visitantesSentados < CAPACIDAD && !comiendo;
        } finally {
            lock.unlock();
        }
    }

    public int getId() {
        return id;
    }
}
