package ru.yandex.practicum;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GameLogger {
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd.MM.yyyy–HH:mm");

    private BufferedWriter writer;

    public GameLogger() throws IOException {
        this.writer = new BufferedWriter(new FileWriter(createLogFile()));
    }

    private static File createLogFile() {
        String logSuffix = FORMATTER.format(new Date());
        Path logFile = Paths.get(System.getProperty("user.dir"),"logs", "log_%s.log".formatted(logSuffix));
        try {
            if (!Files.exists(logFile.getParent())) {
                Files.createDirectory(logFile.getParent());
            }
            Files.createFile(logFile);
        } catch (IOException e) {
            System.out.println("Ошибка при создании лог-файла");
            e.printStackTrace();
        }

        return logFile.toFile();
    }

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
