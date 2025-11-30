package parque.carreraDeGomones;


import parque.Visitante;
import parque.Billetera;
import parque.Ticketera;
import util.Salida;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Semaphore;

/**
 * Sistema de Premios - Entrega fichas a ganadores
 * 
 * RESPONSABILIDAD:
 * - Gestionar entrega de fichas CG al ganador
 * - Coordinar con Ticketera
 * - Trackear ganadores
 */
public class SistemaPremios {
    
    private final Ticketera ticketera;
    private final Semaphore mutex;
    private int ganadoresAtendidos;
    
    public SistemaPremios() {
        this.ticketera = new Ticketera("GOMONES-FICHAS");
        this.mutex = new Semaphore(1, true);
        this.ganadoresAtendidos = 0;
        this.ticketera.start();
    }
    


    
    /**
     * Entrega premio al ganador
     * En gomón doble, ambos reciben premio
     */
    public void entregarPremio(Visitante v, Gomon gomon) throws InterruptedException {
        mutex.acquire();
        try {
            Salida.log(v.getIdVisitante(), 
                "¡GANADOR! Recibe premio por " + gomon.getTipo() + " | CARRERA GOMONES");
            
            // Intercambio con ticketera
            Exchanger<Billetera> exchanger = ticketera.getExchanger();
            
            // Enviar billetera para cargar fichas
            exchanger.exchange(v.getBilletera());
            
            // Recibir billetera actualizada
            Billetera billetera = exchanger.exchange(null);
            v.setBilletera(billetera);
            
            ganadoresAtendidos++;
            
            Salida.log("SISTEMA", 
                "Premio entregado a visitante " + v.getIdVisitante() + 
                " (Total: " + ganadoresAtendidos + ") | CARRERA GOMONES");
            
        } finally {
            mutex.release();
        }
    }
    
    /**
     * Detiene el sistema de premios
     */
    public void detener() {
        ticketera.detener();
    }
    
    public int getGanadoresAtendidos() {
        return ganadoresAtendidos;
    }
}