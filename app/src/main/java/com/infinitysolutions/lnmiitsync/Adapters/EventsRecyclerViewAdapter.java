package com.infinitysolutions.lnmiitsync.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.infinitysolutions.lnmiitsync.Event;
import com.infinitysolutions.lnmiitsync.EventDetailsActivity;
import com.infinitysolutions.lnmiitsync.MainActivity;
import com.infinitysolutions.lnmiitsync.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<EventsRecyclerViewAdapter.ViewHolder> {

    private List<Event> mEventsList;
    private Context mContext;
    private boolean mShowDate;
    private String startTime;
    private String endTime;
    private String date;
    private long currentTime;
    private SimpleDateFormat sdf;
    private SimpleDateFormat sdf1;
    private String colors[];
    private boolean[] notify;
    private List<Integer> index;
    private String TAG = "EventsRecyclerView";

    public EventsRecyclerViewAdapter(Context context, List<Event> eventsList,boolean showDate) {
        mContext = context;
        mShowDate = showDate;
        mEventsList = eventsList;
        colors = new String[5];
        colors[0] = "#ff7043"; //Orange
        colors[1] = "#4dd0e1"; //Cyan
        colors[2] = "#5c6bc0"; //Indigo
        colors[3] = "#e91e63"; //Pink
        colors[4] = "#ffca28"; //Amber

        notify = new boolean[mEventsList.size()];
        currentTime = System.currentTimeMillis() + 19800000;

        sdf = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        if(mShowDate) {
            sdf1 = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
            sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
        }

        index = new ArrayList<Integer>();
        int j = 0;
        for(int i = 0; i < mEventsList.size(); i++){
            if(j == 5){
                j = 0;
            }
            index.add(j);
            j++;
        }
        Collections.shuffle(index);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;
        private TextView mVenueTextView;
        private TextView mTimeTextView;
        private TextView mIndicatorView;
        private LinearLayout mEventLinearLayout;
        private LinearLayout mMainLinearLayout;
        private ImageView mNotifyImageView;
        private TextView mNotifyTextView;

        public ViewHolder(@NonNull View v) {
            super(v);
            mTitleTextView = (TextView) v.findViewById(R.id.event_title);
            mVenueTextView = (TextView) v.findViewById(R.id.event_venue);
            mTimeTextView = (TextView) v.findViewById(R.id.event_time);
            mIndicatorView = (TextView) v.findViewById(R.id.indicator);
            mEventLinearLayout = (LinearLayout)v.findViewById(R.id.event_linear_layout);
            mMainLinearLayout = (LinearLayout)v.findViewById(R.id.main_linear_layout);
            mNotifyImageView = (ImageView)v.findViewById(R.id.notify_image_view);
            mNotifyTextView = (TextView)v.findViewById(R.id.notify_text_view);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        if(mContext instanceof MainActivity) {
            if(((MainActivity) mContext).isBeingNotified(mEventsList.get(position).getId())){
                holder.mNotifyImageView.setImageResource(R.drawable.checked_icon);
                holder.mNotifyTextView.setTextColor(Color.parseColor("#29B6F6"));
                notify[position] = true;
            }else{
                holder.mNotifyImageView.setImageResource(R.drawable.notify_no);
                holder.mNotifyTextView.setTextColor(Color.parseColor("#607D8B"));
                notify[position] = false;
            }
        }

        holder.mTitleTextView.setText(mEventsList.get(position).getEventTitle());
        holder.mVenueTextView.setText(mEventsList.get(position).getVenue());

        startTime = sdf.format(new Date(mEventsList.get(position).getStartTime()));
        if(mShowDate){
            date = sdf1.format(new Date(mEventsList.get(position).getStartTime()));

            if(mEventsList.get(position).getEndTime() == 0){
                holder.mTimeTextView.setText(date + "\n" + startTime);
            }else{
                endTime = sdf.format(new Date(mEventsList.get(position).getEndTime()));;
                holder.mTimeTextView.setText( date + "\n" + startTime + " - " + endTime);
            }
        }else {

            if (mEventsList.get(position).getEndTime() == 0) {
                holder.mTimeTextView.setText(startTime);
            } else {
                endTime = sdf.format(new Date(mEventsList.get(position).getEndTime()));
                holder.mTimeTextView.setText(startTime + " - " + endTime);
            }
        }

        Drawable mDrawable = ContextCompat.getDrawable(mContext, R.drawable.round_indicator_background);
        mDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(colors[index.get(position)]),PorterDuff.Mode.SRC));
        holder.mIndicatorView.setBackground(mDrawable);
        if(mEventsList.get(position).getEventTitle().equals("")){
            holder.mIndicatorView.setText("");
        }else {
            holder.mIndicatorView.setText(mEventsList.get(position).getEventTitle().substring(0, 1).toUpperCase());
        }
        startTime = "";
        endTime = "";

        holder.mEventLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,EventDetailsActivity.class);
                intent.putExtra("title",mEventsList.get(position).getEventTitle());
                intent.putExtra("description",mEventsList.get(position).getEventDescription());
                intent.putExtra("startTime",mEventsList.get(position).getStartTime());
                intent.putExtra("endTime",mEventsList.get(position).getEndTime());
                intent.putExtra("venue",mEventsList.get(position).getVenue());
                mContext.startActivity(intent);
            }
        });

        if(mEventsList.get(position).getStartTime() > currentTime) {
            holder.mNotifyImageView.setEnabled(true);
            holder.mMainLinearLayout.getBackground().setAlpha(255);
            holder.mNotifyImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!notify[position]) {
                        if (mContext instanceof MainActivity) {
                            ((MainActivity) mContext).notifyEvent(mEventsList.get(position).getId(), mEventsList.get(position));
                        }
                        holder.mNotifyImageView.setImageResource(R.drawable.checked_icon);
                        holder.mNotifyTextView.setTextColor(Color.parseColor("#29B6F6"));
                        notify[position] = true;
                    } else {
                        if (mContext instanceof MainActivity) {
                            ((MainActivity) mContext).doNotNotify(mEventsList.get(position).getId());
                        }
                        holder.mNotifyImageView.setImageResource(R.drawable.notify_no);
                        holder.mNotifyTextView.setTextColor(Color.parseColor("#607D8B"));
                        notify[position] = false;
                    }
                }
            });
        }else{
            holder.mMainLinearLayout.getBackground().setAlpha(150);
            holder.mNotifyImageView.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return mEventsList.size();
    }
}
