package com.momeni.meetup.rsvp.cron;

import com.momeni.meetup.rsvp.model.Event;
import com.momeni.meetup.rsvp.service.MeetupScraperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class OncePerDayScheduledTask {
    @Autowired
    private TaskDefinitionBean taskDefinitionBean;

    @Autowired
    private TaskSchedulingService taskSchedulingService;

    @Autowired
    private MeetupScraperService meetupApiService;

    private static final Logger log = LoggerFactory.getLogger(OncePerDayScheduledTask.class);

    // 0 0 14 * * * utc -> 7am mst
    @Scheduled(cron = "0 */2 * * * *")
    public void dailyEventTaskScheduler() throws IOException {
        log.info("Main Scheduler started.. ");
        List<String> eventUrls = meetupApiService.getAllEventUrls();
        List<Event> eventList = meetupApiService.getAllEventsRsvpOpenToday(eventUrls);

        if (eventList.size() > 0) {
            for (Event event : eventList) {
                log.info("Scheduling event: {} at: {}", event.getEventTitle(), event.getRsvpOpensDate());
                taskSchedulingService.scheduleATask(UUID.randomUUID().toString(), taskDefinitionBean, event.getRsvpOpensDate());
            }
        } else {
            log.info("No up coming events to schedule an rsvp task for today");
        }
    }
}