import java.util.List;

import parque.Parque;
import parque.Reloj;
import util.Salida;

public class Main {
    public static void main(String[] args) {

        Salida.DEBUG = true; //ACTIVAR O DESACTIVAR LA GENERACION DE LOGS 
        int APERTURA = 12000; // MILISEGUNDOS
        int CIERRE = 6000;   // MILISEGUNDOS    
        int VISITANTES = 300;
        int MOLINETES = 3;


        new Reloj(APERTURA , CIERRE ).start(); 
        Parque concurrente_2025 = new Parque(MOLINETES);
        List<Thread> visitantes = new Simulacion().crearVisitantes(VISITANTES,concurrente_2025); 
        
        visitantes.forEach(Thread::start);
    }
}

