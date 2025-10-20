package parque;

import java.math.BigInteger;
import java.util.concurrent.Exchanger;
import util.Salida;

public class Ticketera extends Thread {

    private  Exchanger<Billetera> swap = new Exchanger<>();
    private final String tipoFicha;
    private boolean activa = true;
    private BigInteger idTicketera = BigInteger.valueOf(-1);

    public Ticketera( String tipoFicha) {
        this.tipoFicha = tipoFicha;
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
                // Espera que un visitante intercambie su billetera
                Billetera billetera = swap.exchange(null);

                billetera.cargarFichas( valorFicha());
                Salida.log("TICKETERA", "Entregó " + valorFicha() + " fichas de tipo " + tipoFicha );

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
