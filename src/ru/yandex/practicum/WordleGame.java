package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.GameException;
import ru.yandex.practicum.logger.Logger;

import java.util.*;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее
    –> вынес функционал подбора подсказок в класс-словарь


не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    public static final char MISSING = '-';
    public static final char CORRECT = '+';
    public static final char WRONG_POSITION = '^';
    private final Scanner scanner;

    private final Logger logger;
    private Rules rules;

    private String answer;

    private int steps;

    private WordleDictionary dictionary;

    private boolean resolved = false;
    private boolean tipRequested = false;

    private List<Map.Entry<String, String>> guesses; // здесь храним ответы пользователя и их расшифровки

    private List<String> tipsCandidates; // актуальные подсказки по последней догадке пользователя
    private List<Map.Entry<String, String>> resolvedGuesses; // здесь храним попытки пользователя и их расшифровки
    private Set<String> usedTips; // здесь храним использованные подсказки

    public WordleGame(WordleDictionary dictionary, Logger logger, Rules rules) {
        this.logger = logger;
        this.rules = rules;
        this.steps = 0;
        this.dictionary = dictionary;
        this.scanner = new Scanner(System.in);
        this.guesses = new LinkedList<>();
        this.usedTips = new HashSet<>();
        this.resolvedGuesses = new ArrayList<>();

        this.answer = dictionary.getRandomWord();
        logger.log("Игра создана. Загаданное слово: %s".formatted(answer));
        logger.log("Правила игры: длинна слова – %d символов, количество попыток – %d"
                .formatted(rules.WORD_LENGTH(), rules.ATTEMPTS())
        );
    }

    public void play() {
        System.out.println("Я загадал слово из %s букв. Начинаем игру!".formatted(rules.WORD_LENGTH()));
        while (!resolved && steps != rules.ATTEMPTS()) {
            tipRequested = false;
            logger.log("Игрой цикл запущен. Осталось попыток: %d".formatted(rules.ATTEMPTS() - steps));
            System.out.println("Осталось попыток: %d".formatted(rules.ATTEMPTS() - steps));
            System.out.println("Попробуй угадать слово.");

            try {
                makeGuess();
                if (resolved) {
                    break;
                }

                if (!tipRequested) {
                    steps += 1;
                }
            } catch (GameException e) {
                logger.log("[EXCEPTION] %s".formatted(e.getMessage()));
                System.out.println("Предупреждение: %s".formatted(e.getMessage()));
                System.out.println("Попробуйте еще раз!");
            }
        }

        if (resolved) {
            System.out.println("Вы угадали слово!");
        } else {
            System.out.println("Вы использовали все попытки :(");
            System.out.println("Загаданное слово: %s".formatted(answer));
        }
    }

    private void makeGuess() {
        String guess = scanner.nextLine();
        if (!guess.isEmpty()) {
            logger.log("Пользователь ввел слово: %s".formatted(guess));
            if (guess.equals(answer)) {
                resolved = true;
                return;
            }

            validateGuess(guess);
            String resolution = processGuess(guess);
            Map.Entry<String, String> guessPair = Map.entry(guess, resolution);
            resolvedGuesses.add(guessPair);
            tipsCandidates = dictionary.getTips(resolvedGuesses, answer, tipsCandidates);

            guesses.add(Map.entry(guess, resolution));
            System.out.println(resolution);
        } else {
            logger.log("Пользователь просит подсказку.");
            tipRequested = true;
            makeTip(guess);
        }
    }

    private void validateGuess(String guess) {
        if (guess.length() != rules.WORD_LENGTH() && !guess.isEmpty()) {
            throw new GameException("Недопустимая длина слова. Текущие правила по длине слова: %d символов".formatted(rules.WORD_LENGTH()));
        }

        for (Character symbol : guess.toCharArray()) {
            // если есть хоть один некириллический символ
            if (Character.UnicodeBlock.of(symbol) != Character.UnicodeBlock.CYRILLIC) {
                throw new GameException("Слово содержит недопустимый символ '%s'".formatted(symbol));
            }
        }
        logger.log("Слово удовлетворяет условиям");
    }

    private String processGuess(String guess) {
        StringBuilder resolution = new StringBuilder();

        for (int i = 0; i < guess.length(); i++) {
            char symbol = guess.charAt(i);
            // если символ на своем месте
            if (answer.contains(Character.toString(symbol)) && answer.charAt(i) == symbol) {
                resolution.append(CORRECT);
            } else if (answer.contains(Character.toString(symbol)) && answer.charAt(i) != symbol) {
                // если символ не на своем месте
                resolution.append(WRONG_POSITION);
            } else if (!answer.contains(Character.toString(symbol))) {
                // если символ отсутсвет в загаданном слове
                resolution.append(MISSING);
            }
        }

        return resolution.toString();
    }

    private void makeTip(String guess) {
        // получаем ключ для карты [слово-расшифрока] ––– [список подсказок]
        Map.Entry<String, String> guessKey = Map.entry(guess, processGuess(guess));

        // подсказки
        List<String> tips;

        if (tipsCandidates != null) {
            tips = tipsCandidates;
        } else {
            tips = dictionary.getTips(resolvedGuesses, answer, tipsCandidates);
            // сохраняем подсказки для вычисления будущих подсказок
            guesses.add(guessKey);
        }

        if (tips.isEmpty()) {
            System.out.println("Больше подсказок нет(");
            return;
        }

        // получаем случайное слово из кандидатов в подсказки
        Random random = new Random();
        int tipIndex = random.nextInt(0, tips.size());
        String tip = tips.get(tipIndex);
        while (usedTips.contains(tip)) {
            tipIndex = random.nextInt(0, tips.size());
            tip = tips.get(tipIndex);
        }

        // сохраняем использованную подсказку
        usedTips.add(tip);

        logger.log("Выбрана подсказка: %s".formatted(tip));
        System.out.println("Подсказка: %s".formatted(tip));
    }

    public void setRules(Rules rules) {
        this.rules = rules;
    }
}
