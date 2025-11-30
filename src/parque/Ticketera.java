package parque;

import java.util.concurrent.Exchanger;
import util.Salida;

public class Ticketera extends Thread {

    private Exchanger<Billetera> swap = new Exchanger<>();
    private final String tipoFicha;
    private boolean activa = true;
    private final boolean suma; // true = entrega, false = descuenta
    private final int monto; // monto fijo a aplicar; si <= 0 se usa valorFicha()

    private int USO = 0;

    public Ticketera(String tipoFicha) {

        this(tipoFicha, true, -1);
    }

    public Ticketera(String tipoFicha, boolean suma, int monto) {
        super("TICKETERA");
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
                this.USO++;
                if (suma) {
                    int entregar = (monto > 0) ? monto : valorFicha();
                    billetera.cargarFichas(entregar);
                    Salida.log("TICKETERA",
                            "Entrego " + entregar + " fichas de tipo " + tipoFicha + " USO:" + this.USO);
                } else {
                    int quitar = (monto > 0) ? monto : valorFicha();
                    boolean ok = billetera.quitarFichas(quitar);
                    Salida.log("TICKETERA", "Intento descontar " + quitar + " fichas (" + tipoFicha + ") -> "
                            + (ok ? "OK" : "FONDO INSUFICIENTE") + " USO:" + this.USO);
                }

                swap.exchange(billetera);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Salida.log("TICKETERA", "ERROR DE TICKETERA");

                activa = false;
            }
        }
    }
}
