package parque.carreraDeGomones;


public class Gomon {
    
    public enum Tipo {
        INDIVIDUAL(1),
        DOBLE(2);
        
        private final int capacidad;
        
        Tipo(int capacidad) {
            this.capacidad = capacidad;
        }
        
        public int getCapacidad() {
            return capacidad;
        }
    }
    
    private final int id;
    private final Tipo tipo;
    private boolean enUso;
    
    public Gomon(int id, Tipo tipo) {
        this.id = id;
        this.tipo = tipo;
        this.enUso = false;
    }
    
    public int getId() {
        return id;
    }
    
    public Tipo getTipo() {
        return tipo;
    }
    
    public boolean isEnUso() {
        return enUso;
    }
    
    public void marcarEnUso() {
        this.enUso = true;
    }
    
    public void marcarDisponible() {
        this.enUso = false;
    }
    
    @Override
    public String toString() {
        return "Gomón #" + id + " (" + tipo + ")";
    }
}