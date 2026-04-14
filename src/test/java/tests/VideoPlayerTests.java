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
import java.util.List;

public class VideoPlayerTests extends BaseTest {

    private static final String WATCH_URL = "https://vimeo.com/watch";
    private static final long WAIT_TIMEOUT_MS = Duration.ofSeconds(25).toMillis();
    private static final long ACTION_DELAY_MS = 2000;
    private static final long PLAY_PAUSE_DELAY_MS = 3000;

    @BeforeMethod(alwaysRun = true)
    public void openWatchPageAndOpenAnyVideo() {
        openWatchPageWithRetry();

        WebElement firstVideo = findAnyVideoCardOrNull();
        Assert.assertNotNull(firstVideo, "Could not find any video on the watch page.");

        pauseForDemo();
        scrollIntoView(firstVideo);
        highlight(firstVideo);
        pauseForDemo();

        clickElementRaw(firstVideo);
        pauseForDemo();

        WebElement player = waitForPlayerRootOrNull();
        Assert.assertNotNull(player, "Could not find Vimeo player after clicking a video.");

        scrollIntoView(player);
        highlight(player);
        pauseForDemo();
    }

    private void openWatchPageWithRetry() {
        try {
            driver().get(WATCH_URL);
        } catch (TimeoutException e) {
            System.out.println("Page load timed out, continuing with partially loaded page.");
        } catch (Exception e) {
            throw new RuntimeException("Could not open watch page.", e);
        }

        waitForPageReady();
        pauseForDemo();

        if (findAnyVideoCardOrNull() == null) {
            refreshPageSafe();
            waitForPageReady();
            pauseForDemo();
        }
    }

