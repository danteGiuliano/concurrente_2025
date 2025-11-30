package parque.areaPremios;

import parque.Billetera;
import parque.Visitante;
import parque.areaPremios.AreaPremios.SolicitudPremio;
import util.Salida;

public class Encargado extends Thread {
    private final AreaPremios areaPremios;
    private int visitantesAtendidos = 0;
    
    public Encargado(AreaPremios areaPremios) {
        super("Encargado-Premios");
        this.areaPremios = areaPremios;
    }
    
    @Override
    public void run() {
        Salida.log("ENCARGADO", "inicia su turno en el area de premios | AREA PREMIOS");
        
        while (true) {
            try {
                SolicitudPremio solicitud = areaPremios.obtenerSiguienteSolicitud();
                atenderVisitante(solicitud);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void atenderVisitante(SolicitudPremio solicitud) throws InterruptedException {
        Visitante v = solicitud.visitante;
        Billetera billetera = v.getBilletera();
        int fichasDisponibles = billetera.getFichas();
        
        Salida.log("ENCARGADO", 
            "atendiendo a visitante " + v.getIdVisitante() + 
            " con " + fichasDisponibles + " fichas | AREA PREMIOS");
        
        Premio premioSeleccionado = areaPremios.buscarMejorPremio(fichasDisponibles);
        
        if (premioSeleccionado == null) {
            Salida.log("ENCARGADO", 
                "no hay premios disponibles para visitante " + v.getIdVisitante() + 
                " | AREA PREMIOS");
            
            solicitud.atendido = true;
            solicitud.exitoso = false;
            areaPremios.notificarEntrega(solicitud);
            return;
        }
        
        // CRÍTICO: Asignar el premio ANTES de realizar la transacción
        solicitud.premioObtenido = premioSeleccionado;
        
        if (realizarTransaccion(v, premioSeleccionado)) {
            visitantesAtendidos++;
            solicitud.exitoso = true;
            
            Salida.log("ENCARGADO", 
                "entrego " + premioSeleccionado.getNombre() + 
                " a visitante " + v.getIdVisitante() + 
                " (Total atendidos: " + visitantesAtendidos + ") | AREA PREMIOS");
        } else {
            solicitud.exitoso = false;
            solicitud.premioObtenido = null; // Limpiar si falló
            
            Salida.log("ENCARGADO", 
                "no pudo completar transacción con visitante " + v.getIdVisitante() + 
                " | AREA PREMIOS");
        }
        
        solicitud.atendido = true;
        areaPremios.notificarEntrega(solicitud);
    }
    
    private boolean realizarTransaccion(Visitante v, Premio premio) throws InterruptedException {
        Billetera billetera = v.getBilletera();
        
        if (!billetera.quitarFichas(premio.getCostoFichas())) {
            return false;
        }
        
        premio.decrementarStock();
        Thread.sleep(1000);
        
        return true;
    }
    
    public int getVisitantesAtendidos() {
        return visitantesAtendidos;
    }
}