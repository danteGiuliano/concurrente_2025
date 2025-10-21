package util;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Semaphore;

public class Salida {
    private static final String FILE_NAME = "simulacion.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static boolean DEBUG = false;

    private static final Semaphore mutex = new Semaphore(1 , true);

    public static void log(Object threadId, String action) {
        if (!DEBUG) {
            System.out.println("Thread-" + Thread.currentThread().getName() + 
                             " (Visitante " + threadId + ") " + action);
            return;
        }

        String currentTime = LocalDateTime.now().format(FORMATTER);
        String message = "#" + Thread.currentThread().getName() + 
                        "|" + action + 
                        "|" + currentTime + "#\n";

        try {
            mutex.acquire();
            try (FileWriter fw = new FileWriter(FILE_NAME, true)) {
                fw.write(message);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
        }
    }
}
