package com.frostbittmedia.Hiccups.Objects;

public class LogEvent {

    // ===========================================================
    // Fields
    // ===========================================================

    private String event;
    private String detail;
    private String dateTime;

    public static final String FEEDING_EVENT = "Feeding";
    public static final String SLEEPING_EVENT = "Went to sleep";
    public static final String WOKE_UP_EVENT = "Woke up";
    public static final String DIAPER_CHANGE_EVENT = "Changed diapers";
    public static final String NOTE_EVENT = "Note";

    // ===========================================================
    // Constructors
    // ===========================================================

    public LogEvent(){
    }

    public LogEvent(String event, String dateTime){
        this.event = event;
        this.dateTime = dateTime;
    }

    public LogEvent(String event, String detail, String dateTime){
        this.event = event;
        this.detail = detail;
        this.dateTime = dateTime;
    }

    // ===========================================================
    // Getters & Setters
    // ===========================================================

    public String getEvent() {
        return event;
    }

    public String getDetail() {
        return detail;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
