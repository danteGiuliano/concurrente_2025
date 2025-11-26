package parque.realidadVirtual;

import util.Salida;


public class Encargado extends Thread {
    private final RealidadVirtual atraccion;

    public Encargado(RealidadVirtual atraccion) {
        super("Encargado-RV");
        this.atraccion = atraccion;
    }

    @Override
    public void run() {
        Salida.log("ENCARGADO", "inicia su turno | REALIDAD VIRTUAL");

        while (true) {
            try {
                atraccion.distribuirComponente();
                Thread.sleep(3500);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }

 
}