package tests;

import base.BaseTest;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UIElementsTests extends BaseTest {

    @Test
    public void testPageTitleExists() {
        sleep(1500);
        Assert.assertNotNull(driver.getTitle());
    }

    @Test
    public void testInputFieldExists() {
        WebElement input = driver.findElement(By.tagName("input"));
        highlight(input);
        Assert.assertTrue(input.isDisplayed());
    }

    @Test
    public void testButtonsExist() {
        Assert.assertTrue(driver.findElements(By.tagName("button")).size() > 0);
    }

    @Test
    public void testPageSourceContainsVimeo() {
        Assert.assertTrue(driver.getPageSource().contains("Vimeo"));
    }

    @Test
    public void testURLNotEmpty() {
        Assert.assertFalse(driver.getCurrentUrl().isEmpty());
    }
}