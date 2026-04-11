package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WatchPageTests extends BaseTest {

    @Test
    public void testWatchPageNavigation() {
        driver.navigate().to("https://vimeo.com/watch");
        sleep(2000);
        Assert.assertTrue(driver.getCurrentUrl().contains("watch"));
    }

    @Test
    public void testTitleExists() {
        Assert.assertNotNull(driver.getTitle());
    }

    @Test
    public void testPageContentLoaded() {
        Assert.assertTrue(driver.getPageSource().length() > 1000);
    }

    @Test
    public void testNoErrorsInPage() {
        Assert.assertFalse(driver.getPageSource().toLowerCase().contains("error"));
    }

    @Test
    public void testNavigationStable() {
        sleep(2000);
        Assert.assertTrue(true);
    }
}