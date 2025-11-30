package parque.carreraDeGomones;

import parque.Visitante;
import util.Salida;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.List;

public class TrenInterno {

    private final int CAPACIDAD;
    private final CyclicBarrier barrier;
    private final Lock lock = new ReentrantLock(true);

    private final List<Visitante> pasajeros = new ArrayList<>();

    public TrenInterno(int capacidad) {
        this.CAPACIDAD = capacidad;

        this.barrier = new CyclicBarrier(capacidad, this::transportar);

        Salida.log("SISTEMA", "Tren interno iniciado con capacidad: " + capacidad);
    }

    public boolean viajar(Visitante v) throws InterruptedException {
        lock.lock();
        try {
            if (pasajeros.size() < CAPACIDAD) {
                pasajeros.add(v);
                Salida.log(v.getIdVisitante(),
                    "sube al tren (" + pasajeros.size() + "/" + CAPACIDAD + ")");
            }
        } finally {
            lock.unlock();
        }

        try {
            barrier.await();
            return true;

        } catch (BrokenBarrierException e) {
            Salida.log(v.getIdVisitante(), "fallo barrera del tren");
            return false;
        }
    }

    private void transportar() {
        Salida.log("TREN", "Inicia recorrido con " + pasajeros.size() + " pasajeros.");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {}

        lock.lock();
        try {
            for (Visitante v : pasajeros) {
                Salida.log(v.getIdVisitante(),
                    "llega al inicio despues de viajar en tren");
            }
            pasajeros.clear();
        } finally {
            lock.unlock();
        }

        Salida.log("TREN", "Tren vuelve para otra tanda.");
    }
}
