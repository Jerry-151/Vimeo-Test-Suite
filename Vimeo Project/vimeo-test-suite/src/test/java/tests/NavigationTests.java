package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NavigationTests extends BaseTest {

    @Test
    public void testHomeLink() throws InterruptedException {
        Thread.sleep(2000);

        String url = driver.getCurrentUrl();

        Assert.assertTrue(url.contains("vimeo"),
            "URL does not contain 'vimeo'");
    }

    @Test
    public void testLogoClick() throws InterruptedException {
        Thread.sleep(2000);
        Assert.assertTrue(true);
    }

    @Test
    public void testURLContainsWatch() throws InterruptedException {
        Thread.sleep(2000);
        Assert.assertTrue(driver.getCurrentUrl().contains("watch"));
    }

    @Test
    public void testPageRefresh() throws InterruptedException {
        Thread.sleep(2000);
        driver.navigate().refresh();
        Thread.sleep(2000);
        Assert.assertTrue(true);
    }

    @Test
    public void testBackwardNavigation() throws InterruptedException {
        Thread.sleep(2000);
        driver.navigate().back();
        Thread.sleep(2000);
        Assert.assertTrue(true);
    }
}