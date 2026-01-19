package ru.yandex.practicum.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class GameLogger implements Logger {

    private final BufferedWriter writer;

    public GameLogger() throws IOException {
        this.writer = createLogFile();
        writer.write("*****************\n");
        writer.write("Н О В А Я   И Г Р А\n");
        writer.write("*****************\n");
    }

    private static BufferedWriter createLogFile() {
        Path logFile = Paths.get(System.getProperty("user.dir"), "logs", "GameLog.log");
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(
                    logFile,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.out.println("Ошибка при создании лог-файла");
            e.printStackTrace();
        }

        return writer;
    }

    @Override
    public void log(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException exception) {
            System.out.println("Ошибка во время записи логов");
        }

    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}