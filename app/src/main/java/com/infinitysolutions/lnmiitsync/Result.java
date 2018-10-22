package com.infinitysolutions.lnmiitsync;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Result {

    @SerializedName("clubs")
    @Expose
    private List<String> clubs = null;

    @SerializedName("googleId")
    @Expose
    private String googleId;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("batch")
    @Expose
    private String batch;

    public List<String> getClubs() {
        return clubs;
    }

    public void setClubs(List<String> clubs) {
        this.clubs = clubs;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

}