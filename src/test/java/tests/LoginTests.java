package tests;

import base.BaseTest;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {

    @Test
    public void testLoginLinkExists() {
        WebElement login = driver.findElement(By.linkText("Log in"));
        highlight(login);
        Assert.assertTrue(login.isDisplayed());
    }

    @Test
    public void testNavigateToLoginPage() {
        driver.findElement(By.linkText("Log in")).click();
        sleep(2000);
        Assert.assertTrue(driver.getCurrentUrl().contains("login"));
    }

    @Test
    public void testLoginPageLoads() {
        driver.findElement(By.linkText("Log in")).click();
        sleep(2000);
        Assert.assertTrue(driver.getTitle().length() > 0);
    }

    @Test
    public void testEmailFieldExists() {
        driver.findElement(By.linkText("Log in")).click();
        sleep(2000);
        Assert.assertTrue(driver.findElements(By.tagName("input")).size() > 0);
    }

    @Test
    public void testLoginPageURL() {
        driver.findElement(By.linkText("Log in")).click();
        sleep(2000);
        Assert.assertTrue(driver.getCurrentUrl().contains("login"));
    }
}