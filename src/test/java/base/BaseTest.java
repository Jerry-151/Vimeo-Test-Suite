package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import utils.WaitUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

public class BaseTest {

    protected static final String LOGIN_URL = "https://vimeo.com/log_in";

    // Only keep the visible 2-second pause around user actions.
    protected static final int DEMO_DELAY_MS = 2000;

    // Remove extra dead time between tests.
    protected static final int TEST_TRANSITION_DELAY_MS = 0;

    private static final ThreadLocal<WebDriver> THREAD_DRIVER = new ThreadLocal<>();
    protected static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        log.info("Launching Chrome browser...");

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--window-size=1920,1080");

        THREAD_DRIVER.set(createChromeDriver(options));

        driver().manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        driver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(45));
        driver().manage().window().setSize(new Dimension(1920, 1080));

        log.info("Opening default Vimeo page...");
        openLoginPage();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        log.info("Closing browser...");

        if (driver() != null) {
            driver().quit();
            THREAD_DRIVER.remove();
        }
    }

    protected WebDriver driver() {
        return THREAD_DRIVER.get();
    }

    protected void openLoginPage() {
        driver().get(LOGIN_URL);
        WaitUtils.waitForPageLoad(driver());
    }

    private WebDriver createChromeDriver(ChromeOptions options) {
        RuntimeException lastFailure = null;

        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                ChromeDriverService service = new ChromeDriverService.Builder()
                        .usingAnyFreePort()
                        .build();
                return new ChromeDriver(service, options);
            } catch (RuntimeException error) {
                lastFailure = error;
                sleep(500);
            }
        }

        throw new RuntimeException("Unable to start ChromeDriver after retries", lastFailure);
    }

    protected void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sleep interrupted", e);
        }
    }

    protected void highlight(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver();
        js.executeScript("arguments[0].style.border='3px solid red'", element);
    }

    protected void demoPause() {
        sleep(DEMO_DELAY_MS);
    }

    protected void pauseBetweenTests() {
        if (TEST_TRANSITION_DELAY_MS > 0) {
            sleep(TEST_TRANSITION_DELAY_MS);
        }
    }

    protected WebElement linkContainingText(String text) {
        String xpath = "//a[contains(normalize-space(.), " + xpathLiteral(text) + ")]";
        WebElement element = WaitUtils.waitForFirstDisplayed(driver(), By.xpath(xpath));
        highlight(element);
        return element;
    }

    protected WebElement exactLinkText(String text) {
        WebElement element = WaitUtils.waitForFirstDisplayed(driver(), By.linkText(text));
        highlight(element);
        return element;
    }

    protected WebElement partialLinkText(String text) {
        WebElement element = WaitUtils.waitForFirstDisplayed(driver(), By.partialLinkText(text));
        highlight(element);
        return element;
    }

    protected WebElement findVisibleByCss(String cssSelector) {
        WebElement element = WaitUtils.waitForFirstDisplayed(driver(), By.cssSelector(cssSelector));
        highlight(element);
        return element;
    }

    protected WebElement findVisibleByTag(String tagName) {
        WebElement element = WaitUtils.waitForFirstDisplayed(driver(), By.tagName(tagName));
        highlight(element);
        return element;
    }

    protected WebElement findChildByCss(WebElement parent, String cssSelector) {
        WebElement element = parent.findElement(By.cssSelector(cssSelector));
        highlight(element);
        return element;
    }

    protected WebElement findChildByLinkText(WebElement parent, String linkText) {
        WebElement element = parent.findElement(By.linkText(linkText));
        highlight(element);
        return element;
    }

    protected WebElement headingContainingText(String text) {
        String xpath = "//*[self::h1 or self::h2 or self::h3 or self::h4][contains(normalize-space(.), "
                + xpathLiteral(text) + ")]";
        WebElement element = WaitUtils.waitForFirstDisplayed(driver(), By.xpath(xpath));
        highlight(element);
        return element;
    }

    protected WebElement textBlockContaining(String text) {
        String xpath = "//*[self::p or self::div or self::span or self::section][contains(normalize-space(.), "
                + xpathLiteral(text) + ")]";
        WebElement element = WaitUtils.waitForFirstDisplayed(driver(), By.xpath(xpath));
        highlight(element);
        return element;
    }

    protected void clickAndVerifyUrl(WebElement element, String expectedUrlFragment) {
        scrollIntoView(element);
        demoPause();
        element.click();
        WaitUtils.waitForPageLoad(driver());
        WaitUtils.waitForUrlContains(driver(), expectedUrlFragment);
        demoPause();
    }

    protected void clickWithPause(WebElement element) {
        scrollIntoView(element);
        highlight(element);
        demoPause();
        element.click();
        demoPause();
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver()).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'nearest'});", element);
    }

    protected void assertHrefContains(WebElement element, String expectedFragment) {
        String href = element.getAttribute("href");
        if (href == null || !href.contains(expectedFragment)) {
            throw new AssertionError("Expected href to contain '" + expectedFragment + "' but was '" + href + "'");
        }
    }

    protected String hostFromHref(WebElement element) {
        try {
            return new URI(element.getAttribute("href")).getHost();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid href on element", e);
        }
    }

    private String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }

        String[] parts = value.split("'");
        StringBuilder xpath = new StringBuilder("concat(");
        for (int i = 0; i < parts.length; i++) {
            xpath.append("'").append(parts[i]).append("'");
            if (i < parts.length - 1) {
                xpath.append(", \"'\", ");
            }
        }
        xpath.append(")");
        return xpath.toString();
    }
}