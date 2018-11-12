package com.infinitysolutions.lnmiitsync.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.infinitysolutions.lnmiitsync.CustomListLayout;
import com.infinitysolutions.lnmiitsync.Event;
import com.infinitysolutions.lnmiitsync.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<EventsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<ArrayList<Event>> mEventsGroups;
    private String mTime[];
    private String mAmPm[];
    private Context mContext;
    private String TAG = "EventsRecyclerView";

    public EventsRecyclerViewAdapter(Context context, HashMap<Long, ArrayList<Event>> eventsViewItemsList) {
        mContext = context;
        Long keys[];
        keys = eventsViewItemsList.keySet().toArray(new Long[eventsViewItemsList.size()]);
        Arrays.sort(keys);

        String test;
        String str[] = new String[2];
        Date df;
        mTime = new String[keys.length];
        mAmPm = new String[keys.length];
        mEventsGroups = new ArrayList<ArrayList<Event>>();

        for (int i = 0; i < keys.length; i++) {
            mEventsGroups.add(eventsViewItemsList.get(keys[i]));
            df = new Date(keys[i]);
            test = new SimpleDateFormat("h:mm,a",Locale.ENGLISH).format(df);
            str = test.split(",");
            mTime[i] = str[0];
            mAmPm[i] = str[1];
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CustomListLayout mEventListView;
        private TextView mTimeTextView;
        private TextView mAmPmTextView;

        public ViewHolder(@NonNull View v) {
            super(v);
            mEventListView = (CustomListLayout) v.findViewById(R.id.events_list);
            mTimeTextView = (TextView) v.findViewById(R.id.events_group_time_view);
            mAmPmTextView = (TextView) v.findViewById(R.id.events_group_am_pm);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_group_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTimeTextView.setText(mTime[position]);
        holder.mAmPmTextView.setText(mAmPm[position]);
        holder.mEventListView.setList(mContext,mEventsGroups.get(position),mTime[position]+mAmPm[position]);
    }

    @Override
    public int getItemCount() {
        return mEventsGroups.size();
    }
}
