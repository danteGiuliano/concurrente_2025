package parque;


import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Parque {
    private final Semaphore molinetes;
    private final Random rnd = new Random();
    private BigInteger ticket= BigInteger.ZERO;

    public Parque(int kMolinetes) {
        this.molinetes = new Semaphore(kMolinetes, true);
    }

    public void ingresarParque(Visitante v) {
        try {
            molinetes.acquire();

            synchronized (this.ticket) {
                this.ticket = this.ticket.add(BigInteger.ONE);
                v.setPase(this.ticket);
            }

            molinetes.release();
            System.out.println("Visitante " + v.getIdVisitante() + " pasó por un molinete.");
            enviarADestino(v);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } 
    }

    private void enviarADestino(Visitante v) {
        int opcion = rnd.nextInt(4); 
        switch (opcion) {

        }
    }
}
