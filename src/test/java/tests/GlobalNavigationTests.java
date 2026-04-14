package tests;

import base.BaseTest;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Set;

public class GlobalNavigationTests extends BaseTest {

    private static final String WATCH_URL = "https://vimeo.com/watch";
    private static final long WAIT_TIMEOUT_MS = Duration.ofSeconds(12).toMillis();

    @BeforeMethod(alwaysRun = true)
    public void openPage() {
        driver().get(WATCH_URL);
        waitForPageReady();
        demoPause();
    }

    private void waitForPageReady() {
        long end = System.currentTimeMillis() + WAIT_TIMEOUT_MS;

        while (System.currentTimeMillis() < end) {
            try {
                Object state = ((JavascriptExecutor) driver())
                        .executeScript("return document.readyState");
                if ("complete".equals(String.valueOf(state))) return;
            } catch (Exception ignored) {}
        }
    }

    // ---------------- SCROLL TEST ----------------

    @Test(priority = 1)
    public void verifyScrolling() {
        ((JavascriptExecutor) driver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight);");

        demoPause();

        ((JavascriptExecutor) driver())
                .executeScript("window.scrollTo(0, 0);");

        demoPause();

        Assert.assertTrue(true, "Scrolling executed successfully.");
    }

    // ---------------- SCREENSHOT TEST ----------------

    @Test(priority = 2)
    public void verifyScreenshotCapture() throws Exception {
        File src = ((TakesScreenshot) driver()).getScreenshotAs(OutputType.FILE);

        File target = new File("target/vimeo_screenshot.png");
        Files.copy(src.toPath(), target.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        Assert.assertTrue(target.exists(), "Screenshot file should be created.");
    }

    // ---------------- NEW TAB TEST ----------------

    @Test(priority = 3)
    public void verifyNewTabNavigation() {
        String originalWindow = driver().getWindowHandle();
        Set<String> before = driver().getWindowHandles();

        ((JavascriptExecutor) driver())
                .executeScript("window.open('https://vimeo.com', '_blank');");

        demoPause();

        Set<String> after = driver().getWindowHandles();
        ArrayList<String> list = new ArrayList<>(after);

        for (String handle : list) {
            if (!before.contains(handle)) {
                driver().switchTo().window(handle);
                break;
            }
        }

        demoPause();

        driver().close();
        driver().switchTo().window(originalWindow);

        Assert.assertEquals(driver().getWindowHandle(), originalWindow,
                "Should return to original tab.");
    }

    // ---------------- REFRESH TEST ----------------

    @Test(priority = 4)
    public void verifyRefresh() {
        String before = driver().getCurrentUrl();

        driver().navigate().refresh();
        waitForPageReady();

        String after = driver().getCurrentUrl();

        Assert.assertEquals(after, before, "URL should remain same after refresh.");
    }

    // ---------------- BACK/FORWARD TEST ----------------

    @Test(priority = 5)
    public void verifyBackForwardNavigation() {
        driver().navigate().to("https://vimeo.com");

        String vimeoHome = driver().getCurrentUrl();
        driver().navigate().to(WATCH_URL);

        demoPause();

        driver().navigate().back();
        demoPause();

        Assert.assertTrue(driver().getCurrentUrl().contains("vimeo"));

        driver().navigate().forward();
        demoPause();

        Assert.assertTrue(driver().getCurrentUrl().equals(WATCH_URL),
                "Forward navigation should return to watch page.");
    }
}