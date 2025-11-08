package parque.areaPremios;

public class Premio {
    private final String nombre;
    private final int costoFichas;
    private int stock;

    public Premio(String nombre, int costoFichas, int stock) {
        this.nombre = nombre;
        this.costoFichas = costoFichas;
        this.stock = stock;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCostoFichas() {
        return costoFichas;
    }

    public int getStock() {
        return stock;
    }

    public boolean hayStock() {
        return stock > 0;
    }

    public void decrementarStock() {
        if (stock > 0) {
            stock--;
        }
    }

    @Override
    public String toString() {
        return nombre + " (" + costoFichas + " fichas) - Stock: " + stock;
    }
}