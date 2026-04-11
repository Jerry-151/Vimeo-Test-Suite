package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;

import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() throws InterruptedException {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver.get("https://vimeo.com/watch");

        // Presentation delay
        Thread.sleep(3000);
    }

    @AfterMethod
    public void tearDown() throws InterruptedException {
        Thread.sleep(2000); // allow viewer to see result
        if (driver != null) {
            driver.quit();
        }
    }
}