package ru.yandex.practicum;

import java.io.IOException;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {
    private static final Rules RULES = new Rules(5, 6);
    public static void main(String[] args) throws IOException {
        GameLogger logger = new GameLogger();
        WordleDictionaryLoader dictionaryLoader = new WordleDictionaryLoader(RULES, logger);
        WordleDictionary dictionary = dictionaryLoader.loadDictionary();
        WordleGame game = new WordleGame(dictionary, logger, RULES);
        game.play();
    }
}
