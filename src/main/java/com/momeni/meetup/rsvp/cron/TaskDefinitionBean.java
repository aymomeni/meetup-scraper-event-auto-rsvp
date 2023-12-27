package com.momeni.meetup.rsvp.cron;

import com.momeni.meetup.rsvp.config.Hook;
import com.momeni.meetup.rsvp.helper.VisibilityHelper;
import com.momeni.meetup.rsvp.model.Event;
import com.momeni.meetup.rsvp.service.MeetupScraperService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Timer;

@Service
public class TaskDefinitionBean implements Runnable {

    @Value("${meetup.url}")
    String meetupBaseUrl;

    @Value("${username}")
    String username;

    @Value("${password}")
    String password;

    @Autowired
    private Hook hooks;

    @Autowired
    private TaskSchedulingService taskSchedulingService;

    @Autowired
    private VisibilityHelper visibilityHelper;

    @Autowired
    private MeetupScraperService meetupApiService;

    private static final Logger log = LoggerFactory.getLogger(OncePerDayScheduledTask.class);
    private static Timer timer = new Timer();

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
            System.out.println("Event: " + event.getEventTitle() + " url: " + event.getEventUrl() + " rsvp opens date: " + event.getRsvpOpensDate());

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

            try {
                Thread.sleep(3000);
            } catch (InterruptedException exception) {
                throw new RuntimeException(exception);
            }

            visibilityHelper.waitForPresenceOf(By.xpath("//button[@data-testid=\"attend-irl-btn\"]"));

            log.info("Rsvp to event button present");

            WebElement attendButton = driver.findElement(By.xpath("//button[@data-testid=\"attend-irl-btn\"]"));
            visibilityHelper.waitForVisibilityOf(attendButton);
            attendButton.click();
            hooks.closeDriver();
        }
    }

    public TaskDefinition getTaskDefinition() {
        return taskDefinition;
    }

    public void setTaskDefinition(TaskDefinition taskDefinition) {
        this.taskDefinition = taskDefinition;
    }
}
