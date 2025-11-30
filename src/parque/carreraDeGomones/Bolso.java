package parque.carreraDeGomones;


public class Bolso {
    
    public enum Estado {
        DISPONIBLE,
        EN_USO,
        EN_TRANSPORTE,
        EN_DESTINO
    }
    
    private final int numero;
    private Estado estado;
    
    public Bolso(int numero) {
        this.numero = numero;
        this.estado = Estado.DISPONIBLE;
    }
    
    public int getNumero() {
        return numero;
    }
    
    public Estado getEstado() {
        return estado;
    }
    
    public void setEstado(Estado estado) {
        this.estado = estado;
    }
    
    public boolean isDisponible() {
        return estado == Estado.DISPONIBLE;
    }
    
    @Override
    public String toString() {
        return "Bolso N:" + numero;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Bolso bolso = (Bolso) obj;
        return numero == bolso.numero;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(numero);
    }
}