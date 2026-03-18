package parque.carreraDeGomones;

import parque.Visitante;
import util.Salida;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.List;

public class TrenInterno {

    private final int CAPACIDAD;
    private final CyclicBarrier barrier;

    private final Semaphore mutex = new Semaphore(1, true); // único semáforo
    private boolean subidaHabilitada = true;                // controla si se puede subir

    private final List<Visitante> pasajeros = new ArrayList<>();

    public TrenInterno(int capacidad) {
        this.CAPACIDAD = capacidad;
        this.barrier = new CyclicBarrier(capacidad, this::transportar);
        Salida.log("SISTEMA", "Tren interno iniciado con capacidad: " + capacidad + "| TREN INTERNO");
    }

    public boolean viajar(Visitante v) throws InterruptedException {
        boolean added = false;

        while (!added) {
            // adquiere mutex
            mutex.acquire();
            try {
                // si no se permite subir o ya está lleno, no hago nada aquí
                if (subidaHabilitada && pasajeros.size() < CAPACIDAD) {
                    // operación atómica: añadir y loguear dentro del mutex
                    pasajeros.add(v);
                    Salida.log(v.getIdVisitante(),
                            "sube al tren (" + pasajeros.size() + "/" + CAPACIDAD + ") | TREN INTERNO");

                    // Si completamos la tanda, deshabilito subida para que nadie más entre
                    if (pasajeros.size() == CAPACIDAD) {
                        subidaHabilitada = false;
                    }
                    added = true; // salgo del bucle
                }
                // else: no puede subir ahora
            } finally {
                // siempre libero exactamente una vez
                mutex.release();
            }

            if (!added) {
                // esperamos fuera del mutex para no bloquear a transportar()
            }
        }

        try {
            barrier.await(); // el último que entra dispara transportar()
            return true;
        } catch (BrokenBarrierException e) {
            Salida.log(v.getIdVisitante(), "barrera rota | TREN INTERNO");
            return false;
        }
    }

    private void transportar() {
        Salida.log("TREN", "Inicia recorrido con " + pasajeros.size() + " pasajeros. | TREN INTERNO");

        try { Thread.sleep(2000); }
        catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }

        // bajar pasajeros y resetar todo de forma atómica
        mutex.acquireUninterruptibly();
        try {
            for (Visitante v : pasajeros) {
                Salida.log(v.getIdVisitante(),
                        "llega al inicio despues de viajar en tren | TREN INTERNO");
            }
            pasajeros.clear();

            // habilita nueva tanda
            subidaHabilitada = true;

            Salida.log("TREN", "Tren vuelve para otra tanda. | TREN INTERNO");
        } finally {
            mutex.release();
        }
    }
}
