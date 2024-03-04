package com.momeni.meetup.rsvp.cron;

import com.momeni.meetup.rsvp.config.DriverConfig;
import com.momeni.meetup.rsvp.helper.VisibilityHelper;
import com.momeni.meetup.rsvp.model.Event;
import com.momeni.meetup.rsvp.service.MeetupScraperService;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TaskDefinitionBean implements Runnable {

    @Value("${meetup.url}")
    String meetupBaseUrl;

    @Value("${username}")
    String username;

    @Value("${password}")
    String password;

    @Autowired
    private DriverConfig hooks;

    @Autowired
    private TaskSchedulingService taskSchedulingService;

    @Autowired
    private VisibilityHelper visibilityHelper;

    @Autowired
    private MeetupScraperService meetupApiService;

    private static final Logger log = LoggerFactory.getLogger(OncePerDayScheduledTask.class);

    private TaskDefinition taskDefinition;

    @Override
    public void run() {
        log.info("Running task within TaskDefinitionBean");

        List<String> eventUrls = null;
        try {
            eventUrls = meetupApiService.getAllEventUrls();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Event> eventList = null;
        try {
            eventList = meetupApiService.getAllEventsRsvpOpenToday(eventUrls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Event event : eventList) {
            log.info("Event: " + event.getEventTitle() + " url: " + event.getEventUrl() + " rsvp opens date: " + event.getRsvpOpensDate());

            final WebDriver driver = hooks.getDriver();
            driver.get(event.getEventUrl());
            WebElement loginButton = driver.findElement(By.id("login-link"));
            loginButton.click();

            visibilityHelper.waitForPresenceOf(By.id("email"));
            WebElement emailInput = driver.findElement(By.id("email"));
            emailInput.sendKeys(username);
            WebElement passwordInput = driver.findElement(By.id("current-password"));
            passwordInput.sendKeys(password);
            WebElement submitButton = driver.findElement(By.name("submitButton"));
            submitButton.click();
            log.info("logged in");

            try {
                Thread.sleep(3500);
            } catch (InterruptedException exception) {
                throw new RuntimeException(exception);
            }

            try {
                visibilityHelper.waitForPresenceOf(By.xpath("//button[@data-testid=\"attend-irl-btn\"]"));
            } catch (TimeoutException timeoutException) {
                log.error(timeoutException.getMessage());
                hooks.closeDriver();
                return;
            }

            try {
                log.info("Rsvp to event button present");
                WebElement attendButton = driver.findElement(By.xpath("//button[@data-testid=\"attend-irl-btn\"]"));
                visibilityHelper.waitForVisibilityOf(attendButton);
                attendButton.click();
                log.info("Rsvp button clicked");
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                hooks.closeDriver();
            }
        }
    }

    public TaskDefinition getTaskDefinition() {
        return taskDefinition;
    }

    public void setTaskDefinition(TaskDefinition taskDefinition) {
        this.taskDefinition = taskDefinition;
    }
}
