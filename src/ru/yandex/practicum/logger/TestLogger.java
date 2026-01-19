package ru.yandex.practicum.logger;

public class TestLogger implements Logger {

    @Override
    public void log(String message) {
        System.out.println(message);
    }

    @Override
    public void close() throws Exception {

    }
}
