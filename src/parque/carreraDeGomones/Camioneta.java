package parque.carreraDeGomones;

import parque.Visitante;
import util.Salida;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Camioneta extends Thread {
    
    // Solicitudes de transporte pendientes
    private final BlockingQueue<SolicitudTransporte> colaSolicitudes;
    
    // Bolsos que ya llegaron al destino
    private final Map<Bolso, Visitante> bolsosEnDestino;
   
    private final Lock lock;
    private final Condition bolsoListo;
    
    private boolean operativa;
    private int viajesRealizados;
    
    public Camioneta() {
        super("Camioneta");
        this.colaSolicitudes = new LinkedBlockingQueue<>();
        this.bolsosEnDestino = new HashMap<>();
        this.lock = new ReentrantLock(true);
        this.bolsoListo = lock.newCondition();
        this.operativa = true;
        this.viajesRealizados = 0;
    }
    
    @Override
    public void run() {
        Salida.log("CAMIONETA", "inicia operaciones | CARRERA GOMONES");
        
        while (operativa) {
            try {
                // Esperar solicitud de transporte
                SolicitudTransporte solicitud = colaSolicitudes.take();
                
                Salida.log("CAMIONETA", 
                    "recoge " + solicitud.bolso + " de visitante " + 
                    solicitud.visitante.getIdVisitante() + " | CARRERA GOMONES");
                
                // Marcar bolso en transporte
                solicitud.bolso.setEstado(Bolso.Estado.EN_TRANSPORTE);
                
                Thread.sleep(2000);
                
                // Entregar bolso al destino
                entregarBolsoEnDestino(solicitud);
                
                Thread.sleep(1000);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                operativa = false;
            }
        }
        
        Salida.log("CAMIONETA", "finaliza operaciones | CARRERA GOMONES");
    }
    
  
    public void transportarBolso(Bolso bolso, Visitante v) throws InterruptedException {
        SolicitudTransporte solicitud = new SolicitudTransporte(bolso, v);
        colaSolicitudes.put(solicitud);
        
        Salida.log(v.getIdVisitante(), 
            "entrega " + bolso + " a la camioneta | CARRERA GOMONES");
    }
    
    /**
     * Camioeta entrega el bolso enn el destino
     */
    private void entregarBolsoEnDestino(SolicitudTransporte solicitud) {
        lock.lock();
        try {
            solicitud.bolso.setEstado(Bolso.Estado.EN_DESTINO);
            bolsosEnDestino.put(solicitud.bolso, solicitud.visitante);
            viajesRealizados++;
            
            Salida.log("CAMIONETA", 
                "entrega " + solicitud.bolso + " en destino " +
                "(Viaje #" + viajesRealizados + ") | CARRERA GOMONES");
            
            // Notificar que el bolso está disponible
            bolsoListo.signalAll();
            
        } finally {
            lock.unlock();
        }
    }

    public void retirarBolso(Bolso bolso, Visitante v) throws InterruptedException {
        lock.lock();
        try {
            // Esperar hasta que el bolso esté en destino
            while (!bolsosEnDestino.containsKey(bolso)) {
                Salida.log(v.getIdVisitante(), 
                    "espera que llegue " + bolso + " | CARRERA GOMONES");
                
                bolsoListo.await();
            }
            
            // Retirar el bolso
            bolsosEnDestino.remove(bolso);
            
            Salida.log(v.getIdVisitante(), 
                "retira " + bolso + " y recupera pertenencias | CARRERA GOMONES");
            
            // Simular sacar pertenencias
            Thread.sleep(500);
            
        } finally {
            lock.unlock();
        }
    }
    
 
    
  
    private static class SolicitudTransporte {
         Bolso bolso;
         Visitante visitante;
        
        SolicitudTransporte(Bolso bolso, Visitante visitante) {
            this.bolso = bolso;
            this.visitante = visitante;
        }
    }
}