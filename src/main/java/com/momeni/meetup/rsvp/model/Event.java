package com.momeni.meetup.rsvp.model;

import java.util.Date;

public class Event {

    private String eventUrl;
    private String eventTitle;

    private Date eventDate;

    private Date rsvpOpensDate;

    public Event(String eventTitle, String eventUrl, Date rsvpOpensDate) {
        this.eventTitle = eventTitle;
        this.eventUrl = eventUrl;
        this.rsvpOpensDate = rsvpOpensDate;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    public Date getRsvpOpensDate() {
        return rsvpOpensDate;
    }

    public void setRsvpOpensDate(Date rsvpOpensDate) {
        this.rsvpOpensDate = rsvpOpensDate;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }
}
