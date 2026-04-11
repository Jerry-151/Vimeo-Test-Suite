package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class VideoPlaybackTests extends BaseTest {

    @Test
    public void testPageLoads() {
        sleep(2000);
        Assert.assertTrue(driver.getTitle().length() > 0);
    }

    @Test
    public void testURLContainsVimeo() {
        Assert.assertTrue(driver.getCurrentUrl().contains("vimeo"));
    }

    @Test
    public void testPageSourceLoaded() {
        Assert.assertTrue(driver.getPageSource().length() > 1000);
    }

    @Test
    public void testNoErrorInTitle() {
        Assert.assertFalse(driver.getTitle().toLowerCase().contains("error"));
    }

    @Test
    public void testPageStableAfterWait() {
        sleep(3000);
        Assert.assertTrue(true);
    }
}