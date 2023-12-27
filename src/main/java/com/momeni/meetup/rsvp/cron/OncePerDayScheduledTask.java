package com.momeni.meetup.rsvp.cron;

import com.momeni.meetup.rsvp.config.Hook;
import com.momeni.meetup.rsvp.helper.VisibilityHelper;
import com.momeni.meetup.rsvp.model.Event;
import com.momeni.meetup.rsvp.service.MeetupScraperService;
import org.apache.commons.lang3.time.DateUtils;
import org.assertj.core.util.DateUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


/**
 * TODO: add waitlist functionality
 */
@Component
public class OncePerDayScheduledTask {
    @Autowired
    private TaskDefinitionBean taskDefinitionBean;

    @Autowired
    private TaskSchedulingService taskSchedulingService;

    @Autowired
    private MeetupScraperService meetupApiService;

    private static final Logger log = LoggerFactory.getLogger(OncePerDayScheduledTask.class);
    private static Timer timer = new Timer();

    // 0 0 14 * * * utc (every day at 7am)
    @Scheduled(cron = "0 0 14 * * *")
    public void dailyEventTaskScheduler() throws IOException {
        log.info("Main Scheduler started.. ");
        List<String> eventUrls = meetupApiService.getAllEventUrls();
        List<Event> eventList = meetupApiService.getAllEventsRsvpOpenToday(eventUrls);

//        eventList.add(new Event("brunch", "https://www.meetup.com/the-sunday-squad/events/298062683/", new Date())); // todo remove
        if (eventList.size() > 0) {
            for (Event event : eventList) {
                log.info("Scheduling event: {} at: {}", event.getEventTitle(), event.getRsvpOpensDate());
                taskSchedulingService.scheduleATask(UUID.randomUUID().toString(), taskDefinitionBean, addMinutesToDate(1, DateUtil.now()));
            }
        } else {
            log.info("No up coming events to schedule an rsvp task for today");
        }
    }

    public static Date addMinutesToDate(int minutes, Date beforeTime) {

        long curTimeInMs = beforeTime.getTime();
        Date afterAddingMins = new Date(curTimeInMs
                + (minutes * 60000)); // min in ms
        return afterAddingMins;
    }
}