package com.gtdev.meetingassistant.activites;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.gtdev.meetingassistant.R;
import com.gtdev.meetingassistant.adapters.EventAdapter;
import com.gtdev.meetingassistant.model.EventInfo;
import com.gtdev.meetingassistant.utils.RestClientHelper;
import com.gtdev.meetingassistant.views.EmptyRecyclerView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.melnykov.fab.FloatingActionButton;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String KEY_EMAIL = "email";
    protected static String userEmail;
    public static List<EventInfo> eventInfos = new ArrayList<>();
    private EmptyRecyclerView recList;
    private final EventAdapter eventAdapter = new EventAdapter(eventInfos, new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, EventInfoActivity.class);
            intent.putExtra("event_id", recList.getChildAdapterPosition(v));
            startActivityForResult(intent, 2);
        }
    });
    private LinearLayoutManager llm;

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
                startActivityForResult(new Intent(MainActivity.this, AddEventActivity.class), 1);
            }
        });
        recList = (EmptyRecyclerView) findViewById(R.id.eventList);
        recList.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        recList.setEmptyView(findViewById(R.id.empty_view));
        recList.setAdapter(eventAdapter);
        recList.getItemAnimator().setAddDuration(1000);
        recList.getItemAnimator().setRemoveDuration(1000);
        recList.getItemAnimator().setMoveDuration(1000);
        recList.getItemAnimator().setChangeDuration(1000);

        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefreshLayout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateContent();
            }
        });
        swipeLayout.setColorSchemeResources(android.R.color.holo_red_light);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        userEmail = sharedPref.getString(KEY_EMAIL, null);
        if (userEmail == null) {
            startActivityForResult(new Intent(this, SignInActivity.class), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(KEY_EMAIL, data.getStringExtra("email"));
                userEmail = data.getStringExtra("email");
                editor.commit();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((SwipeRefreshLayout) findViewById(R.id.swipeToRefreshLayout)).setRefreshing(true);
                        updateContent();
                    }
                }, 1000);
                break;
            case 1:
                if (resultCode == 1) {
                    updateIdOfNewEvent();
                    eventAdapter.notifyItemInserted(0);
                    recList.scrollToPosition(0);
                }
                break;
            case 2:
                eventAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void updateIdOfNewEvent() {
        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefreshLayout);
        RestClientHelper.getEvents("grtkachenko@gmail.com", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                swipeLayout.setRefreshing(false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                swipeLayout.setRefreshing(false);
                for (int i = 0; i < timeline.length(); i++) {
                    try {
                        JSONObject event = timeline.getJSONObject(i);
                        String id = event.getString("id");
                        boolean have = false;
                        for (EventInfo eventInfo : eventInfos) {
                            if (id.equals(eventInfo.id)) {
                                have = true;
                                break;
                            }
                        }
                        if (!have) {
                            for (EventInfo eventInfo : eventInfos) {
                                if (eventInfo.id == null) {
                                    eventInfo.id = id;
                                    break;
                                }
                            }
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void updateContent() {
        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefreshLayout);
        RestClientHelper.getEvents("grtkachenko@gmail.com", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                swipeLayout.setRefreshing(false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                swipeLayout.setRefreshing(false);
                int beforeCount = eventInfos.size();
                eventInfos.clear();
                for (int i = 0; i < timeline.length(); i++) {
                    try {
                        JSONObject event = timeline.getJSONObject(i);
                        String id = event.getString("id");
                        String title = event.getString("title");
                        String date = event.getString("date");
                        boolean hasAudio = event.getBoolean("hasAudio");
                        List<String> users = new ArrayList<String>();
                        JSONArray usersJson = event.getJSONArray("users");
                        for (int j = 0; j < usersJson.length(); j++) {
                            users.add(usersJson.getString(j));
                        }
                        eventInfos.add(new EventInfo(id, title, date, users, hasAudio));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                SnackbarManager.show(
                        Snackbar.with(MainActivity.this)
                                .text("" + (eventInfos.size() - beforeCount) + " items updated")
                                .duration(Snackbar.SnackbarDuration.LENGTH_SHORT));
                Collections.reverse(eventInfos);
                eventAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                swipeLayout.setRefreshing(false);
            }
        });
    }
}
