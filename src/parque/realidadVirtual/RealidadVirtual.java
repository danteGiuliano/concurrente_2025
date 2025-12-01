package parque.realidadVirtual;

import parque.Visitante;
import parque.Billetera;
import parque.Ticketera;
import util.Salida;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RealidadVirtual {

    private int visoresDisponibles;
    private int manoplasDisponibles;
    private int basesDisponibles;

    private final Lock lock = new ReentrantLock(true);
    private final Condition visitanteEsperando = lock.newCondition();

    private final Queue<Pedido> cola = new LinkedList<>();
    private final Map<Visitante, Pedido> equiposEnUso = new HashMap<>();

    private final Semaphore mutexTicketera = new Semaphore(1, true);
    private final Ticketera ticketera = new Ticketera("RV-FICHAS");

    private final Encargado encargado;

    private int visitantesAtendidos = 0; // LOS VISITANTES ATENDIDOS DEBEN COINCIDIR CON LA CANTIDAD DE VECES QUE SE ENTREGO RV FICHAS


    public RealidadVirtual(int visores, int manoplas, int bases) {

        this.visoresDisponibles = visores;
        this.manoplasDisponibles = manoplas;
        this.basesDisponibles = bases;


        encargado = new Encargado(this);
        encargado.start();
        ticketera.start();

        Salida.log("SISTEMA",
                "Realidad Virtual abierta - V:" + visores +" M:" + manoplas + " B:" + bases +"| REALIDAD VIRTUAl");
    }

    /** VISITANTE */
    public boolean participar(Visitante v) throws InterruptedException {
        EquipoVR equipo = new EquipoVR();
        Pedido pedido;

        lock.lock();
        try {
            Condition cond = lock.newCondition();
            pedido = new Pedido(v, equipo, cond);
            cola.add(pedido);

            Salida.log(v.getIdVisitante(),
                    "llega a Realidad Virtual | REALIDAD VIRTUAL");

            visitanteEsperando.signal();

            // Esperar hasta que su propio equipo esté completo
            while (!equipo.estaCompleto()) {
                Salida.log(v.getIdVisitante(),
                        "espera componentes... | REALIDAD VIRTUAL");
                cond.await();
            }

        } finally {
            lock.unlock();
        }

        return true;
    }

    /** ENCARGADO */
    boolean distribuirComponente() throws InterruptedException {
        lock.lock();
        try {
            while (cola.isEmpty()) {
                visitanteEsperando.await();
            }

            Pedido p = cola.peek();
            EquipoVR eq = p.equipo;

            boolean entregado = false;

            if (!eq.tieneVisor() && visoresDisponibles > 0) {
                visoresDisponibles--;
                eq.agregarVisor();
                entregado = true;

                Salida.log("ENCARGADO",
                        "entrega VISOR a visitante " + p.visitante.getIdVisitante() + " | REALIDAD VIRTUAL");
            }
            if (eq.getManoplas() < 2 && manoplasDisponibles > 0) {
                manoplasDisponibles--;
                eq.agregarManopla();
                entregado = true;

                Salida.log("ENCARGADO",
                        "entrega MANOPLA a visitante " + p.visitante.getIdVisitante() +
                                " (" + eq.getManoplas() + "/2) | REALIDAD VIRTUAL");
            }
            if (!eq.tieneBase() && basesDisponibles > 0) {
                basesDisponibles--;
                eq.agregarBase();
                entregado = true;

                Salida.log("ENCARGADO",
                        "entrega BASE a visitante " + p.visitante.getIdVisitante() + "| REALIDAD VIRTUAL");
            }

            if (eq.estaCompleto()) {
                this.visitantesAtendidos++;
                Salida.log("ENCARGADO",
                        "equipo COMPLETO para visitante " + p.visitante.getIdVisitante() + " VISITANTES ATENDIDOS:"+this.visitantesAtendidos+"| REALIDAD VIRTUAL");

                cola.remove(); // ya no está esperando equipo
                equiposEnUso.put(p.visitante, p); // lo registramos como equipo en uso
                p.cond.signal(); // despierta al visitante
            }

            return entregado;

        } finally {
            lock.unlock();
        }
    }

    /** Visitante */
    public void devolverEquipo(Visitante v) {
        lock.lock();
        try {
            Pedido pedido = equiposEnUso.remove(v); 

            if (pedido == null) {
                Salida.log("ERROR", "Visitante " + v.getIdVisitante() +
                        " intenta devolver equipo pero no tiene uno asignado");
                return;
            }

            EquipoVR eq = pedido.equipo;

            if (eq.tieneVisor())
                visoresDisponibles++;
            manoplasDisponibles += eq.getManoplas();
            if (eq.tieneBase())
                basesDisponibles++;

            Salida.log("SISTEMA",
                    "equipo devuelto - VISORES DISPONIBLES:" + visoresDisponibles +
                            " MANOPLAS DISPONIBLES:" + manoplasDisponibles +
                            " BASES DISPONIBLES:" + basesDisponibles + "| REALIDAD VIRTUAL");

            visitanteEsperando.signal(); // por si alguien está esperando componentes

        } finally {
            lock.unlock();
        }
    }

    
    public void realizarActividadVR(Visitante v) throws InterruptedException {
        Salida.log(v.getIdVisitante(),
                "ingresa a la experiencia VR | REALIDAD VIRTUAL");

        Thread.sleep(6000);

        Salida.log(v.getIdVisitante(),
                "finaliza la experiencia VR | REALIDAD VIRTUAL");
    }

    public void obtenerFichas(Visitante v) throws InterruptedException {
        mutexTicketera.acquire();
        try {
            Salida.log(v.getIdVisitante(), "obtiene fichas RV | REALIDAD VIRTUAL");

            Exchanger<Billetera> lector = ticketera.getExchanger();
            lector.exchange(v.getBilletera());
            Billetera actualizada = lector.exchange(null);
            v.setBilletera(actualizada);

        } finally {
            mutexTicketera.release();
        }
    }

}
