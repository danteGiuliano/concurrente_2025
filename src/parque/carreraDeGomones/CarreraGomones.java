package parque.carreraDeGomones;


import parque.Visitante;
import util.Salida;


public class CarreraGomones {
    
    private final StandBicicletas standBicicletas;
    private final TrenInterno trenInterno;
    private final SistemaGomones sistemaGomones;
    private final SistemaBolsos sistemaBolsos;
    private final Camioneta computadora;
    private final ControlLargada controlLargada;
    private final SistemaPremios sistemaPremios;
    
    public CarreraGomones(int bicicletas, int gomonesInd, int gomonesDobles, 
                          int bolsos, int gomonesParaLargada) {
        
        this.standBicicletas = new StandBicicletas(bicicletas);
        this.trenInterno = new TrenInterno(15);
        this.sistemaGomones = new SistemaGomones(gomonesInd, gomonesDobles);
        this.sistemaBolsos = new SistemaBolsos(bolsos);
        this.computadora = new Camioneta();
        this.controlLargada = new ControlLargada(gomonesParaLargada);
        this.sistemaPremios = new SistemaPremios();
        
    
        computadora.start();
        
        Salida.log("SISTEMA", "Carrera de Gomones ABIERTA | CARRERA GOMONES");
    }
    
   
    public void participar(Visitante v) throws InterruptedException {
        // El visitante elige entre bicicleta o tren para llegar al inicio
        if (!llegarAlInicio(v)) {
            return;
        }
        
        // Obtener un bolso con llave para guardar pertenencias
        Bolso bolso = sistemaBolsos.obtenerBolso(v);
        Thread.sleep(1000);
        if (bolso == null) {
            Salida.log(v.getIdVisitante(), "no hay bolsos disponibles | CARRERA GOMONES");
            return;
        }
        
        // La camioneta transporta el bolso al final del recorrido
        computadora.transportarBolso(bolso, v);
        
        // Obtener gomón (individual o doble)
        Gomon gomon = sistemaGomones.obtenerGomon(v);
        if (gomon == null) {
            sistemaBolsos.devolverBolso(bolso, v);
            return;
        }
        
        // Competir en la carrera
        int tiempoDescenso = 5000 + (int)(Math.random() * 3000);
        int posicion = competir(v, gomon, tiempoDescenso);
        
        // Al finalizar, retirar el bolso que fue transportado
        computadora.retirarBolso(bolso, v);
        Thread.sleep(500);
        sistemaBolsos.devolverBolso(bolso, v);
        
        // El ganador recibe fichas (ambos si es gomón doble)
        if (posicion == 1) {
            sistemaPremios.entregarPremio(v, gomon);
        }
        
        // Devolver el gomón al sistema
        sistemaGomones.devolverGomon(gomon, v);
    }
    
  
    private boolean llegarAlInicio(Visitante v) throws InterruptedException {
        if (Math.random() < 0.5) {
            boolean resultado = standBicicletas.usarBicicleta(v);
            Thread.sleep(2000);
            return resultado;
        } else {
            return trenInterno.viajar(v);
        }
    }
    
  
    private int competir(Visitante v, Gomon gomon, int tiempoDescenso) throws InterruptedException {
        // Esperar a que haya G gomones listos y se lance la carrera
        controlLargada.esperarLargada(v, gomon);
        controlLargada.verificarYLanzarCarrera();
        
        // Descender por el río
        Salida.log(v.getIdVisitante(), 
            "desciende por el rio en gomon " + gomon.getTipo() + " | CARRERA GOMONES");
        
        // Tiempo aleatorio de descenso (simula competencia)
        Thread.sleep(tiempoDescenso);
        
        // Registrar posición de llegada
        int posicion = controlLargada.registrarLlegada(v);
        
        if (posicion == 1) {
            Salida.log(v.getIdVisitante(), 
                " GANADOR Primer lugar posicion 1 | CARRERA GOMONES");
        } else {
            Salida.log(v.getIdVisitante(), 
                "posicion N:" + posicion + " | CARRERA GOMONES");
        }
        
        return posicion;
    }
}
