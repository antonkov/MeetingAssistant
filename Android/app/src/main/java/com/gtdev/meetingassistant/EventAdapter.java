package com.gtdev.meetingassistant;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by gtkachenko on 29/05/15.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    public static final int MAX_ATTENDANTS = 4;
    private final List<EventInfo> eventInfoList;
    private final View.OnClickListener onClickListener;

    public EventAdapter(List<EventInfo> eventInfoList, View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.eventInfoList = eventInfoList;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.event_card, parent, false);
        itemView.setOnClickListener(onClickListener);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        EventInfo eventInfo = eventInfoList.get(position);
        holder.title.setText(eventInfo.title);
        holder.date.setText(eventInfo.date);
        holder.attendants.removeAllViews();
        for (int i = 0; i < Math.min(MAX_ATTENDANTS, eventInfo.attendants.size()); i++) {
            TextView textView = new TextView(holder.attendants.getContext());
            textView.setText(eventInfo.attendants.get(i));
            holder.attendants.addView(textView);
        }
        if (eventInfo.attendants.size() > MAX_ATTENDANTS) {
            TextView textView = new TextView(holder.attendants.getContext());
            textView.setText("and " + (eventInfo.attendants.size() - MAX_ATTENDANTS) + " others");
            holder.attendants.addView(textView);
        }
        holder.micro.setVisibility(eventInfo.recorded ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return eventInfoList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        protected TextView title;
        protected TextView date;
        protected LinearLayout attendants;
        protected ImageView micro;

        public EventViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            date = (TextView) view.findViewById(R.id.date);
            attendants = (LinearLayout) view.findViewById(R.id.attendants);
            micro = (ImageView) view.findViewById(R.id.micro);
        }
    }
}
