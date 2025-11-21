package parque.realidadVirtual;

import util.Salida;

/**
 * Encargado de la atracción de Realidad Virtual.
 * 
 * RESPONSABILIDAD:
 * - Distribuir componentes (visores, manoplas, bases) a los visitantes
 * - Entregar componentes de manera incremental hasta completar equipos
 * 
 * FUNCIONAMIENTO:
 * - Hilo que corre continuamente
 * - Espera visitantes
 * - Distribuye componentes disponibles uno a uno
 * - Notifica cuando el equipo está completo
 */
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