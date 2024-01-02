package com.momeni.meetup.rsvp.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PostConstruct;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static java.lang.Boolean.TRUE;
import static org.springframework.util.ObjectUtils.isEmpty;

@Component
public class DriverConfig {

    @Value("${selenium.browser.headless:false}")
    private Boolean headless;

    private WebDriver driver;

    private static final Logger LOGGER = LoggerFactory.getLogger(DriverConfig.class);

    public WebDriverWait wait;

    public WebDriver getDriver() {
        if (isEmpty(driver)) {
            initializeDriver();
        }
        return driver;
    }

    private void initializeDriver() {
        // Prevent mem leak
        if (!isEmpty(driver)) {
            closeDriver();
        }

        // Disable driver log output
        System.setProperty("webdriver.chrome.silentOutput", "true");
        setChromeDriver();

        // Navigate
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
    }

    public WebDriverWait getWait() {
        if (isEmpty(wait)) {
            initializeDriver();
        }
        return wait;
    }

    @PostConstruct
    public void initialize() {
        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (isDriverLoaded()) {
                LOGGER.info("Shutdown signal detected: Closing opened drivers");
                closeDriver();
                LOGGER.info("Opened drivers closed");
            }
        }));
    }

    private void setChromeDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--start-maximized");

        // Headless mode
        if (TRUE.equals(headless)) {
            chromeOptions.addArguments("--headless=new");
            chromeOptions.addArguments("--disable-gpu");
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--window-size=1920,1080");
            chromeOptions.addArguments("--disable-dev-shm-usage");
            chromeOptions.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.84 Safari/537.36");
        }

        driver = new ChromeDriver(chromeOptions);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private boolean isDriverLoaded() {
        return driver != null;
    }

    public void closeDriver() {
        if (driver == null) {
            return;
        }
        driver.quit();
        driver = null;
    }
}
