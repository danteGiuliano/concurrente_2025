package parque;


import java.util.Random;
import java.util.concurrent.Semaphore;

public class Parque {
    private final Semaphore molinetes; // control de entrada
    private final Random rnd = new Random();

    public Parque(int kMolinetes) {
        this.molinetes = new Semaphore(kMolinetes, true);
    }

    public void ingresarParque(Visitante v) {
        try {
            molinetes.acquire();
            System.out.println("Visitante " + v.getIdVisitante() + " pasó por un molinete.");

            // Una vez dentro, el visitante elige destino
            enviarADestino(v);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            molinetes.release();
        }
    }

    private void enviarADestino(Visitante v) {
        int opcion = rnd.nextInt(4); 
        switch (opcion) {
        }
    }
}
