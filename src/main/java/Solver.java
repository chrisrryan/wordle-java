import lombok.Getter;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Solver {

    private @Getter int attempts;
    private List<String> words = new ArrayList<>();
    private final String[] possibles = new String[5];
    private final Predicate<String> distinctLettersFilter;
    private final Random random;
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

    public boolean processEvaluation(String[] evaluation) {
        boolean done = Arrays.stream(evaluation).allMatch(s -> s.equals("correct")) || attempts == 6;
        if (!done) {
        char[] letters = latestWord.toCharArray();
        for (int i = 0; i < 5; i++) {
            switch (evaluation[i]) {
                case "absent":
                    // Duplicate letter one present/correct and one absent.
                    boolean repeatingLetterTooMany = false;
                    //Is this letter repeated in the word?
                    if (5 - latestWord.replace(String.valueOf(letters[i]), "").length() >= 2) {
                       // If so, check if any are flagged as present or correct.
                       for (int j = 0; j < 5; j++) {
                           if (letters[j] == letters[i] && !evaluation[j].equals("absent"))
                               repeatingLetterTooMany = true;
                       }
                    }
                    if (repeatingLetterTooMany) {
                        removeLetterFromColumn(letters[i], i);
                    } else {
                        removeLetterFromAll(letters[i]);
                    }
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
        return done;
    }

    public String getWord() {
        attempts++;

        if (attempts == 1) {
            latestWord = "adieu";
        } else {
            StringBuilder viableRegEx = new StringBuilder();
            for (int i = 0; i < 5; i++)
                viableRegEx.append('[').append(possibles[i]).append(']');
            Predicate<String> wordFilter = Pattern.compile(viableRegEx.toString()).asPredicate();
            words = words.stream().filter(wordFilter).collect(Collectors.toList());

            List<String> distinctWords = words.stream().filter(distinctLettersFilter).toList();
            if (!distinctWords.isEmpty()) {
                latestWord = distinctWords.get(random.nextInt(distinctWords.size()));
            } else {
                latestWord = words.get(random.nextInt(words.size()));
            }
        }
        return latestWord;
    }
}
