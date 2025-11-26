package parque.areaPremios;

import java.util.List;
import java.util.concurrent.Semaphore;
import parque.Billetera;
import parque.Visitante;
import util.Salida;


public class Encargado {
    private final List<Premio> catalogoPremios;
    private final Semaphore mutex = new Semaphore(1, true);
    private int visitantesAtendidos = 0;

    public Encargado(List<Premio> catalogoPremios) {
        this.catalogoPremios = catalogoPremios;
    }

    /**
     * Atiende a un visitante y le entrega un premio según sus fichas disponibles.
     * @param v Visitante que solicita canjear fichas
     * @return true si se pudo entregar algún premio, false si no tiene suficientes fichas
     */
    public boolean atenderVisitante(Visitante v) throws InterruptedException {
        mutex.acquire();
        try {
            Billetera billetera = v.getBilletera();
            int fichasDisponibles = billetera.getFichas();

            Salida.log(v.getIdVisitante(), 
                "llega al área de premios con " + fichasDisponibles + " fichas | AREA PREMIOS");

            if (fichasDisponibles == 0) {
                Salida.log(v.getIdVisitante(), 
                    "no tiene fichas para canjear | AREA PREMIOS");
                return false;
            }

            // Buscar el premio más costoso que el visitante pueda comprar
            Premio premioSeleccionado = null;
            for (Premio premio : catalogoPremios) {
                if (premio.hayStock() && 
                    premio.getCostoFichas() <= fichasDisponibles) {
                    if (premioSeleccionado == null || 
                        premio.getCostoFichas() > premioSeleccionado.getCostoFichas()) {
                        premioSeleccionado = premio;
                    }
                }
            }

            if (premioSeleccionado == null) {
                Salida.log(v.getIdVisitante(), 
                    "no hay premios disponibles para su cantidad de fichas | AREA PREMIOS");
                return false;
            }

            // Realizar el canje
            if (billetera.quitarFichas(premioSeleccionado.getCostoFichas())) {
                premioSeleccionado.decrementarStock();
                visitantesAtendidos++;
                
                Salida.log(v.getIdVisitante(), 
                    "canjeó " + premioSeleccionado.getCostoFichas() + 
                    " fichas por: " + premioSeleccionado.getNombre() + 
                    " | AREA PREMIOS");
                
                Salida.log("ENCARGADO", 
                    "entregó " + premioSeleccionado.getNombre() + 
                    " a visitante " + v.getIdVisitante() + 
                    " (Total atendidos: " + visitantesAtendidos + ")");
                
                return true;
            }

            return false;

        } finally {
            mutex.release();
        }
    }

 
}