import java.util.List;

import parque.Parque;
import util.Salida;

public class Main {
    public static void main(String[] args) {
        Salida.DEBUG = true; //ACTIVAR O DESACTIVAR LA GENERACION DE LOGS DEBBUGEAR POR

        Parque concurrente_2025 = new Parque(3);
        List<Thread> visitantes = new Simulacion().crearVisitantes(300,concurrente_2025); 
        
        visitantes.forEach(Thread::start);
    }
}

