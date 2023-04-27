import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.time.Duration;

import static java.lang.Thread.sleep;
import static org.openqa.selenium.chrome.ChromeDriverService.CHROME_DRIVER_VERBOSE_LOG_PROPERTY;
import static org.openqa.selenium.chrome.ChromeDriverService.CHROME_DRIVER_WHITELISTED_IPS_PROPERTY;

public class PageActions {

    private static final long LOAD_TIMEOUT = 10L;
    private final RemoteWebDriver driver;
    private final WebElement keyboard;
    private final Solver solver;
    PageActions() {
        // instantiate the solver
        solver = new Solver();

        // instantiate the Chrome driver
        WebDriverManager.chromedriver().setup();
        System.setProperty(CHROME_DRIVER_VERBOSE_LOG_PROPERTY, "false");
        System.setProperty(CHROME_DRIVER_WHITELISTED_IPS_PROPERTY, "127.0.0.1");
        driver = new ChromeDriver(getChromeOptions());
        configureDriver(driver);

        String baseUrl = "https://www.nytimes.com/games/wordle/index.html";
        driver.get(baseUrl);

        // Close the GDPR dialogue
        driver.findElement(By.id("pz-gdpr-btn-closex")).click();
        // Close How to play
        driver.findElement(By.xpath("//button[contains(@aria-label, 'Close')]")).click();

        keyboard = driver.findElement(By.xpath("//div[contains(@aria-label, 'Keyboard')]"));
    }

    private static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-logging");
        options.addArguments("--log-level=3");
        options.addArguments("--disable-crash-reporter");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.setHeadless(false);
        return options;
    }

    private static void configureDriver(RemoteWebDriver remoteWebDriver) {
        remoteWebDriver.manage().window().maximize();
        remoteWebDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(LOAD_TIMEOUT));
        remoteWebDriver.manage().window().setPosition(new Point(0, 0));
        remoteWebDriver.manage().window().setSize(new Dimension(1920, 1080));
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
            evaluation[i] = letterEvaluation(attempts, i+1);

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