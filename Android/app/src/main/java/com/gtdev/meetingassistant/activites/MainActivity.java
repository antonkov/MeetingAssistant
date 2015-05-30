package com.gtdev.meetingassistant.activites;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;


public class MainActivity extends AppCompatActivity {

    private static final String KEY_EMAIL = "email";
    protected static String userEmail;
    private static List<EventInfo> eventInfos = new ArrayList<>();
    private EmptyRecyclerView recList;
    private final EventAdapter eventAdapter = new EventAdapter(eventInfos, new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, EventInfoActivity.class);
            intent.putExtra("test", eventInfos.get(recList.getChildAdapterPosition(v)));
            startActivity(intent);
        }
    });

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
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);


        recList.setEmptyView(findViewById(R.id.empty_view));
        recList.setAdapter(eventAdapter);
        recList.setItemAnimator(new SlideInLeftAnimator());

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
                break;
            case 1:
                if (resultCode == 1) {
                    ((SwipeRefreshLayout) findViewById(R.id.swipeToRefreshLayout)).setRefreshing(true);
                    updateContent();
                }
                break;

        }
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
                eventInfos.clear();
                for (int i = 0; i < timeline.length(); i++) {
                    try {
                        JSONObject event = timeline.getJSONObject(i);
                        String id = event.getString("id");
                        String title = event.getString("title");
                        String date = event.getString("date");
                        List<String> users = new ArrayList<String>();
                        JSONArray usersJson = event.getJSONArray("users");
                        for (int j = 0; j < usersJson.length(); j++) {
                            users.add(usersJson.getString(j));
                        }
                        eventInfos.add(new EventInfo(id, title, date, users, i % 2 == 0));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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
