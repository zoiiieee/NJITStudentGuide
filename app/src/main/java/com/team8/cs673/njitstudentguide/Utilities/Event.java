package com.team8.cs673.njitstudentguide.Utilities;

/**
 * Created by Aledo-PC on 10/24/2015.
 */
public class Event {
    private int eventID;
    private String EventDate;
    private String EventTime;
    private String EventTitle;
    private String EventDesc;

    public Event(String eventDate, String eventTime, String eventTitle, String eventDesc) {
        EventDate = eventDate;
        EventTime = eventTime;
        EventTitle = eventTitle;
        EventDesc = eventDesc;
    }

    public Event(int eventID, String eventDate, String eventTime, String eventTitle, String eventDesc) {
        this.eventID = eventID;
        EventDate = eventDate;
        EventTime = eventTime;
        EventTitle = eventTitle;
        EventDesc = eventDesc;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getEventDate() {
        return EventDate;
    }

    public void setEventDate(String eventDate) {
        EventDate = eventDate;
    }

    public String getEventTime() {
        return EventTime;
    }

    public void setEventTime(String eventTime) {
        EventTime = eventTime;
    }

    public String getEventTitle() {
        return EventTitle;
    }

    public void setEventTitle(String eventTitle) {
        EventTitle = eventTitle;
    }

    public String getEventDesc() {
        return EventDesc;
    }

    public void setEventDesc(String eventDesc) {
        EventDesc = eventDesc;
    }
}
