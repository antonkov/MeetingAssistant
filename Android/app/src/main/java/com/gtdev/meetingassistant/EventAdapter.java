package com.gtdev.meetingassistant;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by gtkachenko on 29/05/15.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private final List<EventInfo> eventInfoList;

    public EventAdapter(List<EventInfo> eventInfoList) {
        this.eventInfoList = eventInfoList;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.event_card, parent, false);

        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        EventInfo eventInfo = eventInfoList.get(position);
//        holder.title.setText(eventInfo.title);
//        holder.date.setText(eventInfo.date);
    }

    @Override
    public int getItemCount() {
        return eventInfoList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        protected TextView title;
        protected TextView date;
        protected TextView attendant;

        public EventViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            date = (TextView) view.findViewById(R.id.date);
            attendant = (TextView) view.findViewById(R.id.attendant);
        }
    }
}
