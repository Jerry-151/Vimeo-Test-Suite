package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public class BaseTest {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    protected WebDriver driver() {
        WebDriver webDriver = DRIVER.get();
        if (webDriver == null) {
            throw new IllegalStateException("WebDriver is not initialized for this thread.");
        }
        return webDriver;
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");

        WebDriver webDriver = new ChromeDriver(options);
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        DRIVER.set(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        WebDriver webDriver = DRIVER.get();
        if (webDriver != null) {
            try {
                webDriver.quit();
            } catch (Exception ignored) {
            } finally {
                DRIVER.remove();
            }
        }
    }

    protected void demoPause() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted during demo pause.", e);
        }
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver()).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'nearest'});",
                element
        );
    }

    protected void highlight(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver();
            String originalStyle = element.getAttribute("style");

            js.executeScript(
                    "arguments[0].setAttribute('style', arguments[1]);",
                    element,
                    originalStyle + " border: 2px solid red; background: rgba(255,255,0,0.25);"
            );

            Thread.sleep(250);

            js.executeScript(
                    "arguments[0].setAttribute('style', arguments[1]);",
                    element,
                    originalStyle
            );
        } catch (Exception ignored) {
        }
    }
}