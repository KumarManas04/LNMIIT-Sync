package com.infinitysolutions.lnmiitsync;

public class Event {
    private String mId;
    private String mTitle;
    private String mDescription;
    private String mVenue;
    private Long mStartTime;
    private Long mEndTime;

    public Event(String id, String title, String description, String venue,Long startTime, Long endTime){
        mId = id;
        mTitle = title;
        mDescription = description;
        mVenue = venue;
        mStartTime = startTime;
        mEndTime = endTime;
    }

    public String getId(){
        return mId;
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

    public Long getStartTime(){ return mStartTime;}
}
