package com.infinitysolutions.lnmiitsync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String startTime = intent.getStringExtra("startTime");
        long endTime = intent.getLongExtra("endTime",0);
        String venue = intent.getStringExtra("venue");

        String endTimeString = new SimpleDateFormat("h:mm a", Locale.ENGLISH).format(new Date(endTime));
        TextView titleView = (TextView)findViewById(R.id.event_details_title);
        TextView descriptionView = (TextView)findViewById(R.id.event_details_description);
        TextView timeView = (TextView)findViewById(R.id.event_details_time);
        TextView venueView = (TextView)findViewById(R.id.event_details_venue);

        titleView.setText(title);
        descriptionView.setText(description);
        timeView.setText(startTime + " - " + endTimeString);
        venueView.setText(venue);
    }
}
