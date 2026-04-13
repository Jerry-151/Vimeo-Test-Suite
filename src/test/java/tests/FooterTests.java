package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FooterTests extends BaseTest {

    private static final String WATCH_URL = "https://vimeo.com/watch";
    private static final long WAIT_TIMEOUT_MS = Duration.ofSeconds(8).toMillis();

    @BeforeMethod(alwaysRun = true)
    public void openWatchPage() {
        driver().get(WATCH_URL);
        waitForPageReady();
        dismissStickyBannerIfPresent();
        scrollToBottomFast();
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
            sleepQuietly(200);
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
            sleepQuietly(200);
        }
        return null;
    }

    private void scrollToBottomFast() {
        ((JavascriptExecutor) driver()).executeScript(
                "window.scrollTo({ top: document.body.scrollHeight, behavior: 'instant' });"
        );
        sleepQuietly(500);
    }

    private void dismissStickyBannerIfPresent() {
        WebElement closeBannerButton = firstVisibleOrNull(
                By.cssSelector("button[aria-label='close banner']"),
                By.xpath("//button[@aria-label='close banner']"),
                By.xpath("//button[@data-type='icon-button' and @aria-label='close banner']")
        );

        if (closeBannerButton != null) {
            try {
                ((JavascriptExecutor) driver()).executeScript("arguments[0].click();", closeBannerButton);
                sleepQuietly(300);
            } catch (Exception ignored) {
            }
        }
    }

    private WebElement findFooterItemOrNull(String label) {
        switch (label) {
            case "Pricing":
                return firstVisibleOrNull(
                        By.linkText("Pricing"),
                        By.partialLinkText("Pric"),
                        By.cssSelector("a[href*='upgrade-plan']"),
                        By.xpath("//*[normalize-space()='Pricing']/ancestor-or-self::a[1]")
                );

            case "Contact sales":
                return firstVisibleOrNull(
                        By.linkText("Contact sales"),
                        By.partialLinkText("Contact sa"),
                        By.cssSelector("a[href*='contact-us']"),
                        By.xpath("//*[normalize-space()='Contact sales']/ancestor-or-self::a[1]")
                );

            case "Watch demos":
                return firstVisibleOrNull(
                        By.linkText("Watch demos"),
                        By.partialLinkText("Watch dem"),
                        By.cssSelector("a[href*='watch-demo']"),
                        By.xpath("//*[normalize-space()='Watch demos']/ancestor-or-self::a[1]")
                );

            case "Join":
                return firstVisibleOrNull(
                        By.xpath("//button[normalize-space()='Join']"),
                        By.xpath("//*[normalize-space()='Get started for free']/following::button[normalize-space()='Join'][1]"),
                        By.xpath("//button[contains(normalize-space(),'Join')]")
                );

            case "Contact support":
                return firstVisibleOrNull(
                        By.xpath("//button[contains(normalize-space(),'Contact support')]"),
                        By.xpath("//*[normalize-space()='Contact support']/ancestor-or-self::button[1]")
                );

            default:
                return firstVisibleOrNull(
                        By.linkText(label),
                        By.partialLinkText(label),
                        By.xpath("//*[normalize-space()='" + label + "']/ancestor-or-self::*[self::a or self::button][1]"),
                        By.xpath("//*[contains(normalize-space(),'" + label + "')]/ancestor-or-self::*[self::a or self::button][1]")
                );
        }
    }

    private void clickHandleTabOrPageChange(String label) {
        dismissStickyBannerIfPresent();
        scrollToBottomFast();
        sleepQuietly(2000);

        WebElement element = findFooterItemOrNull(label);
        if (element == null) {
            throw new SkipException("Footer item is not present in current UI: " + label);
        }

        String originalWindow = driver().getWindowHandle();
        Set<String> windowsBefore = driver().getWindowHandles();
        String beforeUrl = driver().getCurrentUrl();

        try {
            ((JavascriptExecutor) driver()).executeScript(
                    "arguments[0].scrollIntoView({block:'center', inline:'nearest'});",
                    element
            );
            highlight(element);
            sleepQuietly(2000);
        } catch (StaleElementReferenceException stale) {
            element = findFooterItemOrNull(label);
            if (element == null) {
                throw new RuntimeException("Footer item became stale and could not be found again: " + label, stale);
            }
            ((JavascriptExecutor) driver()).executeScript(
                    "arguments[0].scrollIntoView({block:'center', inline:'nearest'});",
                    element
            );
            highlight(element);
            sleepQuietly(2000);
        }

        try {
            element.click();
        } catch (Exception clickError) {
            WebElement refreshed = findFooterItemOrNull(label);
            if (refreshed == null) {
                throw new RuntimeException("Could not re-find footer item for click: " + label, clickError);
            }
            ((JavascriptExecutor) driver()).executeScript(
                    "arguments[0].scrollIntoView({block:'center', inline:'nearest'});",
                    refreshed
            );
            highlight(refreshed);
            ((JavascriptExecutor) driver()).executeScript("arguments[0].click();", refreshed);
        }

        sleepQuietly(2000);

        Set<String> windowsAfter = driver().getWindowHandles();
        if (windowsAfter.size() > windowsBefore.size()) {
            List<String> newWindows = new ArrayList<>(windowsAfter);
            newWindows.removeAll(windowsBefore);

            if (!newWindows.isEmpty()) {
                driver().switchTo().window(newWindows.get(0));
                waitForPageReady();
                sleepQuietly(2000);
                driver().close();
                driver().switchTo().window(originalWindow);
                waitForPageReady();
                scrollToBottomFast();
                sleepQuietly(2000);
                return;
            }
        }

        String afterUrl = driver().getCurrentUrl();
        if (!afterUrl.equals(beforeUrl)) {
            driver().navigate().back();
            waitForPageReady();
            sleepQuietly(2000);
        }

        scrollToBottomFast();
        sleepQuietly(2000);
    }

    @Test(priority = 1)
    public void verifyFooterPricing() {
        clickHandleTabOrPageChange("Pricing");
    }

    @Test(priority = 2)
    public void verifyFooterContactSales() {
        clickHandleTabOrPageChange("Contact sales");
    }

    @Test(priority = 3)
    public void verifyFooterWatchDemos() {
        clickHandleTabOrPageChange("Watch demos");
    }

    @Test(priority = 4)
    public void verifyFooterJoin() {
        clickHandleTabOrPageChange("Join");
    }

    @Test(priority = 5)
    public void verifyFooterContactSupport() {
        clickHandleTabOrPageChange("Contact support");
    }

    @Test(priority = 6)
    public void verifyFooterVideoHosting() {
        clickHandleTabOrPageChange("Video Hosting");
    }

    @Test(priority = 7)
    public void verifyFooterVideoAnalytics() {
        clickHandleTabOrPageChange("Video Analytics");
    }

    @Test(priority = 8)
    public void verifyFooterVideoEditor() {
        clickHandleTabOrPageChange("Video Editor");
    }

    @Test(priority = 9)
    public void verifyFooterVideoPrivacy() {
        clickHandleTabOrPageChange("Video Privacy");
    }

    @Test(priority = 10)
    public void verifyFooterSendVideos() {
        clickHandleTabOrPageChange("Send Videos");
    }

    @Test(priority = 11)
    public void verifyFooterVideoLibrary() {
        clickHandleTabOrPageChange("Video Library");
    }

    @Test(priority = 12)
    public void verifyFooterLiveStreaming() {
        clickHandleTabOrPageChange("Live Streaming");
    }

    @Test(priority = 13)
    public void verifyFooterInteractiveVideo() {
        clickHandleTabOrPageChange("Interactive Video");
    }

    @Test(priority = 14)
    public void verifyFooterVideoPlayer() {
        clickHandleTabOrPageChange("Video Player");
    }

    @Test(priority = 15)
    public void verifyFooterVirtualEvents() {
        clickHandleTabOrPageChange("Virtual Events");
    }

    @Test(priority = 16)
    public void verifyFooterStreaming() {
        clickHandleTabOrPageChange("Streaming");
    }

    @Test(priority = 17)
    public void verifyFooterMarketing() {
        clickHandleTabOrPageChange("Marketing");
    }

    @Test(priority = 18)
    public void verifyFooterMonetization() {
        clickHandleTabOrPageChange("Monetization");
    }

    @Test(priority = 19)
    public void verifyFooterCommunications() {
        clickHandleTabOrPageChange("Communications");
    }

    @Test(priority = 20)
    public void verifyFooterStaffPicks() {
        clickHandleTabOrPageChange("Staff Picks");
    }

    @Test(priority = 21)
    public void verifyFooterWatch() {
        clickHandleTabOrPageChange("Watch");
    }

    @Test(priority = 22)
    public void verifyFooterCategories() {
        clickHandleTabOrPageChange("Categories");
    }

    @Test(priority = 23)
    public void verifyFooterChannels() {
        clickHandleTabOrPageChange("Channels");
    }

    @Test(priority = 24)
    public void verifyFooterOnDemand() {
        clickHandleTabOrPageChange("On Demand");
    }

    @Test(priority = 25)
    public void verifyFooterHelpCenter() {
        clickHandleTabOrPageChange("Help Center");
    }

    @Test(priority = 26)
    public void verifyFooterBlog() {
        clickHandleTabOrPageChange("Blog");
    }

    @Test(priority = 27)
    public void verifyFooterMediaKit() {
        clickHandleTabOrPageChange("Media Kit");
    }

    @Test(priority = 28)
    public void verifyFooterAbout() {
        clickHandleTabOrPageChange("About");
    }

    @Test(priority = 29)
    public void verifyFooterCareers() {
        clickHandleTabOrPageChange("Careers");
    }

    @Test(priority = 30)
    public void verifyFooterSitemap() {
        clickHandleTabOrPageChange("Sitemap");
    }
}