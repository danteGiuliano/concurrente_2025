package parque.teatro;

import util.Salida;

public class Asistente extends Thread {

    private final int id;
    private final Teatro teatro;
    private boolean activo = true;

    public Asistente(int id, Teatro teatro) {
        super("Asistente-" + id);
        this.id = id;
        this.teatro = teatro;
    }

    @Override
    public void run() {
        Salida.log("ASISTENTE-" + id, "inicia su turno | TEATRO");

        while (activo) {
            try {
                teatro.formarGrupo(id);
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                activo = false;
            }
        }

        Salida.log("ASISTENTE-" + id, "termina su turno | TEATRO");
    }

 
}
