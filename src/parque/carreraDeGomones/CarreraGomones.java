package parque.carreraDeGomones;


import parque.Visitante;
import util.Salida;


public class CarreraGomones {
    
    private final StandBicicletas standBicicletas;
    private final TrenInterno trenInterno;
    private final SistemaGomones sistemaGomones;
    private final SistemaBolsos sistemaBolsos;
    private final Camioneta camioneta;
    private final ControlLargada controlLargada;
    private final SistemaPremios sistemaPremios;
    
    public CarreraGomones(int bicicletas, int gomonesInd, int gomonesDobles, 
                          int bolsos, int gomonesParaLargada) {
        
        this.standBicicletas = new StandBicicletas(bicicletas);
        this.trenInterno = new TrenInterno();
        this.sistemaGomones = new SistemaGomones(gomonesInd, gomonesDobles);
        this.sistemaBolsos = new SistemaBolsos(bolsos);
        this.camioneta = new Camioneta();
        this.controlLargada = new ControlLargada(gomonesParaLargada);
        this.sistemaPremios = new SistemaPremios();
        
        // Iniciar hilos
        trenInterno.start();
        camioneta.start();
        sistemaPremios.iniciar();
        
        Salida.log("SISTEMA", "Carrera de Gomones ABIERTA | CARRERA GOMONES");
    }
    
    /**
     * Visitante participa en la carrera completa
     */
    public void participar(Visitante v) throws InterruptedException {
        
        // FASE 1: Transporte al inicio
        if (!llegarAlInicio(v)) {
            return;
        }
        
        // FASE 2: Obtener bolso
        Bolso bolso = sistemaBolsos.obtenerBolso(v);
        if (bolso == null) {
            Salida.log(v.getIdVisitante(), "no hay bolsos disponibles | CARRERA GOMONES");
            return;
        }
        
        // FASE 3: Transportar bolso
        camioneta.transportarBolso(bolso, v);
        
        // FASE 4: Obtener gomón
        Gomon gomon = sistemaGomones.obtenerGomon(v);
        if (gomon == null) {
            sistemaBolsos.devolverBolso(bolso, v);
            return;
        }
        
        // FASE 5: Competir
        int posicion = competir(v, gomon);
        
        // FASE 6: Retirar bolso
        camioneta.retirarBolso(bolso, v);
        sistemaBolsos.devolverBolso(bolso, v);
        
        // FASE 7: Premiar ganador
        if (posicion == 1) {
            sistemaPremios.entregarPremio(v, gomon);
        }
        
        // FASE 8: Devolver gomón
        sistemaGomones.devolverGomon(gomon, v);
    }
    
    /**
     * Fase 1: Llegar al inicio del recorrido
     */
    private boolean llegarAlInicio(Visitante v) throws InterruptedException {
        // 70% bicicleta, 30% tren
        if (Math.random() < 0.7) {
            return standBicicletas.usarBicicleta(v);
        } else {
            return trenInterno.viajar(v);
        }
    }
    
    /**
     * Fase 5: Competir en la carrera
     */
    private int competir(Visitante v, Gomon gomon) throws InterruptedException {
        // Esperar largada
        controlLargada.esperarLargada(v, gomon);
        
        // Descender por el río
        Salida.log(v.getIdVisitante(), 
            "desciende por el río en gomón " + gomon.getTipo() + " | CARRERA GOMONES");
        
        int tiempoDescenso = 5000 + (int)(Math.random() * 3000);
        Thread.sleep(tiempoDescenso);
        
        // Llegar y determinar posición
        int posicion = controlLargada.registrarLlegada(v);
        
        if (posicion == 1) {
            Salida.log(v.getIdVisitante(), 
                "¡¡¡GANADOR!!! Primer lugar | CARRERA GOMONES");
        } else {
            Salida.log(v.getIdVisitante(), 
                "posición #" + posicion + " | CARRERA GOMONES");
        }
        
        return posicion;
    }
}