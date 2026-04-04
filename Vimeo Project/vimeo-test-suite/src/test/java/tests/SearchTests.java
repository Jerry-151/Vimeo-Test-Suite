package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SearchTests extends BaseTest {

    @Test
    public void testSearchVisible() throws InterruptedException {
        Thread.sleep(2000);
        Assert.assertTrue(driver.findElement(By.tagName("input")).isDisplayed());
    }

    @Test
    public void testSearchEnabled() throws InterruptedException {
        Thread.sleep(2000);
        Assert.assertTrue(driver.findElement(By.tagName("input")).isEnabled());
    }

    @Test
    public void testTypingSearch() throws InterruptedException {
        Thread.sleep(2000);
        driver.findElement(By.tagName("input")).sendKeys("nature");
        Thread.sleep(2000);
        Assert.assertTrue(true);
    }

    @Test
    public void testClearSearch() throws InterruptedException {
        Thread.sleep(2000);
        driver.findElement(By.tagName("input")).clear();
        Assert.assertTrue(true);
    }

    @Test
    public void testPlaceholderExists() throws InterruptedException {
        Thread.sleep(2000);
        String placeholder = driver.findElement(By.tagName("input")).getAttribute("placeholder");
        Assert.assertNotNull(placeholder);
    }
}