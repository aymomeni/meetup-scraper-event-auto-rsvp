package com.momeni.meetup.rsvp.scheduler;

/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OncePerDayScheduledTask extends TimerTask {
    @Value("${meetup.url}")
    String meetupBaseUrl;

    @Autowired
    private Hook hooks;

    @Autowired
    private VisibilityHelper visibilityHelper;

    @Autowired
    private MeetupScraperService meetupApiService;

    private static final Logger log = LoggerFactory.getLogger(OncePerDayScheduledTask.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    // 0 0 7 * * *
    // cron = "0 44 15 * * *"
    @Scheduled(cron = "0 * * * * *")
    public void fetchAllEventsWithRsvpOpenForTheDay() throws IOException, InterruptedException {

        // TODO: should check if there is any games to rsvp to today
        // TODO: if yes create a timerTask to run at that time

        log.info("The time is now {}", dateFormat.format(new Date()));

        System.out.println("got here");
        List<String> eventUrls = meetupApiService.getAllEventUrls();
        List<Event> eventList = meetupApiService.getAllEventsRsvpOpenToday(eventUrls);

        for(Event event : eventList) {
            System.out.println("Event: " + event.getEventTitle() + " url: " + event.getEventUrl() + " rsvp opens date: " + event.getRsvpOpensDate());
        }

        final WebDriver driver = hooks.getDriver();
        driver.get("https://meetup.com/the-sunday-squad/events/298062683/");
        WebElement loginButton = driver.findElement(By.id("login-link"));
        loginButton.click();
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
//        wait.wait();
        visibilityHelper.waitForPresenceOf(By.id("email"));
        WebElement emailInput = driver.findElement(By.id("email"));
        emailInput.sendKeys("aymomeni@gmail.com");
        WebElement passwordInput = driver.findElement(By.id("current-password"));
        passwordInput.sendKeys("Metallica123#");
        WebElement submitButton = driver.findElement(By.name("submitButton"));
        submitButton.click();
//        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
//        wait.wait();
        Thread.sleep(3000);
        visibilityHelper.waitForPresenceOf(By.xpath("//button[@data-testid=\"attend-irl-btn\"]"));

        System.out.println("element is present");

        WebElement attendButton = driver.findElement(By.xpath("//button[@data-testid=\"attend-irl-btn\"]"));
        visibilityHelper.waitForVisibilityOf(attendButton);
//        attendButton.click(); TODO: uncomment later
        hooks.closeDriver();
    }

    @Override
    public void run() {
        // rsvp to an event

    }
}