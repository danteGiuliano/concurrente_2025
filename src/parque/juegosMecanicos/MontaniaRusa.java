import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

import parque.Visitante;
import util.Salida;

public class MontaniaRusa {
    private int CAPACIDAD_COLA = 10;
    private int CAPACIDAD_VIAJE = 5;

    private final BlockingQueue<Visitante> colaEspera = new ArrayBlockingQueue<>(CAPACIDAD_COLA);
    private final Semaphore carrito = new Semaphore(CAPACIDAD_VIAJE, true);

    private final Semaphore mutex = new Semaphore(0, true);

    public MontaniaRusa(int CAPACIDAD_COLA, int CAPACIDAD_VIAJE) {
        this.CAPACIDAD_VIAJE = CAPACIDAD_VIAJE;
        this.CAPACIDAD_COLA = CAPACIDAD_COLA;
    }

    public boolean intentarEntrar(Visitante v) {

        boolean entradaMontania = this.carrito.tryAcquire();

        if (entradaMontania) {
            Salida.log(v.getIdVisitante(), "SE SUBE AL CARRITO");
            return entradaMontania;
        } else {
            if (colaEspera.size() < CAPACIDAD_VIAJE) {
                Salida.log(v.getIdVisitante(), "DECIDE IR A LA COLA DE ESPERA");
                colaEspera.take();
                this.carrito.acquire(); // SI O SI DEBE TOMAR EL PERMISO DEL CARRITO
            } else {
                Salida.log(v.getIdVisitante(), "TODO LLENO, SE VA DE LA MONTAÑA RUSA");
                return entradaMontania;
            }

        }

    }

    public void iniciarViaje(Visitante v) throws InterruptedException {

        if (carrito.availablePermits() > 0) {
            mutex.acquire(); // ESPERA A QUE SE LLENE EL CARRITO
        } else {
            Salida.log(v.getIdVisitante(), "COMIENZA EL VIAJE ");
            Thread.sleep(5000);
            Salida.log(v.getIdVisitante(), "TERMINA EL VIAJE EL VIAJE ");
        }


    }
}