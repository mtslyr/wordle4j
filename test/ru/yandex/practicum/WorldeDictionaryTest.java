package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class WorldeDictionaryTest extends WordleTest {

    @Test
    public void shouldReturnNotEmptyTipsForFirstTipRequest() {
        Map.Entry<String, String> guess = Map.entry("буква", "-----");
        String answer = "гонец";
        List<String> tips = dictionary.getTips(
                List.of(guess),
                answer,
                null
        );

        assertFalse(tips.isEmpty(), "Ожидалось количество подсказок больше 0");
    }

    @Test
    void shouldFilterOutWordsWithMissingLetters() {
        Map.Entry<String, String> guess = Map.entry("буква", "-----");
        String answer = "гонец";

        List<String> tips = dictionary.getTips(List.of(guess), answer, null);

        // в подсказках не должно быть слов, содержащих буквы из слова "буква"
        for (String tip : tips) {
            assertFalse(tip.contains("б") || tip.contains("у") ||
                            tip.contains("к") || tip.contains("в") || tip.contains("а"),
                    "Подсказка содержит запрещённую букву: " + tip);
        }
    }

    @Test
    void shouldRespectCorrectPositionsInTips() {
        Map.Entry<String, String> guess = Map.entry("горка", "+^^--");
        String answer = "гонец";

        List<String> tips = dictionary.getTips(List.of(guess), answer, null);

        for (String tip : tips) {
            assertEquals('г', tip.charAt(0),
                    "В подсказке буква 'г' должна быть на 0-й позиции: " + tip);
            assertNotEquals('о', tip.charAt(1),
                    "В подсказке буква 'о' не должна быть на 1-й позиции: " + tip);
            assertNotEquals('р', tip.charAt(2),
                    "В подсказке буква 'р' не должна быть на 2-й позиции: " + tip);
        }
    }

    @Test
    void shouldExcludeAnswerFromTips() {
        Map.Entry<String, String> guess = Map.entry("гонец", "+++++"); // угадали
        String answer = "гонец";

        List<String> tips = dictionary.getTips(List.of(guess), answer, null);

        assertFalse(tips.contains(answer),
                "Загаданное слово не должно быть в подсказках");
    }
}
