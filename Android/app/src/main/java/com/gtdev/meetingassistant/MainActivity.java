package com.gtdev.meetingassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddEventActivity.class));
            }
        });
        RecyclerView recList = (RecyclerView) findViewById(R.id.eventList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        List<EventInfo> eventInfos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            EventInfo eventInfo = new EventInfo(null, null, null);
            eventInfos.add(eventInfo);
        }
        EventAdapter eventAdapter = new EventAdapter(eventInfos);
        recList.setAdapter(eventAdapter);

    }
}
