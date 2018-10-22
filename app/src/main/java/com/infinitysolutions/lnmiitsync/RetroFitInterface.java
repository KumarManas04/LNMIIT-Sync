package com.infinitysolutions.lnmiitsync;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetroFitInterface {

    @FormUrlEncoded
    @POST("log_user")
    Call<ResponseBody> post(
            @Field("username") String username,
            @Field("googleId") String googleId,
            @Field("thumbnail") String thumbnail,
            @Field("clubs") String clubs[],
            @Field("batch") String batch,
            @Field("email") String email
    );

    @GET("get_user/{googleId}")
    Call<Result> getUserDetails(@Path("googleId") String googleId);

    @GET("get_clubs/")
    Call<List<String>>getClubsList();
}
