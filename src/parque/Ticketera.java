package parque;

import java.util.concurrent.Exchanger;
import util.Salida;

public class Ticketera extends Thread {

    private  Exchanger<Billetera> swap = new Exchanger<>();
    private final String tipoFicha;
    private boolean activa = true;
    private final boolean suma;   // true = entrega, false = descuenta
    private final int monto;      // monto fijo a aplicar; si <= 0 se usa valorFicha()

    // compat constructor: entrega según tipo (comportamiento original)
    public Ticketera(String tipoFicha) {
        this(tipoFicha, true, -1);
    }

    // nuevo constructor: permite configurar suma/descuenta y monto fijo
    public Ticketera(String tipoFicha, boolean suma, int monto) {
        this.tipoFicha = tipoFicha;
        this.suma = suma;
        this.monto = monto;
    }

    private int valorFicha() {
        return switch (tipoFicha.toUpperCase()) {
            case "MR-FICHAS" -> 3;
            case "AC-FICHAS" -> 2;
            case "AI-FICHAS" -> 1;
            default -> 2;
        };
    }

     public Exchanger<Billetera> getExchanger() {
        return swap;
    }

    @Override
    public void run() {
        while (activa) {
            try {
                Billetera billetera = swap.exchange(null);

                if (suma) {
                    int entregar = (monto > 0) ? monto : valorFicha();
                    billetera.cargarFichas(entregar);
                    Salida.log("TICKETERA", "Entregó " + entregar + " fichas de tipo " + tipoFicha );
                } else {
                    int quitar = (monto > 0) ? monto : valorFicha();
                    boolean ok = billetera.quitarFichas(quitar);
                    Salida.log("TICKETERA", "Intentó descontar " + quitar + " fichas (" + tipoFicha + ") -> " + (ok ? "OK" : "FONDO INSUFICIENTE"));
                }

                swap.exchange(billetera);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                activa = false;
            }
        }
    }

    public void detener() {
        activa = false;
        this.interrupt();
    }
}
