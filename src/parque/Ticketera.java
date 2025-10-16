package parque;

import java.util.concurrent.Exchanger;

public class Ticketera implements Runnable {

    private final Exchanger<Billetera> exchanger;
    private final String tipoFicha;

    public Ticketera(Exchanger<Billetera> exchanger, String tipoFicha) {
        this.exchanger = exchanger;
        this.tipoFicha = tipoFicha;
    }

    private int valorFicha() {
        return switch (tipoFicha.toLowerCase()) {
            case "MR-FICHAS" -> 3;
            case "AC-FICHAS" -> 2;
            case "AI-FICHAS" -> 1;
            default -> 2;
        };
    }

    @Override
    public void run() {
        try {
            while (true) {
                Billetera billetera = exchanger.exchange(null);
                if (billetera == null)
                    continue;

                int valor = valorFicha();
                billetera.cargarFichas(valor);
                System.out.println("Ticketera (" + tipoFicha + "): cargó " + valor + " fichas -> " + billetera);

                exchanger.exchange(billetera);
            }
        } catch (InterruptedException e) {
            System.out.println("Ticketera interrumpida");
            Thread.currentThread().interrupt();
        }
    }
}
