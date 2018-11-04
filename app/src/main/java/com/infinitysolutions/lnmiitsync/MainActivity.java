package com.infinitysolutions.lnmiitsync;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();

        getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.navBarColor));

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.simpleRecyclerView);
        List<String> list = new ArrayList<String>();
        list.add("tomato");
        list.add("ring");
        list.add("toe ring");
        list.add("doll");
        list.add("house");
        list.add("pen");
        list.add("USB drive");
        list.add("helmet");
        list.add("toothpaste");
        list.add("socks");

        ClubsRecyclerViewAdapter adapter = new ClubsRecyclerViewAdapter(list);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(manager);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            Log.d(TAG, "No login found");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST_CODE);
        }else{
            ImageView profileImageView = (ImageView)findViewById(R.id.profile_image_view);
            profileImageView.setClipToOutline(true);
            String profileImageUrl;
            try {
                profileImageUrl = account.getPhotoUrl().toString();
            }catch (NullPointerException e){
                e.printStackTrace();
                profileImageUrl = "NullPhoto";
            }
            setUserDetails(profileImageUrl,account.getDisplayName(),account.getEmail());
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
                setUserDetails(thumbnailUrl,userName,emailId);

                //Saving user data to sharedPrefs
                SharedPreferences sharedPrefs = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(SHARED_PREF_USERNAME, userName);
                editor.putString(SHARED_PREF_EMAIL_ID, emailId);
                editor.putString(SHARED_PREF_GID, gId);
                editor.putString(SHARED_PREF_THUMBNAIL_URL, thumbnailUrl);
                Set<String> clubsSet = new HashSet<String>(Arrays.asList(clubs));
                editor.putStringSet(SHARED_PREF_CLUBS,clubsSet);
                editor.apply();

                //Only register user if not already registered
                if (!intent.hasExtra("isRegistered")) {
                    sendDataToServer(userName, gId, thumbnailUrl, clubs, batch, emailId);
                }
            } else {
                //Ask again to login
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.app_name,R.string.app_name);
        drawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("Events");

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
                        dialog.cancel();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }

    public void setAsSelected(View view){
        view.setSelected(true);
    }

    private void setUserDetails(String profileImageUrl, String userName,String emailId){
        Glide.with(this)
                .load(profileImageUrl)
                .apply(new RequestOptions().placeholder(R.drawable.default_profile_photo))
                .into((ImageView)findViewById(R.id.profile_image_view));
        TextView nameTextView = (TextView)findViewById(R.id.name_text_view);
        nameTextView.setText(userName);
        TextView rollNoTextView = (TextView)findViewById(R.id.roll_no_text_view);
        rollNoTextView.setText(emailId);
    }
}
