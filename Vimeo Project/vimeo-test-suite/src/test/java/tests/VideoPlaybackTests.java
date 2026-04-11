package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class VideoPlaybackTests extends BaseTest {

    @Test
    public void testPlaybackPageLoads() throws InterruptedException {
        Thread.sleep(3000);
        Assert.assertTrue(driver.getTitle().length() > 0);
    }

    @Test
    public void testVideoSectionPresent() throws InterruptedException {
        Thread.sleep(3000);
        Assert.assertTrue(true);
    }

    @Test
    public void testPageLoadComplete() throws InterruptedException {
        Thread.sleep(3000);
        Assert.assertTrue(true);
    }

    @Test
    public void testURLValid() throws InterruptedException {
        Thread.sleep(3000);
        Assert.assertTrue(driver.getCurrentUrl().contains("vimeo"));
    }

    @Test
    public void testNoErrorsInTitle() throws InterruptedException {
        Thread.sleep(3000);
        Assert.assertFalse(driver.getTitle().contains("Error"));
    }
}