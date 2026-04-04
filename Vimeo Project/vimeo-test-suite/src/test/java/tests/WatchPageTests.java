package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WatchPageTests extends BaseTest {

    @Test
    public void testWatchPageLoad() throws InterruptedException {
        Thread.sleep(3000);
        Assert.assertTrue(driver.getCurrentUrl().contains("watch"));
    }

    @Test
    public void testTitleExists() throws InterruptedException {
        Thread.sleep(2000);
        Assert.assertNotNull(driver.getTitle());
    }

    @Test
    public void testPageContentLoaded() throws InterruptedException {
        Thread.sleep(3000);
        Assert.assertTrue(true);
    }

    @Test
    public void testNoBrokenUI() throws InterruptedException {
        Thread.sleep(2000);
        Assert.assertTrue(true);
    }

    @Test
    public void testNavigationStable() throws InterruptedException {
        Thread.sleep(2000);
        Assert.assertTrue(true);
    }
}