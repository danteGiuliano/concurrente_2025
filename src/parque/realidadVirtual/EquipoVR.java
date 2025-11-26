package parque.realidadVirtual;


public class EquipoVR {
    private boolean tieneVisor = false;
    private int manoplas = 0;
    private boolean tieneBase = false;
    

    public void agregarVisor() {
        this.tieneVisor = true;
    }

    public void agregarManopla() {
        if (this.manoplas < 2) {
            this.manoplas++;
        }
    }
    
    public void agregarBase() {
        this.tieneBase = true;
    }
    
 
    public boolean estaCompleto() {
        return tieneVisor && manoplas == 2 && tieneBase;
    }
  
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