    private void refreshPageSafe() {
        try {
            driver().navigate().refresh();
        } catch (TimeoutException e) {
            System.out.println("Refresh timed out, continuing with partially loaded page.");
        } catch (Exception ignored) {
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

    private void jsClick(WebElement element) {
        ((JavascriptExecutor) driver()).executeScript("arguments[0].click();", element);
    }

    private WebElement firstVisible(String elementName, By... locators) {
        WebElement element = firstVisibleOrNull(locators);
        if (element == null) {
            Assert.fail("Could not locate visible element: " + elementName);
        }
        return element;
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

    private WebElement waitForPlayerRootOrNull() {
        return firstVisibleOrNull(
                By.cssSelector("div.player.js-player"),
                By.cssSelector("div[class*='player js-player']"),
                By.cssSelector(".vp-video-wrapper"),
                By.cssSelector(".vp-player-ui-container")
        );
    }

    private void moveMouseToPlayer(WebElement player) {
        try {
            ((JavascriptExecutor) driver()).executeScript(
                    "arguments[0].dispatchEvent(new MouseEvent('mousemove', {bubbles:true}));",
                    player
            );
        } catch (Exception ignored) {
        }
    }

    private WebElement findInsidePlayerOrNull(By... locators) {
        long end = System.currentTimeMillis() + WAIT_TIMEOUT_MS;

        while (System.currentTimeMillis() < end) {
            try {
                WebElement player = waitForPlayerRootOrNull();
                if (player == null) {
                    sleepQuietly(400);
                    continue;
                }

                moveMouseToPlayer(player);

                for (By locator : locators) {
                    try {
                        List<WebElement> matches = player.findElements(locator);
                        for (WebElement match : matches) {
                            if (match.isDisplayed()) {
                                return match;
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            } catch (Exception ignored) {
            }

            sleepQuietly(400);
        }

        return null;
    }

    private WebElement findAnyVideoCardOrNull() {
        return firstVisibleOrNull(
                By.xpath("(//main//a[.//img])[1]"),
                By.xpath("(//a[.//img])[1]"),
                By.xpath("(//main//a[@href])[1]")
        );
    }

    private WebElement findPlayButton() {
        WebElement playButton = findInsidePlayerOrNull(
                By.cssSelector("[data-play-button='true']")
        );

        if (playButton == null) {
            Assert.fail("Could not locate visible element: play button");
        }

        return playButton;
    }

    private WebElement findMuteButton() {
        return findInsidePlayerOrNull(
                By.cssSelector("[data-volume-button='true']")
        );
    }

    private WebElement findCCButtonOrNull() {
        return findInsidePlayerOrNull(
                By.cssSelector("[data-cc-button='true']")
        );
    }

    private WebElement findSettingsButton() {
        WebElement settingsButton = findInsidePlayerOrNull(
                By.cssSelector("[data-prefs-button='true']")
        );

        if (settingsButton == null) {
            Assert.fail("Could not locate visible element: settings button");
        }

        return settingsButton;
    }

    private WebElement findFullscreenButton() {
        WebElement fullscreenButton = findInsidePlayerOrNull(
                By.cssSelector("[data-fullscreen-button='true']")
        );

        if (fullscreenButton == null) {
            Assert.fail("Could not locate visible element: fullscreen button");
        }

        return fullscreenButton;
    }

    private WebElement findProgressBarOrNull() {
        return findInsidePlayerOrNull(
                By.cssSelector("[data-progress-bar-focus-target='true']"),
                By.cssSelector("[data-progress-bar='true']")
        );
    }

    private List<WebElement> findSettingsMenuItems() {
        WebElement player = waitForPlayerRootOrNull();
        if (player == null) {
            throw new SkipException("Player root not available.");
        }

        moveMouseToPlayer(player);
        pauseForDemo();

        return player.findElements(By.cssSelector("[data-menu-item='true']"));
    }

    private void clickElementRaw(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            jsClick(element);
        }
    }

    private void clickElement(WebElement element, String name) {
        if (element == null) {
            throw new SkipException("Element not available for click: " + name);
        }

        pauseForDemo();
        scrollIntoView(element);
        highlight(element);
        pauseForDemo();

        clickElementRaw(element);

        pauseForDemo();
    }

    @Test(priority = 1, description = "Verify player is visible.")
    public void verifyPlayerVisible() {
        WebElement player = waitForPlayerRootOrNull();
        Assert.assertNotNull(player, "Player is not visible.");
        pauseForDemo();
    }

    @Test(priority = 2, description = "Click play, wait 3 seconds, then click pause.")
    public void clickPlayPauseButton() {
        WebElement playButton = findPlayButton();

        pauseForDemo();
        scrollIntoView(playButton);
        highlight(playButton);
        pauseForDemo();

        clickElementRaw(playButton);
        sleepQuietly(PLAY_PAUSE_DELAY_MS);

        WebElement pauseButton = findPlayButton();
        scrollIntoView(pauseButton);
        highlight(pauseButton);
        pauseForDemo();

        clickElementRaw(pauseButton);
        pauseForDemo();
    }

    @Test(priority = 3, description = "Click mute button.")
    public void clickMuteButton() {
        WebElement muteButton = findMuteButton();
        if (muteButton == null) {
            throw new SkipException("Mute button not available.");
        }
        clickElement(muteButton, "mute button");
    }

    @Test(priority = 4, description = "Click CC button if present.")
    public void clickCCButton() {
        WebElement ccButton = findCCButtonOrNull();
        if (ccButton == null) {
            throw new SkipException("CC button not available.");
        }
        clickElement(ccButton, "cc button");
    }

    @Test(priority = 5, description = "Click settings button.")
    public void clickSettingsButton() {
        clickElement(findSettingsButton(), "settings button");
    }


    @Test(priority = 7, description = "Click fullscreen button.")
    public void clickFullscreenButton() {
        clickElement(findFullscreenButton(), "fullscreen button");
    }

    @Test(priority = 8, description = "Click progress bar if present.")
    public void clickProgressBar() {
        WebElement progressBar = findProgressBarOrNull();
        if (progressBar == null) {
            throw new SkipException("Progress bar not available.");
        }
        clickElement(progressBar, "progress bar");
    }
}