package com.momeni.meetup.rsvp.cron;

import com.momeni.meetup.rsvp.config.Hook;
import com.momeni.meetup.rsvp.helper.VisibilityHelper;
import com.momeni.meetup.rsvp.model.Event;
import com.momeni.meetup.rsvp.service.MeetupScraperService;
import org.apache.commons.lang3.time.DateUtils;
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

    private static final Logger log = LoggerFactory.getLogger(OncePerDayScheduledTask.class);
    private static Timer timer = new Timer();

    // 0 0 7 * * *
    // cron = "0 44 15 * * *"
    @Scheduled(cron = "0 */3 * * * *")
    public void fetchAllEventsWithRsvpOpenForTheDay() throws IOException, InterruptedException {
        // check if there is events to rsvp to today

        log.info("Main Scheduler started.. ");

//        System.out.println("got here");
//        List<String> eventUrls = null;
//
//        try {
//            eventUrls = meetupApiService.getAllEventUrls();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        List<Event> eventList = null;
//        try {
//            eventList = meetupApiService.getAllEventsRsvpOpenToday(eventUrls);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        if(eventList.size() > 0) {
//            // TODO: remove later
//            Event event = new Event("Brunch at Sunday's Best. (Brunch Hard)", "https://www.meetup.com/the-sunday-squad/events/298062683/", new Date());
//            eventList.add(event);
//
//            TimerTask CheckEventsTask = new OncePerDayScheduledTask();
//            timer.schedule(CheckEventsTask, event.getRsvpOpensDate());
//            timer.schedule(CheckEventsTask, DateUtils.addMinutes(new Date(), 2));
//            log.info("task has been scheduled for event: {}", event.getEventTitle());
//
////            for(Event e : eventList) {
////                TimerTask CheckEventsTask = new OncePerDayScheduledTask();
////                timer.schedule(CheckEventsTask, event.getRsvpOpensDate());
////                timer.schedule(CheckEventsTask, DateUtils.addMinutes(new Date(), 2));
////                log.info("task has been scheduled for event: {}", event.getEventTitle());
////            }


        taskSchedulingService.scheduleATask("some id", taskDefinitionBean, "*/30 * * * * *"); // taskDefinition.getCronExpression());
    }
}