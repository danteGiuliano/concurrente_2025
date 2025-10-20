package parque.juegosMecanicos;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Semaphore;

import parque.Billetera;
import parque.Ticketera;
import parque.Visitante;
import util.Salida;


/**
 * Usos de semaforos: 
 * Uso de blocking queue para la cola de espera
 * Uso de Exchanger para el intercambio de billeteras con la ticketera
 * 
 * barrier para controlar el inicio del viaje cuando el carrito esta lleno
 */

public class MontaniaRusa {
    private int CAPACIDAD_COLA = 10;
    private int CAPACIDAD_VIAJE = 5;
    private Ticketera ticketera = new Ticketera( "MR-FICHAS");

    private final BlockingQueue<Visitante> colaEspera = new ArrayBlockingQueue<>(CAPACIDAD_COLA);
    private final Semaphore carrito = new Semaphore(CAPACIDAD_VIAJE, true);

    private final Semaphore barrier = new Semaphore(0, true);
    private final Semaphore mutex = new Semaphore(1, true);

    public MontaniaRusa(int CAPACIDAD_COLA, int CAPACIDAD_VIAJE) {

        this.ticketera.start(); 

        this.CAPACIDAD_VIAJE    = CAPACIDAD_VIAJE;
        this.CAPACIDAD_COLA     = CAPACIDAD_COLA;
    }

    public boolean intentarEntrar(Visitante v) throws InterruptedException {


        if (this.carrito.tryAcquire()) {
            Salida.log(v.getIdVisitante(), "se sube al carrito | MONTANIA RUSA");
            return true;
        } else {
            if (colaEspera.size() < CAPACIDAD_VIAJE) {
                Salida.log(v.getIdVisitante(), "decide ir a la cola de espera | MONTANIA RUSA");
                colaEspera.take();
                this.carrito.acquire(); // SI O SI DEBE TOMAR EL PERMISO DEL CARRITO
                return true;
            } else {
                Salida.log(v.getIdVisitante(), "todo lleno se va del juego | MONTANIA RUSA");
                return false;
            }

        }

    }

    public void iniciarViaje(Visitante v) throws InterruptedException {

        if (carrito.availablePermits() > 0) {
            barrier.acquire(); // ESPERA A QUE SE LLENE EL CARRITO
        } else {
            Salida.log(v.getIdVisitante(), "comienza el viaje | MONTANIA RUSA");
            Thread.sleep(5000);
            Salida.log(v.getIdVisitante(), "termina el viaje | MONTANIA RUSA");
            this.barrier.release(CAPACIDAD_VIAJE - 1); //Bajan de manera ordenada y van por sus tickets
        }

    }

    public void obtenerFichas(Visitante v) throws InterruptedException {

        this.mutex.acquire();
        Salida.log(v.getIdVisitante(), "pasa a obtener sus fichas | MONTANIA RUSA");
        Exchanger<Billetera> lector = ticketera.getExchanger();

        // se envia la billetera para cargar fichas
        lector.exchange(v.getBilletera());
        // recibe la billetera actualizada
        Billetera actualizada = lector.exchange(null);
        v.setBilletera(actualizada);

        Salida.log(v.getIdVisitante(), "obtuvo fichas, vuelve al parque | MONTANIA RUSA");
        this.mutex.release();

    }
}