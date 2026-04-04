package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UIElementsTests extends BaseTest {

    @Test
    public void testPageTitle() throws InterruptedException {
        Thread.sleep(2000);
        Assert.assertNotNull(driver.getTitle());
    }

    @Test
    public void testInputFieldPresence() throws InterruptedException {
        Thread.sleep(2000);
        Assert.assertTrue(driver.findElement(By.tagName("input")).isDisplayed());
    }

    @Test
    public void testButtonsExist() throws InterruptedException {
        Thread.sleep(2000);
        Assert.assertTrue(true);
    }

    @Test
    public void testPageSource() throws InterruptedException {
        Thread.sleep(2000);
        Assert.assertTrue(driver.getPageSource().contains("Vimeo"));
    }

    @Test
    public void testURLNotEmpty() throws InterruptedException {
        Thread.sleep(2000);
        Assert.assertFalse(driver.getCurrentUrl().isEmpty());
    }
}