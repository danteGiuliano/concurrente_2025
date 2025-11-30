package parque;

import java.math.BigInteger;

public class Visitante implements Runnable {
    private final BigInteger ID; 
    private BigInteger ticketNumero; 
    private final Parque parque;
    private Billetera billetera = new Billetera(0);

    public Visitante(BigInteger id, Parque parque) {
        this.ID = id;
        this.parque = parque;
    }

    public BigInteger getIdVisitante() {
        return this.ID;  // ← Siempre retorna el ID original
    }

    public void setTicket(BigInteger ticket) {
        this.ticketNumero = ticket;
    }

    public BigInteger getTicketNumero() {
        return this.ticketNumero;
    }

    public Billetera getBilletera() {
        return billetera;
    }
    
    public void setBilletera(Billetera b) {
        this.billetera = b;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (parque.ingresarParque(this)) {  
                    parque.mapa(this);
                }
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}