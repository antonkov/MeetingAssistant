package com.gtdev.meetingassistant;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gtkachenko on 29/05/15.
 */
public class EventInfo implements Parcelable {
    public final String title;
    public final String date;
    public final List<String> attendants;
    public boolean recorded = false;

    public EventInfo(String title, String date, List<String> attendants, boolean recorded) {
        this.title = title;
        this.date = date;
        this.attendants = attendants;
        this.recorded = recorded;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(recorded ? 1 : 0);
        dest.writeString(title);
        dest.writeString(date);
        dest.writeStringList(attendants);
    }

    public static final Parcelable.Creator<EventInfo> CREATOR
            = new Parcelable.Creator<EventInfo>() {
        public EventInfo createFromParcel(Parcel in) {
            boolean recorded = in.readInt() == 1;
            String title = in.readString();
            String date = in.readString();
            List<String> list = new ArrayList<>();
            in.readStringList(list);
            return new EventInfo(title, date, list, recorded);
        }

        public EventInfo[] newArray(int size) {
            return new EventInfo[size];
        }
    };
}
