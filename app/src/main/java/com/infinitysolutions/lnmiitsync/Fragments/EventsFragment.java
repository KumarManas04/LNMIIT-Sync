package com.infinitysolutions.lnmiitsync.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.infinitysolutions.lnmiitsync.Event;
import com.infinitysolutions.lnmiitsync.RetrofitResponses.EventResponse;
import com.infinitysolutions.lnmiitsync.Adapters.EventsRecyclerViewAdapter;
import com.infinitysolutions.lnmiitsync.R;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_EVENTS_DATA;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_NAME;

public class EventsFragment extends Fragment {

    private RecyclerView mEventsRecyclerView;
    private TextView mLoadingTextView;
    private ImageView mRecyclerEmptyImageView;
    private Context mContext;
    private List<Event> mEvents;
    private long mStartTime;
    private long mEndTime;
    private int screenWidth;
    private int mark;
    private String TAG = "EventsFragment";

    public EventsFragment() {
        //Required empty public constructor
    }

    public static EventsFragment newInstance() {
        return new EventsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events_view, container, false);
        mEventsRecyclerView = rootView.findViewById(R.id.events_recycler_view);
        mEventsRecyclerView.setVisibility(View.VISIBLE);
        mLoadingTextView = rootView.findViewById(R.id.loading_view);
        mLoadingTextView.setVisibility(View.INVISIBLE);
        mRecyclerEmptyImageView = rootView.findViewById(R.id.recycler_empty_view);
        mRecyclerEmptyImageView.setVisibility(View.INVISIBLE);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        mEventsRecyclerView.setLayoutManager(mLayoutManager);
        mEvents = new ArrayList<Event>();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = (displayMetrics.widthPixels)/2;

        loadEvents();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    public void setParams(long startTime, long endTime){
        mStartTime = startTime;
        mEndTime = endTime;
    }

    private void loadEvents() {
        mRecyclerEmptyImageView.setImageResource(R.drawable.loading);
        mRecyclerEmptyImageView.getLayoutParams().width = 150;
        mRecyclerEmptyImageView.getLayoutParams().height = 150;
        mLoadingTextView.setText("Loading...");
        mLoadingTextView.setVisibility(View.VISIBLE);
        mRecyclerEmptyImageView.setVisibility(View.VISIBLE);
        mEventsRecyclerView.setVisibility(View.INVISIBLE);

        SharedPreferences sharedPrefs = mContext.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);

        String json = sharedPrefs.getString(SHARED_PREF_EVENTS_DATA,"{}");
        List<EventResponse> eventResponses = Arrays.asList(new Gson().fromJson(json, EventResponse[].class));
        loadIntoRecyclerView(eventResponses);
    }

    private void loadIntoRecyclerView(final List<EventResponse> list) {

        final long currentTime = System.currentTimeMillis() + 19800000;
        Log.d(TAG,"Current time = " + currentTime);
        mark = 0;

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what == 0){
                    EventsRecyclerViewAdapter adapter;
                    if(mStartTime == 0 && mEndTime == 0) {
                        adapter = new EventsRecyclerViewAdapter(mContext, mEvents,true);
                    }else{
                        adapter = new EventsRecyclerViewAdapter(mContext, mEvents,false);
                    }
                    if(mEvents.size() == 0){
                        mRecyclerEmptyImageView.setImageResource(R.drawable.empty_list);
                        mRecyclerEmptyImageView.getLayoutParams().width = screenWidth;
                        mRecyclerEmptyImageView.getLayoutParams().height = screenWidth;
                        mLoadingTextView.setText("No events");
                    }else {
                        mLoadingTextView.setVisibility(View.INVISIBLE);
                        mRecyclerEmptyImageView.setVisibility(View.INVISIBLE);
                        mEventsRecyclerView.setVisibility(View.VISIBLE);
                        mEventsRecyclerView.setAdapter(adapter);
                        mEventsRecyclerView.scrollToPosition(mark);
                    }
                }
                return true;
            }
        });

        Thread thread = new Thread() {
            @Override
            public void run() {
                long endTime;
                String title = "";
                int i = 0;
                if(mStartTime != 0 && mEndTime != 0) {
                    for(EventResponse event : list){
                            if (event.getDate() < mStartTime) {
                                continue;
                            }
                            if (event.getDate() > mEndTime) {
                                break;
                            }

                            if(mark == 0){
                                if(event.getDate() > currentTime){
                                    mark = i;
                                }
                            }

                        if(event.getDuration() == 0) {
                            endTime = 0;
                        }else{
                            endTime = event.getDate() + (event.getDuration() * 60 * 60 * 1000);
                        }
                        if(event.getName() != null) {
                            if(event.getName().length() > 1) {
                                title = event.getName().substring(0, 1).toUpperCase() + event.getName().substring(1);
                            }
                        }
                        mEvents.add(new Event(event.getId(),title,event.getDescription(),event.getVenue(),event.getDate(),endTime));
                        title = "";
                        i++;
                    }
                }else {
                    for (EventResponse event : list) {

                        if(mark == 0){
                            if(event.getDate() > currentTime){
                                mark = i;
                            }
                        }

                        if (event.getDuration() == 0) {
                            endTime = 0;
                        } else {
                            endTime = event.getDate() + (event.getDuration() * 60 * 60 * 1000);
                        }
                        if (event.getName() != null) {
                            if (event.getName().length() > 1) {
                                title = event.getName().substring(0, 1).toUpperCase() + event.getName().substring(1);
                            }
                        }
                        mEvents.add(new Event(event.getId(),title, event.getDescription(), event.getVenue(), event.getDate(), endTime));
                        title = "";
                        i++;
                    }
                }
                Log.d(TAG,"i = " + i + ", Mark = " + mark);
                handler.sendEmptyMessage(0);
            }
        };

        thread.start();
    }
}
