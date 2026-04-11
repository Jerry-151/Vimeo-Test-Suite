package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProfileTests extends BaseTest {

    @Test
    public void testProfileURLStructure() {
        sleep(1500);
        Assert.assertTrue(driver.getCurrentUrl().contains("vimeo"));
    }

    @Test
    public void testPageLoads() {
        Assert.assertTrue(driver.getTitle().length() > 0);
    }

    @Test
    public void testPageSourceExists() {
        Assert.assertTrue(driver.getPageSource().length() > 1000);
    }

    @Test
    public void testNoErrorText() {
        Assert.assertFalse(driver.getPageSource().toLowerCase().contains("error"));
    }

    @Test
    public void testBasicNavigation() {
        driver.navigate().to("https://vimeo.com/channels");
        sleep(2000);
        Assert.assertTrue(driver.getCurrentUrl().contains("channels"));
    }
}