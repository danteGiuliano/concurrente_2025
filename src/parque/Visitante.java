package parque;


public class Visitante implements Runnable {
    private final int id;
    private final Parque parque;

    public Visitante(int id, Parque parque) {
        this.id = id;
        this.parque = parque;
    }

    public int getIdVisitante() {
        return id;
    }

    @Override
    public void run() {
        parque.ingresarParque(this);
    }
}
