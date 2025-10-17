package parque;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Exchanger;

import parque.juegosMecanicos.MontaniaRusa;
import util.Salida;

public class Parque {
    private final List<Molinete> molinetes = new ArrayList<>();

    private MontaniaRusa montaniaRusa = new MontaniaRusa(5, 5);

    Random rng = new Random();

    public Parque(int kMolinetes) {
        for (int i = 0; i < kMolinetes; i++) {
            molinetes.add(new Molinete(i + 1));
        }
    }

    public void ingresarParque(Visitante v) {
        if (!Reloj.operativo()) {
            Salida.log(v.getIdVisitante(), "no puedo entrar, parque cerrado");
            return;
        }
        molinetes.get(
                this.rng.nextInt(molinetes.size()))
                .intentarIngresar(v);
    }

    // A partir de aca. se sabe. que un visitante. ya posee ticket y puede navegar
    // por el Parque. hasta que tenga una sesion valida.
    public void mapa(Visitante v) throws InterruptedException {

        if (this.montaniaRusa.intentarEntrar(v)) {
            this.montaniaRusa.iniciarViaje(v);
            this.montaniaRusa.obtenerFichas(v);
        }
        ;

    }

}
