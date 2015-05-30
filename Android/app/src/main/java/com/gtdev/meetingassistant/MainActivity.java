package com.gtdev.meetingassistant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;


public class MainActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Meetings");
        setSupportActionBar(toolbar);

        final FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddEventActivity.class));
            }
        });
        final RecyclerView recList = (RecyclerView) findViewById(R.id.eventList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        final List<EventInfo> eventInfos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            List<String> attendants = new ArrayList<String>() {{
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
                add("grtkachenko@gmail.com");
            }};

            EventInfo eventInfo = new EventInfo("Пойти поспать", "10 may", attendants, i % 2 == 0);
            eventInfos.add(eventInfo);
        }
        final EventAdapter eventAdapter = new EventAdapter(eventInfos, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EventInfoActivity.class);
                intent.putExtra("test", eventInfos.get(recList.getChildAdapterPosition(v)));
                startActivity(intent);
            }
        });
        AnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(eventAdapter);
        recList.setAdapter(eventAdapter);
        recList.setItemAnimator(new SlideInLeftAnimator());

        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefreshLayout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                        SnackbarManager.show(
                                Snackbar.with(MainActivity.this)
                                        .text("0 items updated")
                                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT));
                    }
                }, 5000);
            }
        });
        swipeLayout.setColorSchemeResources(android.R.color.holo_red_light);

    }
}
