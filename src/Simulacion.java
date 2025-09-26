import java.util.ArrayList;
import java.util.List;

import parque.Parque;
import parque.Visitante;

public class Simulacion {
    public List<Thread> crearVisitantes(int cantidad, Parque parque) {
        List<Thread> visitantes = new ArrayList<>();


        for (int i = 0; i < cantidad; i++) {
            Visitante v = new Visitante(parque);
            Thread hilo = new Thread(v, "Visitante-" + i);
            visitantes.add(hilo);
        }

        return visitantes;
    }
}