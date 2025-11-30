package parque.areaPremios;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import parque.Visitante;
import util.Salida;

public class AreaPremios {
    private final List<Premio> catalogoPremios;
    private final Queue<SolicitudPremio> colaSolicitudes;
    
    private final Lock lock;
    private final Condition hayVisitantes;
    
    private final Encargado encargado;
    
    public AreaPremios() {
        this.catalogoPremios = inicializarCatalogo();
        this.colaSolicitudes = new LinkedList<>();
        
        this.lock = new ReentrantLock(true);
        this.hayVisitantes = lock.newCondition();
        
        this.encargado = new Encargado(this);
        this.encargado.start();
        
        Salida.log("SISTEMA", "area de Premios abierta | AREA PREMIOS");
    }
    
    private List<Premio> inicializarCatalogo() {
        List<Premio> catalogo = new ArrayList<>();
        
        catalogo.add(new Premio("Llavero", 1, 50));
        catalogo.add(new Premio("Sticker", 1, 100));
        catalogo.add(new Premio("Gorra", 3, 30));
        catalogo.add(new Premio("Remera", 5, 25));
        catalogo.add(new Premio("Peluche pequenio", 8, 20));
        catalogo.add(new Premio("Taza", 10, 15));
        catalogo.add(new Premio("Mochila", 12, 10));
        catalogo.add(new Premio("Peluche grande", 15, 8));
        catalogo.add(new Premio("Auriculares", 20, 5));
        catalogo.add(new Premio("Reloj", 25, 3));
        catalogo.add(new Premio("Consola portatil", 30, 2));
        
        return catalogo;
    }
    
    public boolean solicitarPremio(Visitante v) throws InterruptedException {
        lock.lock();
        try {
            int fichasDisponibles = v.getBilletera().getFichas();
            
            Salida.log(v.getIdVisitante(), 
                "llega al area de premios con " + fichasDisponibles + " fichas | AREA PREMIOS");
            
            if (fichasDisponibles == 0) {
                Salida.log(v.getIdVisitante(), 
                    "no tiene fichas para canjear, se retira | AREA PREMIOS");
                return false;
            }
            
            // Crear condition ÚNICA para este visitante
            Condition miCondition = lock.newCondition();
            SolicitudPremio solicitud = new SolicitudPremio(v, miCondition);
            colaSolicitudes.add(solicitud);
            
            Salida.log(v.getIdVisitante(), 
                "entra en cola de premios (posicion: " + colaSolicitudes.size() + ") | AREA PREMIOS");
            
            // Notificar al encargado
            hayVisitantes.signal();
            
            Salida.log(v.getIdVisitante(), 
                "espera ser atendido por el encargado | AREA PREMIOS");
            
            // Esperar en MI PROPIA condition variable
            while (!solicitud.atendido) {
                miCondition.await();
            }
            
            if (solicitud.exitoso && solicitud.premioObtenido != null) {
                Salida.log(v.getIdVisitante(), 
                    "obtuvo " + solicitud.premioObtenido.getNombre() + 
                    " por " + solicitud.premioObtenido.getCostoFichas() + " fichas | AREA PREMIOS");
            } else {
                Salida.log(v.getIdVisitante(), 
                    "no pudo obtener premio (sin stock o fichas insuficientes) | AREA PREMIOS");
            }
            
            return solicitud.exitoso;
            
        } finally {
            lock.unlock();
        }
    }
    
    SolicitudPremio obtenerSiguienteSolicitud() throws InterruptedException {
        lock.lock();
        try {
            while (colaSolicitudes.isEmpty()) {
                hayVisitantes.await();
            }
            
            SolicitudPremio solicitud = colaSolicitudes.poll();
            
            Salida.log("SISTEMA", 
                "encargado toma solicitud de visitante " + solicitud.visitante.getIdVisitante() + 
                " | AREA PREMIOS");
            
            return solicitud;
            
        } finally {
            lock.unlock();
        }
    }
    
    Premio buscarMejorPremio(int fichasDisponibles) {
        lock.lock();
        try {
            Premio mejorPremio = null;
            
            for (Premio premio : catalogoPremios) {
                if (premio.hayStock() && premio.getCostoFichas() <= fichasDisponibles) {
                    if (mejorPremio == null || 
                        premio.getCostoFichas() > mejorPremio.getCostoFichas()) {
                        mejorPremio = premio;
                    }
                }
            }
            
            return mejorPremio;
            
        } finally {
            lock.unlock();
        }
    }
    
    void notificarEntrega(SolicitudPremio solicitud) {
        lock.lock();
        try {
            solicitud.miCondition.signal();
        } finally {
            lock.unlock();
        }
    }
    
    public List<Premio> getCatalogoPremios() {
        return new ArrayList<>(catalogoPremios);
    }
    
    static class SolicitudPremio {
        final Visitante visitante;
        final Condition miCondition;  
        boolean atendido = false;
        boolean exitoso = false;
        Premio premioObtenido = null;
        
        SolicitudPremio(Visitante visitante, Condition condition) {
            this.visitante = visitante;
            this.miCondition = condition;
        }
    }
}