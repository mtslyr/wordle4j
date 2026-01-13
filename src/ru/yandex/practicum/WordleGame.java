package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.IllegalWordException;

import java.util.Random;
import java.util.Scanner;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    private static final String MISSING = "-";
    private static final String CORRECT = "+";
    private static final String WRONG_POSITION = "^";
    private final Scanner scanner;

    private final GameLogger logger;
    private final Rules rules;

    private String answer;

    private int steps;

    private WordleDictionary dictionary;

    private boolean resolved = false;

    public WordleGame(WordleDictionary dictionary, GameLogger logger, Rules rules) {
        this.logger = logger;
        this.rules = rules;
        this.steps = 0;
        this.dictionary = dictionary;
        this.scanner = new Scanner(System.in);

        this.answer = dictionary.getRandomWord();
        logger.log("Игра создана. Загаданное слово: %s".formatted(answer));
        logger.log("Правила игры: длинна слова – %d символов, количество попыток – %d"
                .formatted(rules.WORD_LENGTH(), rules.ATTEMPTS())
        );
    }

    public void play() {
        System.out.println("Я загадал слово из 5 букв. Начинаем игру!");
        while (!resolved && steps != rules.ATTEMPTS()) {
            System.out.println("Осталось попыток: %d".formatted(rules.ATTEMPTS() - steps));
            System.out.println("Попробуй угадать слово.");
            String guess = makeGuess();
            if (guess.equals(answer)) {
                resolved = true;
                break;
            }
            processGuess(guess);
            steps += 1;
        }

        if (resolved) {
            System.out.println("Вы угадали слово!");
        } else {
            System.out.println("Вы использовали все попытки :(");
        }
    }

    private String makeGuess() {
        String guess = scanner.next();
        validateGuess(guess);
        return guess;
    }

    private void validateGuess(String guess) {
        if (guess.isEmpty() || guess.isBlank()) {
            throw new IllegalWordException("Слово не должно быть пустым");
        }

        for (Character symbol : guess.toCharArray()) {
            // если есть хоть один некириллический символ
            if (Character.UnicodeBlock.of(symbol) != Character.UnicodeBlock.CYRILLIC) {
                throw new IllegalWordException("Слово содержит недопустимый символ: %s".formatted(symbol));
            }
        }
    }

    private void processGuess(String guess) {
        StringBuilder resolution = new StringBuilder();

        for (int i = 0; i < guess.length(); i++) {
            Character symbol = guess.charAt(i);
            // если символ на своем месте
            if (answer.contains(symbol.toString()) && answer.charAt(i) == symbol) {
                resolution.append(CORRECT);
            }
            // если символ не на своем месте
            else if (answer.contains(symbol.toString()) && answer.charAt(i) != symbol) {
                resolution.append(WRONG_POSITION);
            }
            // если символ отсутсвет в загаданном слове
            else if (!answer.contains(symbol.toString())) {
                resolution.append(MISSING);
            }
        }

        System.out.println(resolution.toString());
    }
}
