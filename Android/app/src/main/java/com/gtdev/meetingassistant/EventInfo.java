package com.gtdev.meetingassistant;

import java.util.List;

/**
 * Created by gtkachenko on 29/05/15.
 */
public class EventInfo {
    public final String title;
    public final String date;
    public final List<String> attendants;

    public EventInfo(String title, String date, List<String> attendants) {
        this.title = title;
        this.date = date;
        this.attendants = attendants;
    }

}
