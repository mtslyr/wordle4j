package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorldeDictionaryLoaderTest extends WordleTest {

    private static final String DICTIONARY_FILE_NAME = "words_ru.txt";
    @Test
    public void shouldReadAllLines() throws IOException {
        int countLines = 0;
        Path dictionaryFile = Paths.get(System.getProperty("user.dir"), DICTIONARY_FILE_NAME);
        BufferedReader br = new BufferedReader(new FileReader(dictionaryFile.toFile()));
            while (br.ready()) {
                String word = br.readLine();
                if (!word.isBlank() && !word.isEmpty()) {
                    countLines++;
                }
        }

        int actualLines = dictionaryLoader.getEntireDictionary().size();

        assertEquals(
                countLines,
                actualLines,
                "Ожидалось слов в словаре: %d. Всего слов в словаре: %d".formatted(countLines, actualLines)
        );
    }

    @Test
    void shouldLoadDictionaryWithCorrectWordLength() {
        int wordLength = 5;
        Rules testRules = new Rules(wordLength, 6);
        WordleDictionaryLoader loader = new WordleDictionaryLoader(testRules, logger);
        WordleDictionary dictionary = loader.loadDictionary();

        for (String word : dictionary.getWords()) {
            assertEquals(wordLength, word.length(),
                    "Слово '" + word + "' имеет неверную длину");
        }
    }

    @Test
    void shouldNotIncludeEmptyOrBlankLines() throws IOException {
        int originalSize = dictionaryLoader.getEntireDictionary().size();
        int normalizedSize = dictionary.getWords().size();

        assertTrue(normalizedSize <= originalSize,
                "Нормализованный словарь должен быть меньше или равен исходному");
    }
}
