import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import parque.Parque;
import parque.Visitante;

public class Simulacion {
    private static final AtomicLong idGenerator = new AtomicLong(0);

    public List<Thread> crearVisitantes(int cantidad, Parque parque) {
        List<Thread> visitantes = new ArrayList<>();

        for (int i = 0; i < cantidad; i++) {
            Visitante v = new Visitante(parque);
            long uniqueId = idGenerator.incrementAndGet();
            v.setPase(BigInteger.valueOf(uniqueId));
            Thread hilo = new Thread(v, "Visitante-" + uniqueId);
            visitantes.add(hilo);
        }

        return visitantes;
    }
}