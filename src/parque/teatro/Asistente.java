package parque.teatro;

import util.Salida;

/**
 * Asistente del teatro que forma grupos de visitantes.
 * Es un hilo que trabaja continuamente formando grupos de 5 personas.
 * Usa locks y monitores para coordinarse con los visitantes y otros asistentes.
 */
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
                // Intenta formar un grupo
                if (teatro.formarGrupo(id)) {
                    // Pequeña pausa después de formar un grupo
                    Thread.sleep(500);
                } else {
                    // Si no pudo formar grupo, espera 
                    Thread.sleep(1000);
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                activo = false;
            }
        }
        
        Salida.log("ASISTENTE-" + id, "termina su turno | TEATRO");
    }
    
    public void detener() {
        activo = false;
        this.interrupt();
    }
}