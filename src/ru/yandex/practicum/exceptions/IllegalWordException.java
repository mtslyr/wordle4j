package ru.yandex.practicum.exceptions;

public class IllegalWordException extends RuntimeException {
    public IllegalWordException() {
    }

    public IllegalWordException(String message) {
        super(message);
    }
}
