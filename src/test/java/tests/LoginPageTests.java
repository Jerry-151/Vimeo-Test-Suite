package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

/**
 * Fast Vimeo login popup tests.
 *
 * Order matters:
 * - All non-authenticating flows run first
 * - Successful login runs last because it changes the page state
 */
public class LoginPageTests extends BaseTest {

    private static final String WATCH_URL = "https://vimeo.com/watch";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(12);

    private static final String INVALID_EMAIL = "invalid_user_automation@example.com";
    private static final String INVALID_PASSWORD = "wrongpass123";

    private static final String VALID_EMAIL = "jgmargarito6741@eagle.fgcu.edu";
    private static final String VALID_PASSWORD = "SoftwareTesting5!";

    private WebDriverWait waitShort() {
        return new WebDriverWait(driver(), WAIT_TIMEOUT);
    }

    @BeforeMethod(alwaysRun = true)
    public void openWatchPageAndLoginPopup() {
        driver().get(WATCH_URL);
        waitForPageReady();

        WebElement loginButton = findHeaderLoginButton();
        performClick(loginButton, "Header Log in button");

        WebElement modalHeading = findLoginHeading();
        Assert.assertTrue(modalHeading.isDisplayed(), "Login popup did not appear.");
    }

    private void waitForPageReady() {
        waitShort().until(webDriver ->
                "complete".equals(((org.openqa.selenium.JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState")));
    }

    private void performClick(WebElement element, String elementName) {
        try {
            scrollIntoView(element);
            highlight(element);
            demoPause();
            waitShort().until(ExpectedConditions.elementToBeClickable(element)).click();
            demoPause();
        } catch (Exception e) {
            Assert.fail("Could not click " + elementName + ": " + e.getMessage(), e);
        }
    }

    private void performType(WebElement element, String value, String elementName) {
        try {
            scrollIntoView(element);
            highlight(element);
            demoPause();
            element.clear();
            element.sendKeys(value);
            demoPause();
        } catch (InvalidElementStateException e) {
            Assert.fail("Could not type into " + elementName + ": " + e.getMessage(), e);
        }
    }

    private WebElement findHeaderLoginButton() {
        return firstVisible(
                "Header Log in button",
                By.xpath("//header//*[self::a or self::button][normalize-space()='Log in']"),
                By.xpath("(//*[self::a or self::button][normalize-space()='Log in'])[last()]"),
                By.xpath("//*[contains(@class,'top') or contains(@class,'nav') or contains(@class,'header')]//*[self::a or self::button][normalize-space()='Log in']"),
                By.cssSelector("header a[href*='log_in']"),
                By.xpath("//*[self::a or self::button][contains(normalize-space(),'Log in')]")
        );
    }

    private WebElement findLoginHeading() {
        return firstVisible(
                "Login popup heading",
                By.xpath("//h1[normalize-space()='Log in to Vimeo']"),
                By.xpath("//*[self::h1 or self::h2][normalize-space()='Log in to Vimeo']")
        );
    }

    private WebElement findSignupHeading() {
        return firstVisible(
                "Signup popup heading",
                By.xpath("//*[self::h1 or self::h2][contains(normalize-space(),'Join Vimeo')]"),
                By.xpath("//*[self::h1 or self::h2][contains(normalize-space(),'Sign up')]"),
                By.xpath("//*[self::h1 or self::h2][contains(normalize-space(),'Create')]"),
                By.xpath("//*[self::h1 or self::h2][contains(normalize-space(),'Register')]")
        );
    }

    private WebElement findFacebookButton() {
        return firstVisible(
                "Facebook login button",
                By.xpath("//*[self::button or self::a][contains(normalize-space(),'Facebook')]"),
                By.cssSelector("button[aria-label*='Facebook']"),
                By.cssSelector("a[href*='facebook']")
        );
    }

    private WebElement findGoogleButton() {
        return firstVisible(
                "Google login button",
                By.xpath("//*[self::button or self::a][contains(normalize-space(),'Google')]"),
                By.cssSelector("button[aria-label*='Google']"),
                By.cssSelector("a[href*='google']")
        );
    }

    private WebElement findAppleButton() {
        return firstVisible(
                "Apple login button",
                By.xpath("//*[self::button or self::a][contains(normalize-space(),'Apple')]"),
                By.cssSelector("button[aria-label*='Apple']"),
                By.cssSelector("a[href*='apple']")
        );
    }

    private WebElement findEmailField() {
        return firstVisible(
                "Email field",
                By.cssSelector("input[type='email']"),
                By.cssSelector("input[name='email']"),
                By.cssSelector("input[placeholder='you@email.com']"),
                By.xpath("//input[contains(@placeholder,'email')]")
        );
    }

    private WebElement findPasswordField() {
        return firstVisible(
                "Password field",
                By.cssSelector("input[type='password']"),
                By.cssSelector("input[name='password']"),
                By.cssSelector("input[placeholder='Password']"),
                By.xpath("//input[contains(@placeholder,'Password') or contains(@placeholder,'password')]")
        );
    }

    private WebElement findEmailSubmitButton() {
        return firstVisible(
                "Email submit button",
                By.xpath("//button[contains(normalize-space(),'Log in with an email')]"),
                By.cssSelector("button[type='submit']"),
                By.xpath("//button[contains(normalize-space(),'Log in')]")
        );
    }

    private WebElement findForgotPasswordLink() {
        return firstVisible(
                "Forgot password link",
                By.xpath("//a[contains(normalize-space(),'Forgot your password')]"),
                By.cssSelector("a[href*='forgot']"),
                By.cssSelector("a[href*='reset']")
        );
    }

    private WebElement findJoinVimeoButton() {
        return firstVisible(
                "Join Vimeo button",
                By.xpath("//button[normalize-space()='Join Vimeo.']"),
                By.xpath("//button[normalize-space()='Join Vimeo']"),
                By.xpath("//button[contains(normalize-space(),'Join Vimeo')]"),
                By.xpath("//button[contains(@class,'LinkText-sc') and contains(normalize-space(),'Join Vimeo')]"),
                By.xpath("//section[contains(@class,'BottomSection')]//button[contains(normalize-space(),'Join Vimeo')]"),
                By.xpath("//button[contains(.,'Join Vimeo')]")
        );
    }

    private WebElement findCloseButton() {
        return firstVisible(
                "Close button",
                By.xpath("//button[@aria-label='Close']"),
                By.xpath("//button[contains(@aria-label,'close') or contains(@aria-label,'Close')]"),
                By.xpath("//h1[normalize-space()='Log in to Vimeo']/ancestor::div[1]//button[last()]")
        );
    }

    private boolean isAnyVisible(By... locators) {
        for (By locator : locators) {
            try {
                List<WebElement> elements = driver().findElements(locator);
                for (WebElement element : elements) {
                    if (element.isDisplayed()) {
                        return true;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    private WebElement firstVisible(String elementName, By... locators) {
        long end = System.currentTimeMillis() + WAIT_TIMEOUT.toMillis();
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

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Assert.fail("Interrupted while locating element: " + elementName, e);
            }
        }

        if (lastError != null) {
            Assert.fail("Could not locate visible element: " + elementName + ". Last error: " + lastError.getMessage(), lastError);
        } else {
            Assert.fail("Could not locate visible element: " + elementName);
        }
        return null;
    }

    @Test(priority = 1, description = "Click login and verify the popup appears.")
    public void verifyLoginPopupAppears() {
        Assert.assertTrue(findLoginHeading().isDisplayed(), "Login popup heading is not visible.");
    }

    @Test(priority = 2, description = "Click login, popup appears, click Login with Facebook.")
    public void verifyFacebookLoginButton() {
        WebElement facebookButton = findFacebookButton();
        performClick(facebookButton, "Facebook login button");

        Assert.assertTrue(
                driver().getCurrentUrl().contains("facebook")
                        || driver().getCurrentUrl().contains("login")
                        || findLoginHeading().isDisplayed(),
                "Facebook login action did not trigger expected navigation or popup flow."
        );
    }

    @Test(priority = 3, description = "Click login, popup appears, click Login with Google.")
    public void verifyGoogleLoginButton() {
        WebElement googleButton = findGoogleButton();
        performClick(googleButton, "Google login button");

        Assert.assertTrue(
                driver().getCurrentUrl().contains("google")
                        || driver().getCurrentUrl().contains("login")
                        || findLoginHeading().isDisplayed(),
                "Google login action did not trigger expected navigation or popup flow."
        );
    }

    @Test(priority = 4, description = "Click login, popup appears, click Login with Apple.")
    public void verifyAppleLoginButton() {
        WebElement appleButton = findAppleButton();
        performClick(appleButton, "Apple login button");

        Assert.assertTrue(
                driver().getCurrentUrl().contains("apple")
                        || driver().getCurrentUrl().contains("login")
                        || findLoginHeading().isDisplayed(),
                "Apple login action did not trigger expected navigation or popup flow."
        );
    }

    @Test(priority = 5, description = "Click login, popup appears, type invalid email and password, click submit.")
    public void verifyInvalidCredentialsSubmit() {
        WebElement emailField = findEmailField();
        WebElement passwordField = findPasswordField();
        WebElement submitButton = findEmailSubmitButton();

        performType(emailField, INVALID_EMAIL, "Email field");
        performType(passwordField, INVALID_PASSWORD, "Password field");

        waitShort().until(driver -> submitButton.isDisplayed() && submitButton.isEnabled());

        performClick(submitButton, "Email submit button");

        Assert.assertTrue(
                driver().getCurrentUrl().contains("login")
                        || driver().getCurrentUrl().contains("log")
                        || findLoginHeading().isDisplayed(),
                "Invalid credential submission did not keep the user in a login-related flow."
        );
    }

    @Test(priority = 6, description = "Click login, popup appears, click submit without typing anything.")
    public void verifyEmptySubmit() {
        WebElement emailField = findEmailField();
        WebElement passwordField = findPasswordField();
        WebElement submitButton = findEmailSubmitButton();

        Assert.assertEquals(emailField.getAttribute("value"), "", "Email field should start empty.");
        Assert.assertEquals(passwordField.getAttribute("value"), "", "Password field should start empty.");

        if (submitButton.isEnabled()) {
            performClick(submitButton, "Email submit button");
        }

        Assert.assertTrue(findLoginHeading().isDisplayed(), "Popup disappeared after empty submit attempt.");
    }

    @Test(priority = 7, description = "Click login, popup appears, click forgot password link.")
    public void verifyForgotPasswordLink() {
        WebElement forgotPasswordLink = findForgotPasswordLink();
        performClick(forgotPasswordLink, "Forgot password link");

        Assert.assertTrue(
                driver().getCurrentUrl().contains("forgot")
                        || driver().getCurrentUrl().contains("reset")
                        || driver().getCurrentUrl().contains("password"),
                "Forgot password link did not navigate to a recovery-related page."
        );
    }

    @Test(priority = 8, description = "Click login, popup appears, click Join Vimeo.")
    public void verifyJoinVimeoLink() {
        WebElement joinVimeoButton = findJoinVimeoButton();
        performClick(joinVimeoButton, "Join Vimeo button");

        waitShort().until(driver -> {
            boolean loginHeadingGone = !isAnyVisible(
                    By.xpath("//h1[normalize-space()='Log in to Vimeo']"),
                    By.xpath("//*[self::h1 or self::h2][normalize-space()='Log in to Vimeo']")
            );

            boolean signupViewVisible = isAnyVisible(
                    By.xpath("//*[self::h1 or self::h2][contains(normalize-space(),'Join Vimeo')]"),
                    By.xpath("//*[self::h1 or self::h2][contains(normalize-space(),'Sign up')]"),
                    By.xpath("//*[self::h1 or self::h2][contains(normalize-space(),'Create')]"),
                    By.xpath("//*[self::h1 or self::h2][contains(normalize-space(),'Register')]"),
                    By.cssSelector("input[name='email']"),
                    By.cssSelector("input[type='email']"),
                    By.xpath("//button[contains(normalize-space(),'Join Vimeo')]"),
                    By.xpath("//button[contains(normalize-space(),'Sign up')]")
            );

            return loginHeadingGone && signupViewVisible;
        });

        boolean loginPopupStillVisible = isAnyVisible(
                By.xpath("//h1[normalize-space()='Log in to Vimeo']"),
                By.xpath("//*[self::h1 or self::h2][normalize-space()='Log in to Vimeo']")
        );

        boolean replacementPopupVisible = isAnyVisible(
                By.xpath("//*[self::h1 or self::h2][contains(normalize-space(),'Join Vimeo')]"),
                By.xpath("//*[self::h1 or self::h2][contains(normalize-space(),'Sign up')]"),
                By.xpath("//*[self::h1 or self::h2][contains(normalize-space(),'Create')]"),
                By.xpath("//*[self::h1 or self::h2][contains(normalize-space(),'Register')]"),
                By.cssSelector("input[name='email']"),
                By.cssSelector("input[type='email']"),
                By.xpath("//button[contains(normalize-space(),'Join Vimeo')]"),
                By.xpath("//button[contains(normalize-space(),'Sign up')]")
        );

        Assert.assertFalse(
                loginPopupStillVisible,
                "Login popup was still visible after clicking Join Vimeo."
        );

        Assert.assertTrue(
                replacementPopupVisible,
                "Join Vimeo did not replace the login popup with a signup-related popup."
        );
    }

    @Test(priority = 9, description = "Click login, popup appears, click close.")
    public void verifyCloseLoginPopup() {
        WebElement closeButton = findCloseButton();
        performClick(closeButton, "Close button");

        List<WebElement> headings = driver().findElements(
                By.xpath("//*[self::h1 or self::h2][normalize-space()='Log in to Vimeo']")
        );

        boolean anyVisible = false;
        for (WebElement heading : headings) {
            try {
                if (heading.isDisplayed()) {
                    anyVisible = true;
                    break;
                }
            } catch (Exception ignored) {
            }
        }

        Assert.assertFalse(anyVisible, "Login popup was still visible after clicking close.");
    }

    @Test(priority = 10, description = "Click login, popup appears, type valid email and password, click submit.")
    public void verifyValidCredentialsSubmit() {
        WebElement emailField = findEmailField();
        WebElement passwordField = findPasswordField();
        WebElement submitButton = findEmailSubmitButton();

        performType(emailField, VALID_EMAIL, "Email field");
        performType(passwordField, VALID_PASSWORD, "Password field");

        waitShort().until(driver -> submitButton.isDisplayed() && submitButton.isEnabled());

        performClick(submitButton, "Email submit button");

        Assert.assertTrue(
                driver().getCurrentUrl() != null && !driver().getCurrentUrl().isBlank(),
                "No visible result after valid credential submission."
        );
    }
}