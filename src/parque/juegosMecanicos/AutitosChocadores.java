package parque.juegosMecanicos;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Semaphore;

import parque.Billetera;
import parque.Ticketera;
import parque.Visitante;
import util.Salida;

public class AutitosChocadores {
    private final int PERSONAS_POR_AUTO = 2; 
    private final int CAPACIDAD_TOTAL; // PERSONAS_POR_AUTO * CANTIDAD_AUTOS
    
    private Ticketera ticketera = new Ticketera("AC-FICHAS");
    private final Semaphore autosDisponibles;
    private final Semaphore mutex = new Semaphore(1, true);
    private final CyclicBarrier barrierInicio;
    
    public AutitosChocadores(int cantidadAutos) {
        this.CAPACIDAD_TOTAL = PERSONAS_POR_AUTO * cantidadAutos;
        this.autosDisponibles = new Semaphore(CAPACIDAD_TOTAL, true);
        this.ticketera.start();
        
        this.barrierInicio = new CyclicBarrier(CAPACIDAD_TOTAL, () -> {
            Salida.log("SISTEMA", "¡Todos los autos ocupados! Inicia la atracción | AUTOS CHOCADORES");
        });
    }
    
    public boolean intentarEntrar(Visitante v) {
        if (autosDisponibles.tryAcquire()) {
            Salida.log(v.getIdVisitante(), "ocupa un lugar en un auto | AUTOS CHOCADORES");
            return true;
        } else {
            Salida.log(v.getIdVisitante(), "no hay lugares disponibles, se retira | AUTOS CHOCADORES");
            return false;
        }
    }
    
    public void esperarInicioYJugar(Visitante v) throws InterruptedException, BrokenBarrierException {
        try {
            Salida.log(v.getIdVisitante(), "espera a que se completen los autos | AUTOS CHOCADORES");
            barrierInicio.await();
            
            Salida.log(v.getIdVisitante(), "chocando autos | AUTOS CHOCADORES");
            Thread.sleep(5000);
            
            Salida.log(v.getIdVisitante(), "termina el juego | AUTOS CHOCADORES");
        } finally {
            autosDisponibles.release();
        }
    }

    public void obtenerFichas(Visitante v) throws InterruptedException {
        mutex.acquire();
        try {
            Salida.log(v.getIdVisitante(), "pasa a obtener sus fichas | AUTOS CHOCADORES");
            Exchanger<Billetera> lector = ticketera.getExchanger();
            
            lector.exchange(v.getBilletera());
            Billetera actualizada = lector.exchange(null);
            v.setBilletera(actualizada);
            
            Salida.log(v.getIdVisitante(), "obtuvo fichas, vuelve al parque | AUTOS CHOCADORES");
        } finally {
            mutex.release();
        }
    }
}