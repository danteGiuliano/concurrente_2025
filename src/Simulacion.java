import java.util.ArrayList;
import java.util.List;

public class Simulacion {
        public List<Thread> crearHilos(int cantidad, String parametro) {
        List<Thread> hilos = new ArrayList<>();

        for (int i = 0; i < cantidad; i++) {
            final int id = i;
            Runnable tarea = () -> {
                System.out.println("Hilo " + id + " ejecutando con parámetro: " + parametro);
                try {
                    Thread.sleep(1000); // simula trabajo
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };

            Thread hilo = new Thread(tarea, "Hilo-" + id);
            hilos.add(hilo);
        }

        return hilos;
    }
}
