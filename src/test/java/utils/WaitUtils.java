package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

public class WaitUtils {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(15);

    public static WebElement waitForElement(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickable(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static List<WebElement> waitForAllElements(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public static WebElement waitForFirstDisplayed(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        return wait.until(webDriver -> webDriver.findElements(locator)
                .stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElse(null));
    }

    public static void waitForUrlContains(WebDriver driver, String expectedFragment) {
        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        wait.until(ExpectedConditions.urlContains(expectedFragment));
    }

    public static void waitForPageLoad(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        wait.until(webDriver -> "complete".equals(
                ((JavascriptExecutor) webDriver).executeScript("return document.readyState")));
    }
}
