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
        
        Salida.log("SISTEMA", "Carrera de Gomones ABIERTA | CARRERA GOMONES");
    }
    
   
    public void participar(Visitante v) throws InterruptedException {
        //1
        if (!llegarAlInicio(v)) {
            return;
        }
        //2
        Bolso bolso = sistemaBolsos.obtenerBolso(v);
        if (bolso == null) {
            Salida.log(v.getIdVisitante(), "no hay bolsos disponibles | CARRERA GOMONES");
            return;
        }
        //3
        camioneta.transportarBolso(bolso, v);
        //4
        Gomon gomon = sistemaGomones.obtenerGomon(v);
        if (gomon == null) {
            sistemaBolsos.devolverBolso(bolso, v);
            return;
        }
        //5
        int posicion = competir(v, gomon);
        
        // 6
        camioneta.retirarBolso(bolso, v);
        sistemaBolsos.devolverBolso(bolso, v);
        
        // 7
        if (posicion == 1) {
            sistemaPremios.entregarPremio(v, gomon);
        }
        
        // 8
        sistemaGomones.devolverGomon(gomon, v);
    }
    
 
    private boolean llegarAlInicio(Visitante v) throws InterruptedException {
        if (Math.random() < 0.5) {
            return standBicicletas.usarBicicleta(v);
        } else {
            return trenInterno.viajar(v);
        }
    }
    
  
    private int competir(Visitante v, Gomon gomon) throws InterruptedException {
        // Esperar largada
        controlLargada.esperarLargada(v, gomon);
        
        // Descender por el río
        Salida.log(v.getIdVisitante(), 
            "desciende por el rio en gomón " + gomon.getTipo() + " | CARRERA GOMONES");
        
        int tiempoDescenso = 5000 + (int)(Math.random() * 3000);
        Thread.sleep(tiempoDescenso);
        
        // Llegar y determinar posición
        int posicion = controlLargada.registrarLlegada(v);
        
        if (posicion == 1) {
            Salida.log(v.getIdVisitante(), 
                " GANADOR Primer lugar | CARRERA GOMONES");
        } else {
            Salida.log(v.getIdVisitante(), 
                "posicion N:" + posicion + " | CARRERA GOMONES");
        }
        
        return posicion;
    }
}