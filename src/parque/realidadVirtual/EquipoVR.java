package parque.realidadVirtual;

/**
 * Representa el equipo de Realidad Virtual de un visitante.
 * 
 * COMPONENTES:
 * - 1 visor VR
 * - 2 manoplas
 * - 1 base
 * 
 * PROPÓSITO:
 * - Objeto auxiliar para determinar si el visitante tiene equipo completo
 * - Cada visitante mantiene su propio equipo mientras espera
 */
public class EquipoVR {
    private boolean tieneVisor = false;
    private int manoplas = 0;
    private boolean tieneBase = false;
    
    /**
     * Agrega un visor al equipo
     */
    public void agregarVisor() {
        this.tieneVisor = true;
    }
    
    /**
     * Agrega una manopla al equipo
     */
    public void agregarManopla() {
        if (this.manoplas < 2) {
            this.manoplas++;
        }
    }
    
    /**
     * Agrega una base al equipo
     */
    public void agregarBase() {
        this.tieneBase = true;
    }
    
    /**
     * Verifica si el equipo está completo
     * INVARIANTE: 1 visor + 2 manoplas + 1 base
     */
    public boolean estaCompleto() {
        return tieneVisor && manoplas == 2 && tieneBase;
    }
    
    // Getters
    public boolean tieneVisor() {
        return tieneVisor;
    }
    
    public int getManoplas() {
        return manoplas;
    }
    
    public boolean tieneBase() {
        return tieneBase;
    }
    

}