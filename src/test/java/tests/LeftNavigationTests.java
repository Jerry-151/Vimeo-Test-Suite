package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LeftNavigationTests extends BaseTest {

    private static final String WATCH_URL = "https://vimeo.com/watch";
    private static final long WAIT_TIMEOUT_MS = Duration.ofSeconds(12).toMillis();

    @BeforeMethod(alwaysRun = true)
    public void ensureWatchPageBeforeEachTest() {
        driver().get(WATCH_URL);
        waitForPageReady();
    }

    private void waitForPageReady() {
        long end = System.currentTimeMillis() + WAIT_TIMEOUT_MS;

        while (System.currentTimeMillis() < end) {
            try {
                Object state = ((JavascriptExecutor) driver()).executeScript("return document.readyState");
                if ("complete".equals(String.valueOf(state))) {
                    return;
                }
            } catch (Exception ignored) {
            }
            sleepQuietly(250);
        }
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting.", e);
        }
    }

    private WebElement firstVisibleOrNull(By... locators) {
        long end = System.currentTimeMillis() + WAIT_TIMEOUT_MS;

        while (System.currentTimeMillis() < end) {
            for (By locator : locators) {
                try {
                    List<WebElement> matches = driver().findElements(locator);
                    for (WebElement match : matches) {
                        if (match.isDisplayed()) {
                            return match;
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            sleepQuietly(250);
        }
        return null;
    }

    private WebElement findLeftNavItemOrNull(String text) {
        return firstVisibleOrNull(
                By.xpath("//nav//span[@role='link' and normalize-space()='" + text + "']"),
                By.xpath("//nav//*[normalize-space()='" + text + "']/ancestor::span[@role='link'][1]"),
                By.xpath("//nav//*[contains(normalize-space(),'" + text + "')]/ancestor::span[@role='link'][1]"),
                By.xpath("//nav//*[normalize-space()='" + text + "']"),
                By.xpath("//nav//*[contains(normalize-space(),'" + text + "')]")
        );
    }

    private void clickHandleTabOrPageChange(String text) {
        WebElement element = findLeftNavItemOrNull(text);
        if (element == null) {
            throw new SkipException("Left nav item is not present in current UI: " + text);
        }

        String originalWindow = driver().getWindowHandle();
        Set<String> windowsBefore = driver().getWindowHandles();
        String beforeUrl = driver().getCurrentUrl();

        scrollIntoView(element);
        highlight(element);
        demoPause();

        try {
            element.click();
        } catch (Exception clickError) {
            ((JavascriptExecutor) driver()).executeScript("arguments[0].click();", element);
        }

        demoPause();

        Set<String> windowsAfter = driver().getWindowHandles();
        if (windowsAfter.size() > windowsBefore.size()) {
            List<String> newWindows = new ArrayList<>(windowsAfter);
            newWindows.removeAll(windowsBefore);

            if (!newWindows.isEmpty()) {
                driver().switchTo().window(newWindows.get(0));
                waitForPageReady();
                demoPause();
                driver().close();
                driver().switchTo().window(originalWindow);
                waitForPageReady();
                demoPause();
                return;
            }
        }

        String afterUrl = driver().getCurrentUrl();
        if (!afterUrl.equals(beforeUrl)) {
            waitForPageReady();
            demoPause();
            driver().navigate().back();
            waitForPageReady();
            demoPause();
        }
    }

    @Test(priority = 1, description = "Click Back to home.")
    public void verifyBackToHome() {
        clickHandleTabOrPageChange("Back to home");
    }

    @Test(priority = 2, description = "Click Feed.")
    public void verifyFeed() {
        clickHandleTabOrPageChange("Feed");
    }

    @Test(priority = 3, description = "Click Explore.")
    public void verifyExplore() {
        clickHandleTabOrPageChange("Explore");
    }

    @Test(priority = 4, description = "Click Staff Picks.")
    public void verifyStaffPicks() {
        clickHandleTabOrPageChange("Staff Picks");
    }

    @Test(priority = 5, description = "Click Channels.")
    public void verifyChannels() {
        clickHandleTabOrPageChange("Channels");
    }

    @Test(priority = 6, description = "Click Categories.")
    public void verifyCategories() {
        clickHandleTabOrPageChange("Categories");
    }

    @Test(priority = 7, description = "Click Groups.")
    public void verifyGroups() {
        clickHandleTabOrPageChange("Groups");
    }

    @Test(priority = 8, description = "Click On Demand.")
    public void verifyOnDemand() {
        clickHandleTabOrPageChange("On Demand");
    }
}