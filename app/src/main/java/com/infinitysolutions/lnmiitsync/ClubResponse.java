package com.infinitysolutions.lnmiitsync;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClubResponse {

    @SerializedName("name")
    @Expose
    private String name;

    public String getName() {
        return name;
    }

}