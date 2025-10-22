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

    public boolean quitarFichas(int cantidad) {
    if (this.fichas >= cantidad) {
        this.fichas -= cantidad;
        return true;
    }
    return false;
}

    @Override
    public String toString() {
        return "Billetera con " + fichas + " fichas";
    }
}
