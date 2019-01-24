package com.infinitysolutions.lnmiitsync.Fragments;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.infinitysolutions.lnmiitsync.Adapters.WeekTabAdapter;
import com.infinitysolutions.lnmiitsync.MainActivity;
import com.infinitysolutions.lnmiitsync.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


public class WeekViewFragment extends Fragment {
    private String TAG = "WeekViewFragment";

    public WeekViewFragment() {
        // Required empty public constructor
    }

    public static WeekViewFragment newInstance(){
        return new WeekViewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_week_view, container, false);

        ViewPager viewPager = rootView.findViewById(R.id.view_pager);
        TabLayout tabLayout = rootView.findViewById(R.id.tab_layout);

        WeekTabAdapter adapter = new WeekTabAdapter(getActivity().getSupportFragmentManager());

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = sdf.format(calendar.getTime());
        Log.d(TAG,formattedDate);
        Date date = new Date();
        try {
            date = sdf.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long time = date.getTime() + 19800000;
        long thisMondayTime = 0;
        int tabSelect = 0;
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.MONDAY:
                thisMondayTime = time;
                break;
            case Calendar.TUESDAY:
                tabSelect = 1;
                thisMondayTime = time - (86400000);
                break;
            case Calendar.WEDNESDAY:
                tabSelect = 2;
                thisMondayTime = time - (2 * 86400000);
                break;
            case Calendar.THURSDAY:
                tabSelect = 3;
                thisMondayTime = time - (3 * 86400000);
                break;
            case Calendar.FRIDAY:
                tabSelect = 4;
                thisMondayTime = time - (4 * 86400000);
                break;
            case Calendar.SATURDAY:
                tabSelect = 5;
                thisMondayTime = time - (5 * 86400000);
                break;
            case Calendar.SUNDAY:
                tabSelect = 6;
                thisMondayTime = time - (6 * 86400000);
                break;
        }

        for(int i = 0; i < 7; i++){
            EventsFragment eventsFragment = EventsFragment.newInstance();
            eventsFragment.setParams(thisMondayTime,thisMondayTime + 86400000);
            thisMondayTime = thisMondayTime + 86400000;
            adapter.addFragment(eventsFragment);
        }

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(tabSelect).select();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == ViewPager.SCROLL_STATE_IDLE){
                    ((MainActivity)getActivity()).enableSwipeToRefresh(true);
                }else{
                    ((MainActivity)getActivity()).enableSwipeToRefresh(false);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
