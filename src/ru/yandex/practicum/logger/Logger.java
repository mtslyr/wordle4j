package ru.yandex.practicum.logger;

public interface Logger extends AutoCloseable {
    void log(String message);
}
