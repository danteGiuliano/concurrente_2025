package parque;

public class Billetera {
    private int fichas;

    public Billetera(int fichasIniciales) {
        this.fichas = fichasIniciales;
    }

    public void cargarFichas(int cantidad) {
        fichas += cantidad;
    }

    public int getFichas() {
        return fichas;
    }

    @Override
    public String toString() {
        return "Billetera con " + fichas + " fichas";
    }
}
