package ru.yandex.practicum.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GameLogger implements Logger {

    private BufferedWriter writer;

    public GameLogger() throws IOException {
        this.writer = new BufferedWriter(new FileWriter(createLogFile(), true));
        writer.write("*****************\n");
        writer.write("Н О В А Я   И Г Р А\n");
        writer.write("*****************\n");
    }

    private static File createLogFile() {
        Path logFile = Paths.get(System.getProperty("user.dir"), "logs", "GameLog.log");
        try {
            if (!Files.exists(logFile.getParent())) {
                Files.createDirectory(logFile.getParent());
            }
            Files.createFile(logFile);
        } catch (FileAlreadyExistsException ignored) {

        } catch (IOException e) {
            System.out.println("Ошибка при создании лог-файла");
            e.printStackTrace();
        }

        return logFile.toFile();
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

    public void close() throws IOException {
        writer.close();
    }
}
