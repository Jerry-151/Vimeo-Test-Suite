package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LoginPageTests extends BaseTest {

    private static final String WATCH_URL = "https://vimeo.com/watch";
    private static final long WAIT_TIMEOUT_MS = Duration.ofSeconds(12).toMillis();

    // Replace with real credentials before running the valid-login test.
    private static final String VALID_EMAIL = "jgmargarito6741@eagle.fgcu.edu";
    private static final String VALID_PASSWORD = "SoftwareTesting5!";

    private static final String INVALID_EMAIL = "invalid@example.com";
    private static final String INVALID_PASSWORD = "invalid-password";

    @BeforeMethod(alwaysRun = true)
    public void openWatchPageAndLoginPopup() {
        driver().get(WATCH_URL);
        waitForPageReady();

        WebElement loginTrigger = findLoginTrigger();
        Assert.assertNotNull(loginTrigger, "Could not locate watch-page Log in trigger.");

        scrollIntoView(loginTrigger);
        highlight(loginTrigger);
        demoPause();

        try {
            loginTrigger.click();
        } catch (Exception e) {
            jsClick(loginTrigger);
        }

        demoPause();

        Assert.assertTrue(
                isLoginPopupOpen(),
                "Login popup did not open. Could not locate email field, password field, or submit button."
        );
    }

    private void waitForPageReady() {
        long end = System.currentTimeMillis() + WAIT_TIMEOUT_MS;

        while (System.currentTimeMillis() < end) {
            try {
                Object state = ((org.openqa.selenium.JavascriptExecutor) driver())
                        .executeScript("return document.readyState");
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

    private void jsClick(WebElement element) {
        ((org.openqa.selenium.JavascriptExecutor) driver()).executeScript("arguments[0].click();", element);
    }

    private WebElement firstVisible(String elementName, By... locators) {
        long end = System.currentTimeMillis() + WAIT_TIMEOUT_MS;
        Throwable lastError = null;

        while (System.currentTimeMillis() < end) {
            for (By locator : locators) {
                try {
                    List<WebElement> matches = driver().findElements(locator);
                    for (WebElement match : matches) {
                        if (match.isDisplayed()) {
                            return match;
                        }
                    }
                } catch (Exception e) {
                    lastError = e;
                }
            }
            sleepQuietly(250);
        }

        if (lastError != null) {
            Assert.fail("Could not locate visible element: " + elementName + ". Last error: " + lastError.getMessage(), lastError);
        } else {
            Assert.fail("Could not locate visible element: " + elementName);
        }
        return null;
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

    private WebElement findLoginTrigger() {
        return firstVisible(
                "watch-page Log in trigger",
                By.xpath("//header//*[self::a or self::button][normalize-space()='Log in']"),
                By.xpath("(//*[self::a or self::button][normalize-space()='Log in'])[last()]"),
                By.partialLinkText("Log in"),
                By.xpath("//*[contains(@href,'log_in') and normalize-space()='Log in']"),
                By.xpath("//*[contains(normalize-space(),'Log in') and (self::a or self::button)]")
        );
    }

    private WebElement findEmailField() {
        return firstVisible(
                "email field",
                By.cssSelector("input[type='email']"),
                By.cssSelector("input[name='email']"),
                By.xpath("//input[contains(@placeholder,'email')]"),
                By.xpath("//input[contains(@placeholder,'Email')]")
        );
    }

    private WebElement findPasswordField() {
        return firstVisible(
                "password field",
                By.cssSelector("input[type='password']"),
                By.cssSelector("input[name='password']"),
                By.xpath("//input[contains(@placeholder,'password')]"),
                By.xpath("//input[contains(@placeholder,'Password')]")
        );
    }

    private WebElement findSubmitButton() {
        return firstVisible(
                "submit button",
                By.xpath("//button[contains(normalize-space(),'Log in with an email')]"),
                By.xpath("//button[@type='submit']"),
                By.xpath("//*[self::button][contains(normalize-space(),'Log in')]")
        );
    }

    private WebElement findFacebookButton() {
        return firstVisible(
                "Facebook button",
                By.xpath("//button[contains(normalize-space(),'Facebook')]"),
                By.xpath("//*[self::button or self::a][contains(normalize-space(),'Facebook')]")
        );
    }

    private WebElement findGoogleButton() {
        return firstVisible(
                "Google button",
                By.xpath("//button[contains(normalize-space(),'Google')]"),
                By.xpath("//*[self::button or self::a][contains(normalize-space(),'Google')]")
        );
    }

    private WebElement findAppleButton() {
        return firstVisible(
                "Apple button",
                By.xpath("//button[contains(normalize-space(),'Apple')]"),
                By.xpath("//*[self::button or self::a][contains(normalize-space(),'Apple')]")
        );
    }

    private WebElement findForgotPasswordLink() {
        return firstVisible(
                "Forgot password link",
                By.linkText("Forgot your password?"),
                By.partialLinkText("Forgot your password"),
                By.xpath("//*[self::a or self::button][contains(normalize-space(),'Forgot your password')]")
        );
    }

    private WebElement findJoinVimeoLink() {
        return firstVisible(
                "Join Vimeo link",
                By.linkText("Join Vimeo"),
                By.partialLinkText("Join Vimeo"),
                By.xpath("//*[self::a or self::button][normalize-space()='Join Vimeo']"),
                By.xpath("//*[contains(normalize-space(),'Join Vimeo')]")
        );
    }

    private WebElement findCloseButton() {
        return firstVisible(
                "close button",
                By.xpath("//button[@aria-label='Close']"),
                By.xpath("//button[contains(@aria-label,'close') or contains(@aria-label,'Close')]"),
                By.xpath("(//button[.//*[name()='svg']])[last()]")
        );
    }

    private boolean isLoginPopupOpen() {
        return firstVisibleOrNull(
                By.cssSelector("input[type='email']"),
                By.cssSelector("input[type='password']"),
                By.xpath("//button[contains(normalize-space(),'Log in with an email')]"),
                By.xpath("//*[contains(normalize-space(),'Forgot your password')]")
        ) != null;
    }

    private void typeInto(WebElement element, String value, String name) {
        scrollIntoView(element);
        highlight(element);
        demoPause();
        element.clear();
        element.sendKeys(value);
        demoPause();
    }

    private void clickElement(WebElement element, String name) {
        scrollIntoView(element);
        highlight(element);
        demoPause();
        try {
            element.click();
        } catch (Exception e) {
            jsClick(element);
        }
        demoPause();
    }

    private void closeNewTabIfOpened(String originalWindow, Set<String> windowsBefore) {
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
            }
        }
    }

    @Test(priority = 1, description = "Verify login popup appears.")
    public void verifyLoginPopupAppears() {
        Assert.assertTrue(isLoginPopupOpen(), "Login popup is not open.");
    }

    @Test(priority = 2, description = "Click Facebook login.")
    public void verifyFacebookLoginButton() {
        String originalWindow = driver().getWindowHandle();
        Set<String> windowsBefore = driver().getWindowHandles();

        clickElement(findFacebookButton(), "Facebook login button");
        closeNewTabIfOpened(originalWindow, windowsBefore);
    }

    @Test(priority = 3, description = "Click Google login.")
    public void verifyGoogleLoginButton() {
        String originalWindow = driver().getWindowHandle();
        Set<String> windowsBefore = driver().getWindowHandles();

        clickElement(findGoogleButton(), "Google login button");
        closeNewTabIfOpened(originalWindow, windowsBefore);
    }

    @Test(priority = 4, description = "Click Apple login.")
    public void verifyAppleLoginButton() {
        String originalWindow = driver().getWindowHandle();
        Set<String> windowsBefore = driver().getWindowHandles();

        clickElement(findAppleButton(), "Apple login button");
        closeNewTabIfOpened(originalWindow, windowsBefore);
    }

    @Test(priority = 5, description = "Submit invalid email and password.")
    public void verifyInvalidEmailPasswordSubmit() {
        typeInto(findEmailField(), INVALID_EMAIL, "email field");
        typeInto(findPasswordField(), INVALID_PASSWORD, "password field");
        clickElement(findSubmitButton(), "submit button");
    }

    @Test(priority = 6, description = "Submit empty login form.")
    public void verifyEmptySubmitStaysInPopup() {
        clickElement(findSubmitButton(), "submit button");
        Assert.assertTrue(isLoginPopupOpen(), "Login popup should still be open after empty submit.");
    }

    @Test(priority = 7, description = "Click forgot password.")
    public void verifyForgotPasswordLink() {
        String beforeUrl = driver().getCurrentUrl();
        clickElement(findForgotPasswordLink(), "Forgot password link");
        waitForPageReady();
        demoPause();

        if (!driver().getCurrentUrl().equals(beforeUrl)) {
            driver().navigate().back();
            waitForPageReady();
            demoPause();
        }
    }

    @Test(priority = 8, description = "Click Join Vimeo.")
    public void verifyJoinVimeoLink() {
        String beforeUrl = driver().getCurrentUrl();
        clickElement(findJoinVimeoLink(), "Join Vimeo link");
        waitForPageReady();
        demoPause();

        if (!driver().getCurrentUrl().equals(beforeUrl)) {
            driver().navigate().back();
            waitForPageReady();
            demoPause();
        }
    }

    @Test(priority = 9, description = "Click close on the login popup.")
    public void verifyCloseLoginPopup() {
        clickElement(findCloseButton(), "close button");
        Assert.assertFalse(isLoginPopupOpen(), "Login popup should be closed.");
    }

    @Test(priority = 10, description = "Submit valid email and password.")
    public void verifyValidEmailPasswordSubmit() {
        typeInto(findEmailField(), VALID_EMAIL, "email field");
        typeInto(findPasswordField(), VALID_PASSWORD, "password field");
        clickElement(findSubmitButton(), "submit button");
    }
}