package com.infinitysolutions.lnmiitsync;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CustomListLayout extends LinearLayout implements View.OnClickListener {
    private String mStartTime;
    private ArrayList<Event> mEventList;
    private Context mContext;

    public CustomListLayout(Context context) {
        super(context);
    }

    public CustomListLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public CustomListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onClick(View v) {
        Event event = mEventList.get((int)v.getTag());
        Intent intent = new Intent(mContext,EventDetailsActivity.class);
        intent.putExtra("title",event.getEventTitle());
        intent.putExtra("description",event.getEventDescription());
        intent.putExtra("startTime",mStartTime);
        intent.putExtra("endTime",event.getEndTime());
        intent.putExtra("venue",event.getVenue());
        mContext.startActivity(intent);
    }

    public void setList(Context context,ArrayList<Event> events,String startTime) {
        mEventList = events;
        mContext = context;
        mStartTime = startTime;
        TextView mEventTitleTextView;
        TextView mEventTimeTextView;
        TextView mEventVenueTextView;

        int index = 0;
        for (Event event : events) {
            View listItem = LayoutInflater.from(mContext).inflate(R.layout.event_list_item, this, false);
            mEventTitleTextView = (TextView) listItem.findViewById(R.id.event_title);
            mEventTimeTextView = (TextView) listItem.findViewById(R.id.event_time);
            mEventVenueTextView = (TextView) listItem.findViewById(R.id.event_venue);

            if (event.getEndTime() == 0) {
                mEventTimeTextView.setText(startTime);
            } else {
                String endTime = new SimpleDateFormat("h:mm a", Locale.ENGLISH).format(new Date(event.getEndTime()));
                mEventTimeTextView.setText(startTime + " - " + endTime);
            }

            mEventTitleTextView.setText(event.getEventTitle());
            mEventVenueTextView.setText(event.getVenue());
            listItem.setOnClickListener(this);
            listItem.setTag(index);
            this.addView(listItem);
            index++;
        }
    }
}
