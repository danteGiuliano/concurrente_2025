package parque;

import java.math.BigInteger;

import util.Salida;

/**
 * 
 * IDEA PRINCIPAL:
 * 
 *  Un hilo que maneja un estado global. que se usa como proposito multiple para evitar la inanición 
 *  se usa un metodo de clase. para mayor simplificacion del codigo , su configuracion es clave. ya que determina muchos procesos en la simulacion.
 * 
 * 
 *  CASOS DE TEST: 
 * - parque abierto 12 segundos, cerrado 6 segundos. (2:1) 
 * - Parque cerrado 12 segundos, abierto 6 segundos (1:2) usando los logs no deberian mostrar visitantes dentro del parque. en un rango de tiempo mayor a 6 segundos.
 *  
 */

public class Reloj extends Thread {
    private final int apertura; // Duracion de Apertura en segundos
    private final int cierre;   // Duracion de Cierre en segundos
    private static boolean abierto = false;

    public Reloj( int apertura, int cierre) {
        this.apertura = apertura;
        this.cierre = cierre;
    }


    @Override
    public void run() {
        while (true) {
            esperar(apertura);
            esperar(cierre);
        }
    }

    private void esperar(int segundos) {
        try {
            Thread.sleep(segundos);
            Salida.log(BigInteger.valueOf(Thread.currentThread().getId()), abierto ? "Cerrando parque" : "Abriendo parque");
            abierto = !abierto;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static boolean operativo() {
        return abierto;
    }
}
