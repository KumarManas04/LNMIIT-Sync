package com.infinitysolutions.lnmiitsync.RetrofitResponses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EventResponse {

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("date")
    @Expose
    private long date;

    @SerializedName("forWhom")
    @Expose
    private List<String> forWhom = null;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("club")
    @Expose
    private String club;

    @SerializedName("venue")
    @Expose
    private String venue;

    @SerializedName("duration")
    @Expose
    private long duration;

    @SerializedName("description")
    @Expose
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public List<String> getForWhom() {
        return forWhom;
    }

    public void setForWhom(List<String> forWhom) {
        this.forWhom = forWhom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}