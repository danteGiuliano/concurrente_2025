package parque.realidadVirtual;

import java.util.concurrent.locks.Condition;

import parque.Visitante;

class Pedido {
    Visitante visitante;
    EquipoVR equipo;
    Condition cond; 

    Pedido(Visitante v, EquipoVR e, Condition c) {
        this.visitante = v;
        this.equipo = e;
        this.cond = c;
    }
}
