package tests;

import base.BaseTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;

public class VideoPlaybackTests extends BaseTest {

    private static final String WATCH_URL = "https://vimeo.com/1179947837?fl=wc";

    @BeforeMethod(alwaysRun = true)
    public void openWatchPage() {
        driver().get(WATCH_URL);
        waitForPageReady();
        switchToVideoFrame();
        clickVideoToStart();    }

    // ---------------- SETUP HELPERS ----------------

    private void waitForPageReady() {
        new WebDriverWait(driver(), Duration.ofSeconds(15))
                .until(webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete"));
    }

    private void clickVideoToStart() {
        WebElement video = getVideo();
        
        // Create an Actions object to perform a physical click on the element
        Actions actions = new Actions(driver());
        
        // This moves the mouse to the center of the video element and clicks
        actions.moveToElement(video).click().build().perform();
        
        // Alternative: If the standard click is blocked, use JavaScript to force a play
        // ((JavascriptExecutor) driver()).executeScript("arguments[0].play();", video);
    }

    private void switchToVideoFrame() {
        driver().switchTo().defaultContent();
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(10));

        // 1. Check for iframe, but don't fail if it's missing (Vimeo uses both styles)
        try {
            By iframeLocator = By.cssSelector("iframe[src*='://vimeo.com']");
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iframeLocator));
        } catch (TimeoutException e) {
            System.out.println("Video is likely in the main document.");
        }

        // 2. Wait for the video tag
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("video")));
        } catch (TimeoutException e) {
            // If video not found, click the big play button overlay (the fixed selector)
            WebElement playOverlay = driver().findElement(By.cssSelector("button[aria-label='Play'], .vp-preview"));
            playOverlay.click();
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("video")));
        }
    }

    private WebElement getVideo() {
        // This script tries to find the video tag. If it's inside a Shadow DOM or 
        // nested, this more aggressive JS query will find it.
        return (WebElement) ((JavascriptExecutor) driver())
                .executeScript("return document.querySelector('video') || " +
                            "document.querySelector('.vp-video video') || " +
                            "document.getElementsByTagName('video')[0];");
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    // ---------------- TESTS ----------------



    @Test(priority = 2)
    public void verifyPauseVideo() {
        WebElement video = getVideo();
        ((JavascriptExecutor) driver()).executeScript("arguments[0].play();", video);
        sleep(2000);
        
        ((JavascriptExecutor) driver()).executeScript("arguments[0].pause();", video);
        sleep(1000);

        Boolean isPaused = (Boolean) ((JavascriptExecutor) driver())
                .executeScript("return arguments[0].paused;", video);

        Assert.assertTrue(isPaused, "Video should be paused.");
    }

    @Test(priority = 3)
    public void verifySeekBarInteraction() {
        WebElement video = getVideo();
        // Seek to 50% of the video
        ((JavascriptExecutor) driver())
                .executeScript("arguments[0].currentTime = arguments[0].duration * 0.5;", video);

        sleep(1000);

        Double currentTime = Double.valueOf(((JavascriptExecutor) driver())
                .executeScript("return arguments[0].currentTime;").toString());

        Assert.assertTrue(currentTime > 0, "Seek should update video time.");
    }

    @Test(priority = 4)
    public void verifyVideoDurationExists() {
        WebElement video = getVideo();
        Object durationObj = ((JavascriptExecutor) driver()).executeScript("return arguments[0].duration;", video);
        
        double duration = (durationObj instanceof Long) ? (Long) durationObj : (Double) durationObj;
        Assert.assertTrue(duration > 0, "Video duration should be greater than 0.");
    }

    @Test(priority = 5)
    public void verifyVideoCurrentTimeAdvances() {
        WebElement video = getVideo();
        ((JavascriptExecutor) driver()).executeScript("arguments[0].play();", video);

        sleep(3000);

        Double timeAfterThreeSeconds = Double.valueOf(((JavascriptExecutor) driver())
                .executeScript("return arguments[0].currentTime;").toString());

        Assert.assertTrue(timeAfterThreeSeconds > 1, "Video time should advance while playing.");
    }
}
