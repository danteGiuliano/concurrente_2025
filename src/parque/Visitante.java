package parque;

import java.math.BigInteger;

public class Visitante implements Runnable {
    private BigInteger ID ;
    private final Parque parque;

    public Visitante(Parque parque) {
        this.parque = parque;
    }

    public BigInteger getIdVisitante() {
        return this.ID;
    }

    public void setPase(BigInteger pase ) {
        this.ID = pase;
    }

    @Override
    public void run() {
        parque.ingresarParque(this);
    }
}
