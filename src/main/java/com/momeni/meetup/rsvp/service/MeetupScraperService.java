package com.momeni.meetup.rsvp.service;

import com.momeni.meetup.rsvp.config.Hook;
import com.momeni.meetup.rsvp.helper.VisibilityHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MeetupScraperService {

    @Value("${meetup.url}")
    String meetupBaseUrl;

    private static final Pattern eventRsvpOpenPattern = Pattern.compile("\"rsvpOpenTime\":\"(?<dateTime>\\d+-\\d+-\\d{2}T\\d+:\\d+:\\d+-\\d+:\\d+)\",");
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


    // TODO: should return events and their rsvp time
    public HashMap<String, Date> getEventUrlRsvpOpenMap(List<String> eventUrls) throws IOException {
        HashMap<String, Date> eventUrlRsvpOpenDateMap = new HashMap<>();

        for (String eventUrl : eventUrls) {
            Document document = Jsoup.connect(eventUrl).get();

            Matcher eventRsvpOpenMatcher = eventRsvpOpenPattern.matcher(document.toString());

            String strDate = "";
            if (eventRsvpOpenMatcher.find()) {
                strDate = eventRsvpOpenMatcher.group("dateTime");
            }

            Date d = this.stringToISO8601(strDate);
            eventUrlRsvpOpenDateMap.put(eventUrl, d);
        }

        return eventUrlRsvpOpenDateMap;
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
