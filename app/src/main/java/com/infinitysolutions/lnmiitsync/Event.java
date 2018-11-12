package com.infinitysolutions.lnmiitsync;

public class Event {
    private String mTitle;
    private String mDescription;
    private String mVenue;
    private Long mEndTime;

    public Event(String title, String description, String venue, Long endTime){
        mTitle = title;
        mDescription = description;
        mVenue = venue;
        mEndTime = endTime;
    }

    public String getEventTitle(){
        return mTitle;
    }

    public String getEventDescription(){
        return mDescription;
    }

    public String getVenue(){
        return mVenue;
    }

    public Long getEndTime(){
        return mEndTime;
    }
}
