import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.logging.Level;

import static java.lang.Thread.sleep;

public class PageActions {

    private ChromeDriver driver;
    private SearchContext keyboardShadowRoot;
    private WebElement boardElement;

    PageActions() {
        // instantiate the Chrome driver
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        System.setProperty("webdriver.chrome.silentOutput", "true");
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
        driver = new ChromeDriver();

        String baseUrl = "https://www.nytimes.com/games/wordle/index.html";
        driver.get(baseUrl);

        // Close the GDPR dialogue
        driver.findElement(By.id("pz-gdpr-btn-accept")).click();

        // Locate and store the game shadow dom element
        WebElement shadowHost = driver.findElement(By.tagName("game-app"));
        SearchContext shadowRoot = shadowHost.getShadowRoot();
        WebElement gameShadowContent = shadowRoot.findElement(By.cssSelector("#game"));

        // Locate and store the keyboard shadow dom element to use throughout play
        shadowHost = gameShadowContent.findElement(By.tagName("game-keyboard"));
        keyboardShadowRoot = shadowHost.getShadowRoot();
        // Locate and store the results board element to use throughout play
        boardElement = gameShadowContent.findElement(By.id("board"));

        // Close the game information modal
        shadowHost = gameShadowContent.findElement(By.tagName("game-modal"));
        shadowRoot = shadowHost.getShadowRoot();
        shadowRoot.findElement(By.cssSelector(".close-icon")).click();

        // Wait for the cookies dialogue to go. It's un-closable and covers part of the keyboard.
        wait(10000);
    }

    public void enterWord(String word) {
        for (int i = 0; i < 5; i++) clickLetter(word.charAt(i));
        clickLetter('↵'); // Enter key
        // Wordle doesn't reveal word results immediately. Give it time to do its thing.
        wait(3000);

        for (int i = 1; i <= 5; i++) letterEvaluation(1, i);
    }

    private String letterEvaluation(int rowIndex, int tileIndex) {
        WebElement row = boardElement.findElement(By.cssSelector(String.format("game-row:nth-child(%s)", rowIndex)));
        SearchContext shadowRoot = row.getShadowRoot();
        WebElement tile = shadowRoot.findElement(By.cssSelector(String.format("game-tile:nth-child(%s)", tileIndex)));
        System.out.println(tile.getAttribute("letter"));
        System.out.println(tile.getAttribute("evaluation"));
        return tile.getAttribute("evaluation");
    }

    private void clickLetter(char letter) {
        keyboardShadowRoot.findElement(By.cssSelector(String.format("button[data-key='%s']", letter))).click();
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
