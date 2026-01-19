package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeAll;
import ru.yandex.practicum.logger.Logger;
import ru.yandex.practicum.logger.TestLogger;

import java.io.IOException;

class WordleTest {

    public static final Logger logger = new TestLogger();
    public static WordleGame game;
    public static WordleDictionaryLoader dictionaryLoader;
    public static WordleDictionary dictionary;
    @BeforeAll
    public static void beforeAll() throws IOException {
        Rules testRules = new Rules(5, 6);
        dictionaryLoader = new WordleDictionaryLoader(testRules, logger);
        dictionary = dictionaryLoader.loadDictionary();
    }
}
