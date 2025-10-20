package parque;

import java.math.BigInteger;

public class Visitante implements Runnable {
    private BigInteger ID;
    private final Parque parque;
    private Billetera billetera = new Billetera(0);

    public Visitante(Parque parque) {
        this.parque = parque;
    }

    public BigInteger getIdVisitante() {
        return this.ID;
    }

    public void setPase(BigInteger pase) {
        this.ID = pase;
    }

    public Billetera getBilletera() {
        return billetera;
    }
    public void setBilletera(Billetera b){
        this.billetera = b;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if ( parque.ingresarParque(this)) {  
                    parque.mapa(this);
                }
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
