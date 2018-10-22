package com.infinitysolutions.lnmiitsync;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.BASE_URL;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_CLUBS;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_EMAIL_ID;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_GID;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_NAME;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_THUMBNAIL_URL;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_USERNAME;

@SuppressLint("LogNotTimber")
public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private int LOGIN_REQUEST_CODE = 105;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            Log.d(TAG, "No login found");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == LOGIN_REQUEST_CODE) {
            if (intent != null && intent.hasExtra("gId") && intent.hasExtra("batch")) {
                String userName = intent.getStringExtra("userName");
                String emailId = intent.getStringExtra("emailId");
                String gId = intent.getStringExtra("gId");
                String thumbnailUrl = intent.getStringExtra("thumbnailUrl");
                String clubs[] = intent.getStringArrayExtra("clubs");
                String batch = intent.getStringExtra("batch");
                SharedPreferences sharedPrefs = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(SHARED_PREF_USERNAME, userName);
                editor.putString(SHARED_PREF_EMAIL_ID, emailId);
                editor.putString(SHARED_PREF_GID, gId);
                editor.putString(SHARED_PREF_THUMBNAIL_URL, thumbnailUrl);
                Set<String> clubsSet = new HashSet<String>(Arrays.asList(clubs));
                editor.putStringSet(SHARED_PREF_CLUBS,clubsSet);
                editor.apply();
                if (!intent.hasExtra("isRegistered")) {
                    sendDataToServer(userName, gId, thumbnailUrl, clubs, batch, emailId);
                }
            } else {
                //Ask again to login
            }
        }
    }

    private void sendDataToServer(String userName, String gId, String thumbnailUrl, String clubs[], String batch, String emailId) {

        final ProgressDialog dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Signing up...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        //The logging interceptor will be added to the http client
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        //The Retrofit builder will have the client attached, in order to get connection logs
        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        RetroFitInterface service = retrofit.create(RetroFitInterface.class);

        service.post(userName, gId, thumbnailUrl, clubs, batch, emailId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            dialog.cancel();
                            TextView textView = (TextView) findViewById(R.id.text_view);
                            textView.setText(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }
}
