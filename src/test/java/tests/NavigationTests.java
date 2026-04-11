package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NavigationTests extends BaseTest {

    @Test
    public void testHomePageLoads() {
        sleep(1500);
        Assert.assertTrue(driver.getCurrentUrl().contains("vimeo"));
    }

    @Test
    public void testPageRefresh() {
        driver.navigate().refresh();
        sleep(2000);
        Assert.assertTrue(driver.getTitle().length() > 0);
    }

    @Test
    public void testBackNavigation() {
        driver.navigate().to("https://vimeo.com/channels");
        sleep(2000);
        driver.navigate().back();
        sleep(2000);
        Assert.assertTrue(driver.getCurrentUrl().contains("vimeo"));
    }

    @Test
    public void testForwardNavigation() {
        driver.navigate().to("https://vimeo.com/channels");
        driver.navigate().back();
        driver.navigate().forward();
        sleep(2000);
        Assert.assertTrue(driver.getCurrentUrl().contains("channels"));
    }

    @Test
    public void testTitleNotEmpty() {
        sleep(1500);
        Assert.assertFalse(driver.getTitle().isEmpty());
    }
}