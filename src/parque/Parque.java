package parque;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

import parque.areaPremios.AreaPremios;
import parque.carreraDeGomones.CarreraGomones;
import parque.comedor.Comedor;
import parque.juegosMecanicos.AutitosChocadores;
import parque.juegosMecanicos.MontaniaRusa;
import parque.teatro.Teatro;
import parque.realidadVirtual.RealidadVirtual;
import util.Salida;

public class Parque {
    private final List<Molinete> molinetes = new ArrayList<>();
    private final Semaphore entradaParque = new Semaphore(1, true);

    private MontaniaRusa montaniaRusa = new MontaniaRusa(5, 5);
    private AutitosChocadores autitosChocadores = new AutitosChocadores(10);
    private AreaPremios areaPremios = new AreaPremios();
    private Teatro teatro = new Teatro(2);
    private Comedor comedor = new Comedor(5);
    private RealidadVirtual realidadVirtual = new RealidadVirtual(4, 8, 4);
    private CarreraGomones carreraGomones = new CarreraGomones(
            10, // 10 bicicletas en el stand
            8, // 8 gomones individuales
            5, // 5 gomones dobles
            20, // 20 bolsos con llave disponibles
            5 // Se necesitan 5 gomones para iniciar carrera
    );

    Random rng = new Random();

    public Parque(int kMolinetes) {
        for (int i = 0; i < kMolinetes; i++) {
            molinetes.add(new Molinete(i));
        }
    }

    public boolean ingresarParque(Visitante v) {
        if (!Reloj.operativo()) {
            Salida.log(v.getIdVisitante(), "no puedo entrar, parque cerrado | PARQUE ");
            return false;
        }

        try {
            entradaParque.acquire();
            return molinetes.get(this.rng.nextInt(molinetes.size())).intentarIngresar(v);
        } catch (InterruptedException e) {
            Salida.log(v.getIdVisitante(), "ERROR en entrada del parque EXCEPCION | PARQUE ");
            return false;
        } finally {
            entradaParque.release();
        }
    }

    // A partir de aca. se sabe. que un visitante. ya posee ticket y puede navegar
    // por el Parque. hasta que tenga una sesion valida.
    public void mapa(Visitante v) {

        while (Reloj.operativo()) {
            // this.montaniaRusa(v);
            // this.autitosChocadores(v);

            // El visitante decide si va al comedor (25% probabilidad)
            if (rng.nextInt(100) < 25) {
                this.comedor(v);
            }

            // El visitante decide si va al teatro (20% probabilidad)
            // if (rng.nextInt(100) < 20) {
            //     this.teatro(v);
            // }

            // El visitante decide si va a Realidad Virtual (20% probabilidad)
            // if (rng.nextInt(100) < 20) {
            //     this.realidadVirtual(v);
            // }

            // El visitante decide si va al área de premios con probabilidad
            // if (rng.nextInt(100) < 30 && v.getBilletera().getFichas() > 0) {
            //     this.areaPremios(v);
            // }

            // if (rng.nextInt(100) < 25) {
            //     this.carreraGomones(v);
            // }
        }

        // Antes de irse, intenta canjear fichas restantes
        // if (v.getBilletera().getFichas() > 0) {
        //     this.areaPremios(v);
        // }

        Salida.log(v.getIdVisitante(), "parque cerrado se va a casa | PARQUE ");
    }

    public boolean parqueAbierto() {
        return Reloj.operativo();
    }

    public void montaniaRusa(Visitante v) {
        try {
            if (this.montaniaRusa.intentarEntrar(v)) {
                this.montaniaRusa.iniciarViaje(v);
                this.montaniaRusa.obtenerFichas(v);
            }
        } catch (Exception e) {
            Salida.log(v.getIdVisitante(), "interrumpido en el parque EXCEPCION | MONTANIA RUSA ");
        }
    }

    public void autitosChocadores(Visitante v) {
        try {
            if (this.autitosChocadores.intentarEntrar(v)) {
                this.autitosChocadores.esperarInicioYJugar(v);
                this.autitosChocadores.obtenerFichas(v);
            }
        } catch (Exception e) {
            Salida.log(v.getIdVisitante(), "interrumpido en el parque EXCEPCION | AUTITOS CHOCADORES ");
        }
    }

    public void areaPremios(Visitante v) {
        try {
            this.areaPremios.solicitarPremio(v);
        } catch (Exception e) {
            Salida.log(v.getIdVisitante(), "interrumpido en el parque EXCEPCION | AREA PREMIOS ");
        }
    }

    public void teatro(Visitante v) {
        try {
            this.teatro.intentarEntrar(v);
        } catch (Exception e) {
            Salida.log(v.getIdVisitante(), "interrumpido en el parque EXCEPCION | TEATRO");
        }
    }

    public void comedor(Visitante v) {
        try {
            this.comedor.almorzar(v);
        } catch (Exception e) {
            Salida.log(v.getIdVisitante(), "interrumpido en el parque EXCEPCION | COMEDOR");
        }
    }

    public void realidadVirtual(Visitante v) {
        try {
            if (this.realidadVirtual.participar(v)) {
                this.realidadVirtual.realizarActividadVR(v);
                this.realidadVirtual.devolverEquipo(v);
                this.realidadVirtual.obtenerFichas(v);
            }

        } catch (Exception e) {
            Salida.log(v.getIdVisitante(), "interrumpido en el parque EXCEPCION | REALIDAD VIRTUAL");
        }
    }

    public void carreraGomones(Visitante v) {
        try {
            this.carreraGomones.participar(v);
        } catch (Exception e) {
            Salida.log(v.getIdVisitante(),
                    "interrumpido en el parque EXCEPCION | CARRERA GOMONES");
        }
    }
}