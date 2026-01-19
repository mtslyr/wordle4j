package ru.yandex.practicum;

import ru.yandex.practicum.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {

    private static final String DICTIONARY_FILE_NAME = "words_ru.txt";
    private final Rules rules;
    private final Logger logger;
    private final List<String> entireDictionary;

    public WordleDictionaryLoader(Rules rules, Logger logger) {
        this.rules = rules;
        this.logger = logger;
        entireDictionary = new ArrayList<>();
    }

    public WordleDictionary loadDictionary() {
        logger.log("Создаю игровой словарь.");
        readDictionaryFile();
        List<String> gameDictionary = normalizeDictionary();
        logger.log("Игровой словарь создан. Всего слов для игры: %d".formatted(gameDictionary.size()));
        return new WordleDictionary(gameDictionary, logger);
    }

    private void readDictionaryFile() {
        logger.log("Читаю файл со словами.");
        Path dictionaryFile = Paths.get(System.getProperty("user.dir"), DICTIONARY_FILE_NAME);
        try (BufferedReader br = Files.newBufferedReader(dictionaryFile)) {
            while (br.ready()) {
                String word = br.readLine();
                if (wordIsValid(word)) {
                    entireDictionary.add(word);
                } else {
                    logger.log("Слово из файла не прошло проверку: '%s'".formatted(word));
                }
            }
        } catch (IOException exception) {
            logger.log("Ошибка во время чтения файла со словами:");
            for (StackTraceElement ste : exception.getStackTrace()) {
                logger.log(ste.toString());
            }
        }
    }

    private List<String> normalizeDictionary() {
        logger.log("Нормализую словарь согласно правилам игры: длинна слов – %d символов".formatted(rules.wordLength()));
        List<String> normalized = new ArrayList<>();
        for (String word : entireDictionary) {
            if (word.length() == rules.wordLength()) {
                normalized.add(word);
            }
        }

        return normalized;
    }

    public boolean wordIsValid(String word) {
        if (word.isBlank()) {
            return false;
        }

        for (char symbol : word.toCharArray()) {
            if (symbol == '-') {
                // поскольку данный символ не относится к кириллическим валидация слова не пройдет,
                // даже если остальные символы кириллические
                continue;
            }
            if (Character.UnicodeBlock.of(symbol) != Character.UnicodeBlock.CYRILLIC) {
                return false;
            }
        }

        return true;
    }

    public List<String> getEntireDictionary() {
        return List.copyOf(entireDictionary);
    }
}
