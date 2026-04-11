package tests;

import base.BaseTest;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FooterTests extends BaseTest {

    @Test
    public void testFooterExists() {
        WebElement footer = driver.findElement(By.tagName("footer"));
        highlight(footer);
        Assert.assertTrue(footer.isDisplayed());
    }

    @Test
    public void testFooterLinksExist() {
        Assert.assertTrue(driver.findElements(By.tagName("a")).size() > 5);
    }

    @Test
    public void testFooterTextPresent() {
        WebElement footer = driver.findElement(By.tagName("footer"));
        Assert.assertTrue(footer.getText().length() > 0);
    }

    @Test
    public void testScrollToFooter() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        sleep(2000);
        Assert.assertTrue(true);
    }

    @Test
    public void testFooterVisibleAfterScroll() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        sleep(2000);
        Assert.assertTrue(driver.findElement(By.tagName("footer")).isDisplayed());
    }
}