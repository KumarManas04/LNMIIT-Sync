package com.infinitysolutions.lnmiitsync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EventDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        TextView titleView = (TextView)findViewById(R.id.event_details_title);
        TextView descriptionView = (TextView)findViewById(R.id.event_details_description);
        TextView timeView = (TextView)findViewById(R.id.event_details_time);
        TextView venueView = (TextView)findViewById(R.id.event_details_venue);
        TextView dateTextView = (TextView)findViewById(R.id.event_details_date);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        long startTime = intent.getLongExtra("startTime",0);
        long endTime = intent.getLongExtra("endTime",0);
        String venue = intent.getStringExtra("venue");

        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat sdf1 = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
        sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));



        String startTimeString = sdf.format(new Date(startTime));
        String endTimeString = sdf.format(new Date(endTime));
        String date = sdf1.format(new Date(startTime));


        if(title != null) {
            if (title.equals("")) {
                titleView.setText("(No event name)");
            } else {
                titleView.setText(title);
            }
        }

        dateTextView.setText(date);
        if(endTime == 0) {
            timeView.setText(startTimeString);
        }else{
            timeView.setText(startTimeString + " - " + endTimeString);
        }
        venueView.setText(venue);

        if(description != null) {
            if (description.equals("")) {
                descriptionView.setText("(No description available)");
            } else {
                descriptionView.setText(description);
            }
        }else{
            descriptionView.setText("(No description available)");
        }
    }

    public void backPress(View view){
        finish();
    }
}
