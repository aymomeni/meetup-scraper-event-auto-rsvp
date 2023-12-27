package com.momeni.meetup.rsvp.service;

import com.momeni.meetup.rsvp.model.Event;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MeetupScraperService {

    @Value("${meetup.url}")
    String meetupBaseUrl;

    private static final Pattern eventRsvpOpenDatePattern = Pattern.compile("\"rsvpOpenTime\":\"(?<dateTime>\\d+-\\d+-\\d{2}T\\d+:\\d+:\\d+-\\d+:\\d+)\",");
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

    public List<Event> getEventUrlRsvpOpenDateMap(List<String> eventUrls) throws IOException {
        List<Event> eventList = new ArrayList<>();

        for (String eventUrl : eventUrls) {
            Document document = Jsoup.connect(eventUrl).get();

            Matcher eventTitleMatcher = eventTitlePattern.matcher(document.toString());
            String eventTitle = "";
            if (eventTitleMatcher.find()) {
                eventTitle = eventTitleMatcher.group("eventTitle");
            }

            Matcher eventRsvpOpenMatcher = eventRsvpOpenDatePattern.matcher(document.toString());
            String strDate = "";
            if (eventRsvpOpenMatcher.find()) {
                strDate = eventRsvpOpenMatcher.group("dateTime");
            }

            Date eventDate = this.stringToISO8601(strDate);
            Event event = new Event(eventTitle, eventUrl, eventDate);
            eventList.add(event);
        }

        return eventList;
    }

    public List<Event> getAllEventsRsvpOpenToday(List<String> eventUrls) throws IOException {
        List<Event> eventList = new ArrayList<>();
        for (String eventUrl : eventUrls) {
            Document document = Jsoup.connect(eventUrl).get();
            Matcher eventRsvpOpenMatcher = eventRsvpOpenDatePattern.matcher(document.toString());

            Matcher eventTitleMatcher = eventTitlePattern.matcher(document.toString());
            String eventTitle = "";
            if (eventTitleMatcher.find()) {
                eventTitle = eventTitleMatcher.group("eventTitle");
            }

            String strDate = "";
            if (eventRsvpOpenMatcher.find()) {
                strDate = eventRsvpOpenMatcher.group("dateTime");
            }

            Date eventDate = this.stringToISO8601(strDate);

//            if(DateUtils.isSameDay(eventDate, new Date())) {
            if(new Date().after(eventDate) || new Date().before(eventDate)) {
                Event event = new Event(eventTitle, eventUrl, eventDate);
                eventList.add(event);
            }
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