package ru.yandex.practicum;

import ru.yandex.practicum.logger.Logger;

import java.util.*;

import static ru.yandex.practicum.WordleGame.MISSING;
import static ru.yandex.practicum.WordleGame.CORRECT;
import static ru.yandex.practicum.WordleGame.WRONG_POSITION;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {
    private final Logger logger;

    private final List<String> words;


    public WordleDictionary(List<String> words, Logger logger) {
        this.logger = logger;
        this.words = words;
    }

    public String getRandomWord() {
        logger.log("Выбираю случайное слово.");
        Random random = new Random();
        int r = random.nextInt(words.size());
        return words.get(r);
    }

    public List<String> getTips(List<Map.Entry<String, String>> guesses,
                               String answer,
                               List<String> tipsCandidates) {
        logger.log("Выбираю подсказки");
        Map<Character, String> wordFacts = getWordFactByGuesses(guesses);
        return filterWords(wordFacts, answer, tipsCandidates);
    }

    // собирает факты о загаданном слове на основании попыток пользователя в формате:
    // символ –> статус в слове
    // напрмиер: факт о слове 'корча' {а=!3, р=2, с=-, в=-, е=-, ж=-, и=-, к=!4, л=-, ы=-, м=-, о=!3}
    private Map<Character, String> getWordFactByGuesses(List<Map.Entry<String, String>> guesses) {
        logger.log("Собираю факты о загаданном слове на основании попыток пользователя");
        Map<Character, String> fact = new HashMap<>();
        for (Map.Entry<String, String> guess : guesses) {
            String origin = guess.getKey();
            String resolution = guess.getValue();

            for (int i = 0; i < origin.length(); i++) {
                if (resolution.charAt(i) == CORRECT) {
                    fact.put(origin.charAt(i), String.valueOf(i));
                } else if (resolution.charAt(i) == MISSING) {
                    fact.put(origin.charAt(i), String.valueOf(MISSING));
                } else if (resolution.charAt(i) == WRONG_POSITION) {
                    if (!fact.keySet().contains(resolution.charAt(i))) {
                        fact.put(origin.charAt(i), "!" + i);
                    } else {
                        if (fact.get(origin.charAt(i)).startsWith("!")) {
                            String actualValue = fact.get(origin.charAt(i)).concat(String.valueOf(i));
                            fact.put(origin.charAt(i), actualValue);
                        }
                    }
                }
            }
        }

        logger.log("Факты о загаданном слове: %s".formatted(fact));
        return fact;
    }

    // формирует множество слов-кандидатов
    private List<String> filterWords(Map<Character, String> fact, String answer, List<String> tipsCandidates) {
        logger.log("Формирую множество слов-кандидатов для подсказки");
        boolean filterBySymbols = false;

        // Для первой подсказки используем весь словарь, далее фильтруем только те слова,
        // которые были отобранны для предыдущих подсказок
        if (tipsCandidates == null) {
            tipsCandidates = words;
            logger.log("Исходное множество слов – весь словарь.");
        } else {
            filterBySymbols = true;
            logger.log("Величина исходного множества – %d".formatted(tipsCandidates.size()));
        }

        // список символов, не встречающихся в загаданном слове
        List<String> missingSymbols = fact.entrySet().stream()
                .filter(entry -> entry.getValue().equals("-"))
                .map(entry -> String.valueOf(entry.getKey()))
                .toList();

        // сюда собираем все слова, которые необходимо удалить из итогового списка
        Set<String> forbiddenWords = new HashSet<>();

        // FIXME O(m*n) худший случай для игры из 5 букв = 5*4165
        for (String symbol : missingSymbols) {
            for (String word : tipsCandidates) {
                if (forbiddenWords.contains(word)) {
                    continue;
                }
                if (word.contains(symbol)) {
                    forbiddenWords.add(word);
                }
            }
        }

        for (Map.Entry<Character, String> entry : fact.entrySet()) {
            if (entry.getValue().equals("-")) {
                continue;
            }

            // удаляем слова, содержащие символы не на тех позициях
            // например: слово венец, факт о слове 1=!б (на 1-ой позиции не символ 'б'), удаляем все слова,
            // у которых на 1-ой позиции символ 'б'
            if (entry.getValue().startsWith("!")) {
                for (int i = 1; i < entry.getValue().length(); i++) {
                    int wordIndex = Integer.parseInt(String.valueOf(entry.getValue().charAt(i)));
                    for (String word : tipsCandidates) {
                        if (word.charAt(wordIndex) == entry.getKey()) {
                            forbiddenWords.add(word);
                        }
                    }
                }
            } else {
                // удаляем слова, не содержащие отгаданные символы на своих позициях
                for (String word : tipsCandidates) {
                    int wordIndex = Integer.valueOf(entry.getValue());
                    if (word.charAt(wordIndex) != entry.getKey()) {
                        forbiddenWords.add(word);
                    }
                }
            }
        }

        tipsCandidates.remove(answer);

        // фильтруем слова по буквам только для подсказок не в начале игры
        if (filterBySymbols) {
            // делаем карты частот вхождения символов в ответ и слова-кандидаты
            // сравниваем карты:
            // если ключи не совпадают –> удаляем слово
            // если частота символа в слове-киндидате больше чем в ответе –> удаляем слово
            Map<Character, Integer> answerChars = createFreqMap(answer);
            for (String word : tipsCandidates) {
                Map<Character, Integer> wordChars = createFreqMap(word);
                if (!answerChars.keySet().equals(wordChars.keySet())) {
                    forbiddenWords.add(word);
                } else {
                    for (char key : answerChars.keySet()) {
                        if (wordChars.get(key) > answerChars.get(key)) {
                            forbiddenWords.add(word);
                        }
                    }
                }
            }
        }

        tipsCandidates.removeAll(forbiddenWords);

        logger.log("Для подсказок отобранно %d слов".formatted(tipsCandidates.size()));

        return tipsCandidates;
    }

    // возвращает карту частот вхождения символа в слово
    private Map<Character, Integer> createFreqMap(String word) {
        Map<Character, Integer> result = new HashMap<>();
        for (char symbol : word.toCharArray()) {
            if (!result.containsKey(symbol)) {
                result.put(symbol, 1);
            } else {
                int actualValue = result.get(symbol) + 1;
                result.put(symbol, actualValue);
            }
        }

        return result;
    }

    public List<String> getWords() {
        return words;
    }
}