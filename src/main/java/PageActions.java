import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static java.lang.Thread.sleep;

public class PageActions {

    private static final long LOAD_TIMEOUT = 10L;
    private final WebDriver driver;
    private final WebElement keyboard;
    private final Solver solver;

    PageActions() {
        // instantiate the solver
        solver = new Solver();

        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        String baseUrl = "https://www.nytimes.com/games/wordle/index.html";
        driver.get(baseUrl);

        // Close the GDPR dialogue
        driver.findElement(By.id("pz-gdpr-btn-closex")).click();

        // Click Play
        driver.findElement(By.xpath("//button[contains(@data-testid, 'Play')]")).click();

        // Click Close
        driver.findElement(By.xpath("//button[contains(@aria-label, 'Close')]")).click();

        keyboard = driver.findElement(By.xpath("//div[contains(@aria-label, 'Keyboard')]"));
    }

    public boolean enterWord() {
        String word = solver.getWord();

        for (int i = 0; i < 5; i++) clickLetter(word.charAt(i));
        clickLetter('↵'); // Enter key
        // Wordle doesn't reveal word results immediately. Give it time to do its thing.
        wait(2000);

        int attempts = solver.getAttempts();
        String[] evaluation = new String[5];
        for (int i = 0; i < 5; i++)
            evaluation[i] = letterEvaluation(attempts, i + 1);

        return solver.processEvaluation(evaluation);
    }

    private String letterEvaluation(int rowIndex, int tileIndex) {
        String tileXPath = "//div[contains(@aria-label, 'Row " + rowIndex + "')]/div[" + tileIndex + "]/div";
        WebElement tile = driver.findElement(By.xpath(tileXPath));
        return tile.getAttribute("data-state");
    }

    private void clickLetter(char letter) {
        keyboard.findElement(By.cssSelector(String.format("button[data-key='%s']", letter))).click();
    }

    public void wait(int ms) {
        try {
            sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void closeDown() {
        driver.close();
    }
}