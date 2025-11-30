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
    private final Condition premioEntregado;
    
    private final Encargado encargado;
    
    public AreaPremios() {
        this.catalogoPremios = inicializarCatalogo();
        this.colaSolicitudes = new LinkedList<>();
        
        this.lock = new ReentrantLock(true);
        this.hayVisitantes = lock.newCondition();
        this.premioEntregado = lock.newCondition();
        
        this.encargado = new Encargado(this);
        this.encargado.start();
        
        Salida.log("SISTEMA", "Área de Premios abierta | AREA PREMIOS");
    }
    
    private List<Premio> inicializarCatalogo() {
        List<Premio> catalogo = new ArrayList<>();
        
        catalogo.add(new Premio("Llavero", 1, 50));
        catalogo.add(new Premio("Sticker", 1, 100));
        catalogo.add(new Premio("Gorra", 3, 30));
        catalogo.add(new Premio("Remera", 5, 25));
        catalogo.add(new Premio("Peluche pequeño", 8, 20));
        catalogo.add(new Premio("Taza", 10, 15));
        catalogo.add(new Premio("Mochila", 12, 10));
        catalogo.add(new Premio("Peluche grande", 15, 8));
        catalogo.add(new Premio("Auriculares", 20, 5));
        catalogo.add(new Premio("Reloj", 25, 3));
        catalogo.add(new Premio("Consola portátil", 30, 2));
        
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
                    "no tiene fichas para canjear | AREA PREMIOS");
                return false;
            }
            
            SolicitudPremio solicitud = new SolicitudPremio(v);
            colaSolicitudes.add(solicitud);
            
            Salida.log(v.getIdVisitante(), 
                "espera ser atendido por el encargado | AREA PREMIOS");
            
            hayVisitantes.signal();
            
            while (!solicitud.atendido) {
                premioEntregado.await();
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
            
            return colaSolicitudes.poll();
            
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
    
    void notificarEntrega() {
        lock.lock();
        try {
            premioEntregado.signalAll();
        } finally {
            lock.unlock();
        }
    }
    
    public List<Premio> getCatalogoPremios() {
        return new ArrayList<>(catalogoPremios);
    }
    
    static class SolicitudPremio {
        final Visitante visitante;
        boolean atendido = false;
        boolean exitoso = false;
        
        SolicitudPremio(Visitante visitante) {
            this.visitante = visitante;
        }
    }
}