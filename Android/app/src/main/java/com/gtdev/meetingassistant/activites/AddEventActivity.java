package com.gtdev.meetingassistant.activites;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.gtdev.meetingassistant.R;
import com.gtdev.meetingassistant.utils.RestClientHelper;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;


public class AddEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";

    private EditText titleTextView;
    private TextView dateTextView;
    private TextView timeTextView;
    private EditText usersEditTExt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("New meeting");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEventActivity.this.onBackPressed();
            }
        });

        timeTextView = (TextView) findViewById(R.id.timeTextView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        titleTextView = (EditText) findViewById(R.id.titleEditText);
        usersEditTExt = (EditText) findViewById(R.id.usersEditText);


        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);

        findViewById(R.id.dateTextView).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                datePickerDialog.setYearRange(1985, 2028);
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
            }
        });

        findViewById(R.id.timeTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.setCloseOnSingleTapMinute(false);
                timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_event, menu);
        menu.findItem(R.id.action_save).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                StringTokenizer stringTokenizer = new StringTokenizer(usersEditTExt.getText().toString());
                List<String> users = new ArrayList<String>();
                while (stringTokenizer.hasMoreTokens()) {
                    users.add(stringTokenizer.nextToken());
                }
                users.add(MainActivity.userEmail);
                RestClientHelper.createEvent(AddEventActivity.this, titleTextView.getText().toString(), dateTextView.getText().toString(), users, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(AddEventActivity.this, "Success " + statusCode, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(AddEventActivity.this, "Failure " + statusCode, Toast.LENGTH_SHORT).show();
                    }
                });
                setResult(1, null);
                AddEventActivity.this.onBackPressed();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        ((TextView) findViewById(R.id.dateTextView)).setText("" + day + "/" + month + "/" + year);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        ((TextView) findViewById(R.id.timeTextView)).setText("" + hourOfDay + ":" + minute);
    }
}
