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
    
    private final BlockingQueue<SolicitudTransporte> colaSolicitudes;
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
                // Esperar solicitud de transporte de bolso
                SolicitudTransporte solicitud = colaSolicitudes.take();
                
                Salida.log("CAMIONETA", 
                    "recoge " + solicitud.bolso + " de visitante " + 
                    solicitud.visitante.getIdVisitante() + " | CARRERA GOMONES");
                
                // Marcar bolso en transporte hacia el final del recorrido
                solicitud.bolso.setEstado(Bolso.Estado.EN_TRANSPORTE);
                
                // Simular tiempo de transporte
                Thread.sleep(2000);
                
                // Entregar bolso en el destino (final del recorrido)
                entregarBolsoEnDestino(solicitud);
                
                // Tiempo de retorno de la camioneta
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
    
    private void entregarBolsoEnDestino(SolicitudTransporte solicitud) {
        lock.lock();
        try {
            solicitud.bolso.setEstado(Bolso.Estado.EN_DESTINO);
            bolsosEnDestino.put(solicitud.bolso, solicitud.visitante);
            viajesRealizados++;
            
            Salida.log("CAMIONETA", 
                "entrega " + solicitud.bolso + " en destino " +
                "(Viaje N:" + viajesRealizados + ") | CARRERA GOMONES");
            
            bolsoListo.signalAll();
            
        } finally {
            lock.unlock();
        }
    }

    public void retirarBolso(Bolso bolso, Visitante v) throws InterruptedException {
        lock.lock();
        try {
            while (!bolsosEnDestino.containsKey(bolso)) {
                Salida.log(v.getIdVisitante(), 
                    "espera que llegue " + bolso + " | CARRERA GOMONES");
                
                bolsoListo.await();
            }
            
            bolsosEnDestino.remove(bolso);
            
            Salida.log(v.getIdVisitante(), 
                "retira " + bolso + " y recupera pertenencias | CARRERA GOMONES");
            
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