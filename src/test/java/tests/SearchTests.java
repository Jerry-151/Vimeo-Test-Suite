package tests;

import base.BaseTest;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SearchTests extends BaseTest {

    @Test
    public void testSearchBoxVisible() {
        WebElement search = driver.findElement(By.tagName("input"));
        highlight(search);
        sleep(1500);
        Assert.assertTrue(search.isDisplayed());
    }

    @Test
    public void testSearchTyping() {
        WebElement search = driver.findElement(By.tagName("input"));
        highlight(search);
        search.sendKeys("nature");
        sleep(2000);
        Assert.assertTrue(search.getAttribute("value").contains("nature"));
    }

    @Test
    public void testSearchClear() {
        WebElement search = driver.findElement(By.tagName("input"));
        search.sendKeys("test");
        sleep(1500);
        search.clear();
        Assert.assertEquals(search.getAttribute("value"), "");
    }

    @Test
    public void testSearchPlaceholder() {
        WebElement search = driver.findElement(By.tagName("input"));
        highlight(search);
        String placeholder = search.getAttribute("placeholder");
        sleep(1500);
        Assert.assertNotNull(placeholder);
    }

    @Test
    public void testSearchBoxEnabled() {
        WebElement search = driver.findElement(By.tagName("input"));
        sleep(1500);
        Assert.assertTrue(search.isEnabled());
    }
}