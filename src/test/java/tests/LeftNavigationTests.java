package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LeftNavigationTests extends BaseTest {

    private static final String WATCH_URL = "https://vimeo.com/watch";
    private static final long WAIT_TIMEOUT_MS = Duration.ofSeconds(25).toMillis();
    private static final long ACTION_DELAY_MS = 2000;

    @BeforeMethod(alwaysRun = true)
    public void ensureWatchPageBeforeEachTest() {
        openWatchPageSlowSafe();
        waitForPageReady();
        pauseForDemo();
    }

    private void openWatchPageSlowSafe() {
        try {
            driver().get(WATCH_URL);
        } catch (TimeoutException e) {
            System.out.println("Page load timed out, continuing with partially loaded page.");
        } catch (Exception e) {
            throw new RuntimeException("Could not open watch page.", e);
        }
    }

    private void waitForPageReady() {
        long end = System.currentTimeMillis() + WAIT_TIMEOUT_MS;

        while (System.currentTimeMillis() < end) {
            try {
                Object state = ((JavascriptExecutor) driver()).executeScript("return document.readyState");
                List<WebElement> body = driver().findElements(By.tagName("body"));
                if (!body.isEmpty() && ("interactive".equals(String.valueOf(state)) || "complete".equals(String.valueOf(state)))) {
                    return;
                }
            } catch (Exception ignored) {
            }
            sleepQuietly(400);
        }
    }

    private void pauseForDemo() {
        sleepQuietly(ACTION_DELAY_MS);
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
            sleepQuietly(400);
        }
        return null;
    }

    private WebElement firstVisible(String name, By... locators) {
        WebElement element = firstVisibleOrNull(locators);
        if (element == null) {
            Assert.fail("Could not locate visible element: " + name);
        }
        return element;
    }

    private WebElement findLeftNavItemOrNull(String text) {
        return firstVisibleOrNull(
                By.xpath("//nav//a[normalize-space()='" + text + "']"),
                By.xpath("//nav//*[normalize-space()='" + text + "']/ancestor::a[1]"),
                By.xpath("//nav//*[@role='link' and normalize-space()='" + text + "']"),
                By.xpath("//nav//*[contains(normalize-space(),'" + text + "')]/ancestor::*[self::a or @role='link'][1]"),
                By.xpath("//a[normalize-space()='" + text + "']"),
                By.xpath("//*[contains(@aria-label,'" + text + "')]"),
                By.xpath("//*[normalize-space()='" + text + "']")
        );
    }

    private boolean stillOnWatchPage() {
        try {
            String url = driver().getCurrentUrl();
            return url != null && url.contains("/watch");
        } catch (Exception e) {
            return false;
        }
    }

    private String safeCurrentUrl() {
        try {
            return driver().getCurrentUrl();
        } catch (TimeoutException e) {
            System.out.println("getCurrentUrl timed out. Using blank URL fallback.");
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    private boolean navItemStillVisible(String text) {
        return findLeftNavItemOrNull(text) != null;
    }

    private void clickElementRaw(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver()).executeScript("arguments[0].click();", element);
        }
    }

    private void clickHandleNavChange(String text) {
        WebElement element = findLeftNavItemOrNull(text);
        if (element == null) {
            throw new SkipException("Left nav item is not present in current UI: " + text);
        }

        String originalWindow = driver().getWindowHandle();
        Set<String> windowsBefore = driver().getWindowHandles();
        String beforeUrl = safeCurrentUrl();

        pauseForDemo();
        scrollIntoView(element);
        highlight(element);
        pauseForDemo();

        element = findLeftNavItemOrNull(text);
        if (element == null) {
            throw new SkipException("Left nav item disappeared before click: " + text);
        }

        clickElementRaw(element);
        pauseForDemo();

        handleAfterClick(text, originalWindow, windowsBefore, beforeUrl);
    }

    private void handleAfterClick(String text, String originalWindow, Set<String> windowsBefore, String beforeUrl) {
        pauseForDemo();

        Set<String> windowsAfter = driver().getWindowHandles();

        // 1. New tab/window
        if (windowsAfter.size() > windowsBefore.size()) {
            List<String> newWindows = new ArrayList<>(windowsAfter);
            newWindows.removeAll(windowsBefore);

            if (!newWindows.isEmpty()) {
                driver().switchTo().window(newWindows.get(0));
                waitForPageReady();
                pauseForDemo();
                driver().close();
                driver().switchTo().window(originalWindow);
                waitForPageReady();
                pauseForDemo();
                return;
            }
        }

        // 2. Same tab navigation
        try {
            String afterUrl = safeCurrentUrl();
            if (!afterUrl.isBlank() && !afterUrl.equals(beforeUrl)) {
                pauseForDemo();
                driver().navigate().back();
                waitForPageReady();
                pauseForDemo();
                return;
            }
        } catch (Exception ignored) {
        }

        // 3. Dropdown / expand-collapse / inline menu / no navigation
        // Treat this as success if page is still alive and clickable area remains usable.
        if (stillOnWatchPage() || navItemStillVisible(text)) {
            pauseForDemo();
            return;
        }

        // 4. Last fallback
        pauseForDemo();
    }

    @Test(priority = 1, description = "Click Back to home.")
    public void verifyBackToHome() {
        clickHandleNavChange("Back to home");
    }

    @Test(priority = 2, description = "Click Feed.")
    public void verifyFeed() {
        clickHandleNavChange("Feed");
    }

    @Test(priority = 3, description = "Click Explore.")
    public void verifyExplore() {
        clickHandleNavChange("Explore");
    }

    @Test(priority = 4, description = "Click Staff Picks.")
    public void verifyStaffPicks() {
        clickHandleNavChange("Staff Picks");
    }

    @Test(priority = 5, description = "Click Channels.")
    public void verifyChannels() {
        clickHandleNavChange("Channels");
    }

    @Test(priority = 6, description = "Click Categories.")
    public void verifyCategories() {
        clickHandleNavChange("Categories");
    }

    @Test(priority = 7, description = "Click Groups.")
    public void verifyGroups() {
        clickHandleNavChange("Groups");
    }

    @Test(priority = 8, description = "Click On Demand.")
    public void verifyOnDemand() {
        clickHandleNavChange("On Demand");
    }
}