package ru.yandex.practicum;

import java.util.List;
import java.util.Random;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {
    private final GameLogger logger;

    private List<String> words;

    public WordleDictionary(List<String> words, GameLogger logger) {
        this.logger = logger;
        this.words = words;
    }

    public int size() {
        return words.size();
    }

    public String getRandomWord() {
        logger.log("Выбираю случайное слово.");
        Random random = new Random();
        int r = random.nextInt(0, words.size());
        return words.get(r);
    }
}
