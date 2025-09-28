package util;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Salida {
    private static final String FILE_NAME = "simulacion.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static boolean DEBUG = false;

    public static void log(BigInteger threadId, String action) {

        if (!DEBUG) {
            System.out.println("Visitante " + threadId + " " + action);
        } else {

            synchronized (Salida.class) {
                try (FileWriter fw = new FileWriter(FILE_NAME, true)) {
                    String currentTime = LocalDateTime.now().format(FORMATTER);
                    String MESSAGE = threadId + "|" + action + "|" + currentTime;
                    fw.write(MESSAGE + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
