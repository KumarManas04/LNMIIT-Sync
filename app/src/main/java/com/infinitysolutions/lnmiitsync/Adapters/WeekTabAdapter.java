package com.infinitysolutions.lnmiitsync.Adapters;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class WeekTabAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private final List<String> mFragmentTitleList = new ArrayList<String>();

    public WeekTabAdapter(FragmentManager fm) {
        super(fm);
        mFragmentTitleList.add("Mon");
        mFragmentTitleList.add("Tue");
        mFragmentTitleList.add("Wed");
        mFragmentTitleList.add("Thu");
        mFragmentTitleList.add("Fri");
        mFragmentTitleList.add("Sat");
        mFragmentTitleList.add("Sun");
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment){
        mFragmentList.add(fragment);
    }
}
