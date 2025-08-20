package TP_02.EJ_2;

public class MiEjecucion extends Thread {
    public void run() {
        ir();
    }

    public void ir() {
        hacerMas();
    }

    public void hacerMas() {
        System.out.println("En la pila");
    }
}

class TesteoHilos {
    public static void main(String[] args) {
        Thread miHilo = new MiEjecucion();
        System.out.println("En el main");
        miHilo.start();
    }
}