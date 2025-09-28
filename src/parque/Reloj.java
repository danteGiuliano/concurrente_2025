package parque;

public class Reloj extends Thread {
    private final int apertura; // Duracion de Apertura en segundos
    private final int cierre; // Duracion de Cierre en segundos
    private static boolean abierto = false;

    public Reloj( int apertura, int cierre) {
        this.apertura = apertura;
        this.cierre = cierre;
    }


    @Override
    public void run() {
        while (true) {
            esperar(apertura);
            esperar(cierre);
        }
    }

    private void esperar(int segundos) {
        try {
            Thread.sleep(segundos * 1000L);
            abierto = !abierto;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static boolean estaAbierto() {
        return abierto;
    }
}
