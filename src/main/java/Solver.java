import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

public class Solver {

    private List<String> words;
    private String[] possibles = new String[5];
    private int attempts = 0;

    Solver() {
        // Import the Wordle words list
        try {
            words = Files.readAllLines(Paths.get("src/main/resources/words.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Arrays.setAll(possibles, p -> "abcdefghijklmnopqrstuvwxyz");
    }

    public String getWord() {
        attempts++;
        System.out.println("attempts " + attempts);
        switch (attempts) {
            case 1:
                return "adieu";
            default:

                for (int i = 0; i < 5; i++)
                    clickLetter(word.charAt(i));
        String viableRegEx=
          `[${this.viableLetters[0]}]` +
          `[${this.viableLetters[1]}]` +
          `[${this.viableLetters[2]}]` +
          `[${this.viableLetters[3]}]` +
          `[${this.viableLetters[4]}]`;

        const viableRegEx = new RegExp(viablePattern);
            this.words = this.words.filter((word) => viableRegEx.test(word));

            if (this.mustHaveLetters.length > 0) {
                var mustHavePattern = "";
                this.mustHaveLetters.forEach((letter) => {
                        mustHavePattern += `(?=.*${letter})`;
          });
          const mustHaveRegEx = new RegExp(mustHavePattern);
                this.words = this.words.filter((word) => mustHaveRegEx.test(word));
            }

            console.log("length2: " + this.words.length);
        const distinctLettersPattern = /^(?:([a-z])(?!.*\1))*$/;
        const distinctLettersRegEx = new RegExp(distinctLettersPattern);
        const distinctWordsList = this.words.filter((word) =>
                    distinctLettersRegEx.test(word)
        );

            if (distinctWordsList.length > 0) {
          const word =
                        distinctWordsList[
                                Cypress._.random(0, distinctWordsList.length - 1)
                                ];
                console.log(word);
                return word;
            } else {
                return this.words[Cypress._.random(0, this.words.length - 1)];
            }
        }
    }


}
