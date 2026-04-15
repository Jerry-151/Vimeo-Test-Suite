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

public class LoginPageTests extends BaseTest {

    private static final String WATCH_URL = "https://vimeo.com/watch";
    private static final long WAIT_TIMEOUT_MS = Duration.ofSeconds(25).toMillis();
    private static final long ACTION_DELAY_MS = 2000;

    private static final String VALID_EMAIL = "jgmargarito6741@eagle.fgcu.edu";
    private static final String VALID_PASSWORD = "SoftwareTesting5!";

    private static final String INVALID_EMAIL = "invalid@example.com";
    private static final String INVALID_PASSWORD = "invalid-password";

    @BeforeMethod(alwaysRun = true)
    public void openWatchPageAndLoginPopup() {
        openWatchPageWithRetry();

        WebElement loginTrigger = findLoginTriggerOrNull();
        if (loginTrigger == null) {
            refreshPageSafe();
            waitForPageReady();
            pauseForDemo();
            loginTrigger = findLoginTriggerOrNull();
        }

        Assert.assertNotNull(loginTrigger, "Could not locate Log in trigger.");

        pauseForDemo();
        scrollIntoView(loginTrigger);
        highlight(loginTrigger);
        pauseForDemo();

        clickElementRaw(loginTrigger);
        pauseForDemo();

        Assert.assertTrue(
                waitForLoginPopupOpen(),
                "Login popup did not open. Could not locate login fields."
        );

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

        if (findLoginTriggerOrNull() == null) {
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

    private boolean existsVisible(By... locators) {
        return firstVisibleOrNull(locators) != null;
    }

    private WebElement findLoginTriggerOrNull() {
        return firstVisibleOrNull(
                By.xpath("//header//a[normalize-space()='Log in']"),
                By.xpath("//header//button[normalize-space()='Log in']"),
                By.xpath("//a[normalize-space()='Log in']"),
                By.xpath("//button[normalize-space()='Log in']"),
                By.xpath("//*[self::a or self::button][contains(normalize-space(),'Log in')]"),
                By.partialLinkText("Log in")
        );
    }

    private boolean waitForLoginPopupOpen() {
        long end = System.currentTimeMillis() + WAIT_TIMEOUT_MS;

        while (System.currentTimeMillis() < end) {
            try {
                boolean hasEmail = existsVisible(
                        By.cssSelector("input[type='email']"),
                        By.cssSelector("input[name='email']"),
                        By.xpath("//input[@autocomplete='email']"),
                        By.xpath("//input[contains(@placeholder,'Email') or contains(@placeholder,'email')]")
                );

                boolean hasPassword = existsVisible(
                        By.cssSelector("input[type='password']"),
                        By.cssSelector("input[name='password']"),
                        By.xpath("//input[@autocomplete='current-password']"),
                        By.xpath("//input[contains(@placeholder,'Password') or contains(@placeholder,'password')]")
                );

                if (hasEmail && hasPassword) {
                    return true;
                }
            } catch (Exception ignored) {
            }

            sleepQuietly(400);
        }
        return false;
    }

    private WebElement findEmailField() {
        return firstVisible(
                "email field",
                By.cssSelector("input[type='email']"),
                By.cssSelector("input[name='email']"),
                By.xpath("//input[@autocomplete='email']"),
                By.xpath("//input[contains(@placeholder,'Email') or contains(@placeholder,'email')]")
        );
    }

    private WebElement findPasswordField() {
        return firstVisible(
                "password field",
                By.cssSelector("input[type='password']"),
                By.cssSelector("input[name='password']"),
                By.xpath("//input[@autocomplete='current-password']"),
                By.xpath("//input[contains(@placeholder,'Password') or contains(@placeholder,'password')]")
        );
    }

    private WebElement findSubmitButton() {
        return firstVisible(
                "submit button",
                By.xpath("//input[@type='password']/ancestor::form[1]//button[@type='submit']"),
                By.xpath("//input[@type='password']/ancestor::form[1]//*[self::button][contains(normalize-space(),'Log in')]"),
                By.xpath("//form[.//input[@type='password']]//button[@type='submit']"),
                By.xpath("//form[.//input[@type='password']]//*[self::button][contains(normalize-space(),'Log in')]")
        );
    }

    private WebElement findCredentialSubmitButton() {
        return firstVisible(
                "credential submit button",
                By.xpath("//input[@type='password']/ancestor::form[1]//button[@type='submit']"),
                By.xpath("//form[.//input[@type='password']]//button[@type='submit']"),
                By.xpath("//input[@type='password']/ancestor::form[1]//*[self::button][contains(normalize-space(),'Log in')]")
        );
    }

    private WebElement findFacebookButton() {
        return firstVisible(
                "Facebook button",
                By.xpath("//*[self::button or self::a][contains(translate(normalize-space(),'FACEBOOK','facebook'),'facebook')]")
        );
    }

    private WebElement findGoogleButton() {
        return firstVisible(
                "Google button",
                By.xpath("//*[self::button or self::a][contains(translate(normalize-space(),'GOOGLE','google'),'google')]")
        );
    }

    private WebElement findAppleButton() {
        return firstVisible(
                "Apple button",
                By.xpath("//*[self::button or self::a][contains(translate(normalize-space(),'APPLE','apple'),'apple')]")
        );
    }

    private WebElement findForgotPasswordLink() {
        return firstVisible(
                "Forgot password link",
                By.linkText("Forgot your password?"),
                By.partialLinkText("Forgot your password"),
                By.xpath("//*[self::a or self::button][contains(normalize-space(),'Forgot')]")
        );
    }

    private WebElement findJoinVimeoLink() {
        return firstVisible(
                "Join Vimeo link",
                By.linkText("Join Vimeo"),
                By.partialLinkText("Join Vimeo"),
                By.xpath("//*[self::a or self::button][contains(normalize-space(),'Join Vimeo')]")
        );
    }

    private WebElement findCloseButton() {
        return firstVisible(
                "close button",
                By.xpath("//div[@role='dialog']//button[@aria-label='Close']"),
                By.xpath("//button[@aria-label='Close']"),
                By.xpath("//button[contains(@aria-label,'Close')]"),
                By.xpath("//div[@role='dialog']//button[contains(@aria-label,'close') or contains(@aria-label,'Close')]")
        );
    }

    private boolean isLoginPopupOpen() {
        return existsVisible(
                By.cssSelector("input[type='email']"),
                By.cssSelector("input[type='password']"),
                By.xpath("//button[@type='submit']")
        );
    }

    private boolean isLoginPopupStillVisible() {
        return existsVisible(
                By.cssSelector("input[type='email']"),
                By.cssSelector("input[type='password']"),
                By.xpath("//div[@role='dialog']"),
                By.xpath("//button[@aria-label='Close']")
        );
    }

    private void typeInto(WebElement element, String value, String name) {
        if (element == null) {
            throw new SkipException("Element not available for typing: " + name);
        }

        pauseForDemo();
        scrollIntoView(element);
        highlight(element);
        pauseForDemo();

        try {
            element.clear();
            pauseForDemo();
            element.sendKeys(value);
        } catch (Exception e) {
            throw new RuntimeException("Could not type into: " + name, e);
        }

        pauseForDemo();
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

    private void handleAfterClick(String originalWindow, Set<String> windowsBefore, String beforeUrl) {
        pauseForDemo();

        Set<String> windowsAfter = driver().getWindowHandles();

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

        try {
            String afterUrl = safeCurrentUrl();
            if (!afterUrl.isBlank() && !afterUrl.equals(beforeUrl)) {
                pauseForDemo();
                driver().navigate().back();
                waitForPageReady();
                pauseForDemo();

                if (!isLoginPopupOpen()) {
                    WebElement loginTrigger = findLoginTriggerOrNull();
                    if (loginTrigger != null) {
                        clickElement(loginTrigger, "Log in trigger");
                        Assert.assertTrue(waitForLoginPopupOpen(), "Login popup did not reopen after navigating back.");
                    }
                }
                return;
            }
        } catch (Exception ignored) {
        }

        pauseForDemo();
    }

    @Test(priority = 1, description = "Verify login popup appears.")
    public void verifyLoginPopupAppears() {
        Assert.assertTrue(isLoginPopupOpen(), "Login popup is not open.");
        pauseForDemo();
    }

    @Test(priority = 2, description = "Click Facebook login.")
    public void verifyFacebookLoginButton() {
        String originalWindow = driver().getWindowHandle();
        Set<String> windowsBefore = driver().getWindowHandles();
        String beforeUrl = safeCurrentUrl();

        clickElement(findFacebookButton(), "Facebook login button");
        handleAfterClick(originalWindow, windowsBefore, beforeUrl);
    }

    @Test(priority = 3, description = "Click Google login.")
    public void verifyGoogleLoginButton() {
        String originalWindow = driver().getWindowHandle();
        Set<String> windowsBefore = driver().getWindowHandles();
        String beforeUrl = safeCurrentUrl();

        clickElement(findGoogleButton(), "Google login button");
        handleAfterClick(originalWindow, windowsBefore, beforeUrl);
    }

    @Test(priority = 4, description = "Click Apple login.")
    public void verifyAppleLoginButton() {
        String originalWindow = driver().getWindowHandle();
        Set<String> windowsBefore = driver().getWindowHandles();
        String beforeUrl = safeCurrentUrl();

        clickElement(findAppleButton(), "Apple login button");
        handleAfterClick(originalWindow, windowsBefore, beforeUrl);
    }

    @Test(priority = 5, description = "Verify forgot password link.")
    public void verifyForgotPasswordLink() {
        String originalWindow = driver().getWindowHandle();
        Set<String> windowsBefore = driver().getWindowHandles();
        String beforeUrl = safeCurrentUrl();

        clickElement(findForgotPasswordLink(), "Forgot password link");
        handleAfterClick(originalWindow, windowsBefore, beforeUrl);
    }

    @Test(priority = 6, description = "Verify Join Vimeo link.")
    public void verifyJoinVimeoLink() {
        String originalWindow = driver().getWindowHandle();
        Set<String> windowsBefore = driver().getWindowHandles();
        String beforeUrl = safeCurrentUrl();

        clickElement(findJoinVimeoLink(), "Join Vimeo link");
        handleAfterClick(originalWindow, windowsBefore, beforeUrl);
    }

    @Test(priority = 7, description = "Close login popup.")
    public void verifyCloseLoginPopup() {
        clickElement(findCloseButton(), "close button");
        pauseForDemo();
        Assert.assertFalse(isLoginPopupStillVisible(), "Login popup still appears to be visible.");
        pauseForDemo();
    }

    @Test(priority = 8, description = "Submit invalid email and password.")
    public void verifyInvalidEmailPasswordSubmit() {
        typeInto(findEmailField(), INVALID_EMAIL, "email field");
        typeInto(findPasswordField(), INVALID_PASSWORD, "password field");

        WebElement submitButton = findCredentialSubmitButton();
        pauseForDemo();
        scrollIntoView(submitButton);
        highlight(submitButton);
        pauseForDemo();
        clickElementRaw(submitButton);
        pauseForDemo();
    }

    @Test(priority = 9, description = "Submit valid email and password.")
    public void verifyValidEmailPasswordSubmit() {
        typeInto(findEmailField(), VALID_EMAIL, "email field");
        typeInto(findPasswordField(), VALID_PASSWORD, "password field");

        WebElement submitButton = findCredentialSubmitButton();
        pauseForDemo();
        scrollIntoView(submitButton);
        highlight(submitButton);
        pauseForDemo();
        clickElementRaw(submitButton);
        pauseForDemo();
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
}