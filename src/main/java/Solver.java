import lombok.Getter;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Solver {

    private @Getter
    int attempts;
    private List<String> words = new ArrayList<>();
    private List<Character> mustHaveLetters = new ArrayList<>();
    private String[] possibles = new String[5];
    private Predicate<String> distinctLettersFilter;
    private Random random;
    private String latestWord;

    Solver() {
        // Import the Wordle words list
        try {
            words = Files.readAllLines(Paths.get("src/main/resources/words.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Arrays.setAll(possibles, p -> "abcdefghijklmnopqrstuvwxyz");
        distinctLettersFilter = Pattern.compile("^(?:([a-z])(?!.*\\1))*$").asPredicate();
        random = new Random();
    }

    public void setLetterAsFound(char letter, int index) {
        this.possibles[index] = String.valueOf(letter);
    }

    public void removeLetterFromAll(char letter) {
        for (int i = 0; i < 5; i++) {
            removeLetterFromColumn(letter, i);
        }
    }

    public void removeLetterFromColumn(char letter, int index) {
        possibles[index] = possibles[index].replaceFirst(String.valueOf(letter), "");
    }

    public void mustHaveLetter(char letter) {
        String mustHavePattern = (String.format("(?=.*%s)", letter));
        Predicate<String> mustHaveFilter = Pattern.compile(mustHavePattern).asPredicate();
        words = words.stream().filter(mustHaveFilter).collect(Collectors.toList());
    }

    public void processEvaluation(String[] evaluation) {
        char[] letters = latestWord.toCharArray();
        for (int i = 0; i < 5; i++) {
            switch (evaluation[i]) {
                case "absent":
                    // ToDo Duplicate letter one present or correct and one absent.
                    // if (this.result.some((s) => s.letter === letter && s.evaluation !== "absent")
//              { this.removeLetterFromColumn(letter, index);
//              } else {
//                this.removeLetterFromAll(letter);
//              }
                    removeLetterFromColumn(letters[i], i);
                    break;
                case "correct":
                    setLetterAsFound(letters[i], i);
                    break;
                case "present":
                    mustHaveLetter(letters[i]);
                    removeLetterFromColumn(letters[i], i);
                    break;
            }
        }
    }

    public String getWord() {
        attempts++;

        switch (attempts) {
            case 1:
                latestWord = "wakes";
                break;
            default:
                String viableRegEx = "";
                for (int i = 0; i < 5; i++)
                    viableRegEx += '[' + possibles[i] + ']';
                Predicate<String> wordFilter = Pattern.compile(viableRegEx).asPredicate();
                words = words.stream().filter(wordFilter).collect(Collectors.toList());

                List<String> distinctWords = words.stream().filter(distinctLettersFilter).collect(Collectors.toList());
                if (distinctWords.size() > 0) {
                    latestWord = distinctWords.get(random.nextInt(distinctWords.size()));
                } else {
                    latestWord = words.get(random.nextInt(words.size()));
                }
                break;
        }
        return latestWord;
    }
}
