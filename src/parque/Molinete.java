package parque;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Semaphore;

import util.Salida;

/**
 * Esta Clase, tiene varios propositos. el Parque debe ser un recurso. enorme
 * que solamente acople los diferentes recursos compartidos.
 * la logica deberia tener una alta cohesion y bajo acoplamiento. por esa razon
 * el parque no deberia tener la logica de los molinetes.
 * 
 * 
 * Para los molinetes se identifican los siguientes hilos :
 * 
 * - Hilo visitante : intenta ingresar al parque, y pasa por el molinete.
 * - Hilo parque : cierra el parque, y por ende los molinetes.
 * 
 * NOTA: los multiples return con un boolean. es para simplificar la logica. ya
 * que puede existir condiciones de carrera.
 * 
 * - El finally, siempre se ejecuta. por ende el semaforo se libera a pesar de
 * un return. en el try. hay que tener cuidado de no usar un return en el
 * finally.
 */

public class Molinete {
    private final int id;
    private BigInteger contador = BigInteger.ZERO;
    private final Semaphore semaforo = new Semaphore(1, true);


    public Molinete(int id) {
        this.id = id;
    }

    public boolean intentarIngresar(Visitante v) {
        if (!Reloj.operativo()) {
            Salida.log(v.getIdVisitante(), "rechazado por MOLINETE, parque cerrado");
            return false;
        }

        try {
            semaforo.acquire();
            if (!Reloj.operativo()) {
                Salida.log(v.getIdVisitante(), "rechazado por MOLINETE, parque cerrado");
                return false;
            }
            contador = contador.add(BigInteger.ONE);
            v.setPase(contador);
            Salida.log(v.getIdVisitante(), "paso por molinete " + id + " ticket N:" + contador );
            return true;
        } catch (InterruptedException e) {
            Salida.log(v.getIdVisitante(), "ERROR EN MOLINETE " + id);
            return false;
        } finally {
            semaforo.release();
        }
    }

}
