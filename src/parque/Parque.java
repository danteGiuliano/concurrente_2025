package parque;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

import parque.juegosMecanicos.MontaniaRusa;
import util.Salida;

public class Parque {
    private final List<Molinete> molinetes = new ArrayList<>();
    private final Semaphore entradaParque = new Semaphore(1, true); // Add this line

    private MontaniaRusa montaniaRusa = new MontaniaRusa(5, 5);

    Random rng = new Random();

    public Parque(int kMolinetes) {
        for (int i = 0; i < kMolinetes; i++) {
            molinetes.add(new Molinete(i));
        }
    }

    public boolean ingresarParque(Visitante v) {
        if (!Reloj.operativo()) {
            Salida.log(v.getIdVisitante(), "no puedo entrar, parque cerrado");
            return false;
        }

        try {
            entradaParque.acquire(); 
            return molinetes.get(this.rng.nextInt(molinetes.size())).intentarIngresar(v);
        } catch (InterruptedException e) {
            Salida.log(v.getIdVisitante(), "ERROR en entrada del parque EXCEPCION");
            return false;
        } finally {
            entradaParque.release();
        }
    }

    // A partir de aca. se sabe. que un visitante. ya posee ticket y puede navegar
    // por el Parque. hasta que tenga una sesion valida.
    public void mapa(Visitante v) throws InterruptedException {

        if(!Reloj.operativo()){
            return;
        }

        if (this.montaniaRusa.intentarEntrar(v)) {
            this.montaniaRusa.iniciarViaje(v);
            this.montaniaRusa.obtenerFichas(v);
        }

        this.mapa(v);

    }

}
