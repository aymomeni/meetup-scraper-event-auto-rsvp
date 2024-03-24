package com.momeni.meetup.rsvp.service;

import com.momeni.meetup.rsvp.model.Event;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MeetupScraperService {
    @Value("${meetup.url}")
    String meetupBaseUrl;

    private static final Pattern eventRsvpOpenDatePattern = Pattern.compile("\"rsvpOpenTime\":\"(?<rsvpOpenDate>\\d+-\\d+-\\d{2}T\\d+:\\d+:\\d+-\\d+:\\d+)\",");
    private static final Pattern eventTitlePattern = Pattern.compile("<title>(?<eventTitle>.*)<\\/title>");
    private static final Pattern eventUrlPattern = Pattern.compile("\"eventUrl\":\"(?<eventUrl>https://www.meetup.com/cottonwood-co-ed-adult-soccer/events/\\d+/)");

    public List<String> getAllEventUrls() throws IOException {
        Document document = Jsoup.connect(meetupBaseUrl + "/cottonwood-co-ed-adult-soccer/events/").get();
        Matcher eventUrlMatcher = eventUrlPattern.matcher(document.toString());
        List<String> eventUrls = new ArrayList<>();
        while (eventUrlMatcher.find()) {
            eventUrls.add(eventUrlMatcher.group("eventUrl"));
        }

        return eventUrls;
    }

    public List<Event> getAllEventsRsvpOpenToday(List<String> eventUrls) throws IOException {
        List<Event> eventList = new ArrayList<>();
        for (String eventUrl : eventUrls) {
            Document document = Jsoup.connect(eventUrl).get();

            Matcher eventTitleMatcher = eventTitlePattern.matcher(document.toString());
            String eventTitle = "";
            if (eventTitleMatcher.find()) {
                eventTitle = eventTitleMatcher.group("eventTitle");
            }

            Matcher eventRsvpOpenMatcher = eventRsvpOpenDatePattern.matcher(document.toString());
            String rsvpOpenDateStr = "";
            if (eventRsvpOpenMatcher.find()) {
                rsvpOpenDateStr = eventRsvpOpenMatcher.group("rsvpOpenDate");
            }

            Date eventRsvpOpenDate = this.stringToISO8601(rsvpOpenDateStr);

//            // if saturday indoor game rsvp to 2nd session (because I want to sleep in)
//            if(DateUtils.isSameDay(new Date(), eventRsvpOpenDate) && eventTitle.contains("East Millcreek") && eventTitle.contains("Sat") && eventTitle.contains("2nd")) {
//                Event event = new Event(eventTitle, eventUrl, eventRsvpOpenDate);
//                eventList.add(event);
//            // else if tuesday indoor game rsvp to 1st session (because I've work the next day)
//            } else if(DateUtils.isSameDay(new Date(), eventRsvpOpenDate) && eventTitle.contains("East Millcreek") && !eventTitle.contains("Sat") && !eventTitle.contains("2nd")) { // NOTE: disregard second session, want to get into first session
//                Event event = new Event(eventTitle, eventUrl, eventRsvpOpenDate);
//                eventList.add(event);
//            }

            // all events
            Event event = new Event(eventTitle, eventUrl, eventRsvpOpenDate);
            eventList.add(event);
        }
        return eventList;
    }

    public static Date stringToISO8601(String dateStr) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        try {
            return df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
