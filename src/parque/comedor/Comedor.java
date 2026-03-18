package parque.comedor;

import parque.Visitante;
import util.Salida;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Comedor {

    private final List<Mesa> mesas;
    private final Lock lock = new ReentrantLock(true);
    private final Condition hayLugar = lock.newCondition();

    public Comedor(int cantidadMesas) {
        mesas = new ArrayList<>();
        for (int i = 0; i < cantidadMesas; i++) {
            mesas.add(new Mesa(i, this));
        }

        Salida.log("SISTEMA",
                "Comedor abierto con " + cantidadMesas + " mesas | COMEDOR");
    }

    public boolean almorzar(Visitante v) throws InterruptedException {
        Mesa mesa = buscarMesaDisponible(v);

        Salida.log(v.getIdVisitante(),
                "se dirige a la mesa N:" + mesa.getId() + " | COMEDOR");

        mesa.esperarYComer(v);
        
        Thread.sleep(5000);

        return true;
    }

    private Mesa buscarMesaDisponible(Visitante v) throws InterruptedException {
        lock.lock();
        try {
            while (true) {

                for (Mesa m : mesas) {
                    // intentoSentarse NO notificará al comedor (para evitar inversión de locks)
                    if (m.estaDisponible() && m.intentarSentarse(v)) {
                        // ya que estamos dentro de comedor.lock, podemos despertar a otros que esperan
                        // para reintentar (esto sustituye la llamada previa dentro de Mesa)
                        hayLugar.signalAll();
                        return m;
                    }
                }

                // No hay lugar  espera
                Salida.log(v.getIdVisitante(),
                        "no encuentra mesa disponible, espera... | COMEDOR");

                hayLugar.await();
            }

        } finally {
            lock.unlock();
        }
    }

    public void notificarLugarDisponible() {
        // esta función adquiere solo comedor.lock: ok
        lock.lock();
        try {
            hayLugar.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public int getCantidadMesas() {
        return mesas.size();
    }
}
