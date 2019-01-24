package com.infinitysolutions.lnmiitsync;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.infinitysolutions.lnmiitsync.Fragments.EventsFragment;
import com.infinitysolutions.lnmiitsync.Fragments.WeekViewFragment;
import com.infinitysolutions.lnmiitsync.RetrofitResponses.EventResponse;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.BASE_URL;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_BATCH;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_CLUBS;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_EMAIL_ID;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_EVENTS_DATA;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_GID;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_LOGIN_TYPE;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_NAME;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_NOTIFY_BEFORE;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_NOTIFY_EVENTS;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_THUMBNAIL_URL;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_USERNAME;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.STUDENT_LOGIN;

@SuppressLint("LogNotTimber")
public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private int LOGIN_REQUEST_CODE = 105;
    private ActionBarDrawerToggle mDrawerToggle;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Map<String, Event> notifyEventsMap;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Signing up...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.cyan, R.color.indigo);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadEvents();
            }
        });

        SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        String gId = sharedPrefs.getString(SHARED_PREF_GID, "");

        if (gId.equals("")) {
            Log.d(TAG, "No login found");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST_CODE);
        } else {
            ImageView profileImageView = findViewById(R.id.profile_image_view);
            profileImageView.setClipToOutline(true);

            String profileImageUrl = sharedPrefs.getString(SHARED_PREF_THUMBNAIL_URL, "NullPhoto");

            setUserDetails(profileImageUrl, sharedPrefs.getString(SHARED_PREF_USERNAME, ""), sharedPrefs.getString(SHARED_PREF_EMAIL_ID, ""));
            sharedPrefs = this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
            if (sharedPrefs.contains(SHARED_PREF_EVENTS_DATA) && !sharedPrefs.getString(SHARED_PREF_EVENTS_DATA, "{}").equals("{}")) {
                loadTodayFragment();
                loadEvents();
            } else {
                loadEvents();
            }
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);

                switch (menuItem.getItemId()) {
                    case R.id.bnav_today_view:
                        collapsingToolbarLayout.setTitle("Today");
                        loadTodayFragment();
                        break;
                    case R.id.bnav_week_view:
                        collapsingToolbarLayout.setTitle("This week");
                        loadWeekFragment();
                        break;
                    case R.id.bnav_all_view:
                        collapsingToolbarLayout.setTitle("All");
                        loadAllFragment();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        String notifyEventsJson = sharedPrefs.getString(SHARED_PREF_NOTIFY_EVENTS, "{}");
        if (notifyEventsJson.equals("{}")) {
            notifyEventsMap = new HashMap<String, Event>();
        } else {
            Type type = new TypeToken<HashMap<String, Event>>() {
            }.getType();
            notifyEventsMap = new Gson().fromJson(notifyEventsJson, type);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dialog != null && dialog.isShowing()) {
            dialog.cancel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == LOGIN_REQUEST_CODE) {
            SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPrefs.edit();
            int loginType = sharedPrefs.getInt(SHARED_PREF_LOGIN_TYPE, 1);

            StringBuilder userName;
            if(intent.hasExtra("userName")) {
                userName = new StringBuilder(intent.getStringExtra("userName"));
            }else{
                userName = new StringBuilder();
            }
            String emailId = intent.getStringExtra("emailId");
            String gId = intent.getStringExtra("gId");
            String thumbnailUrl = intent.getStringExtra("thumbnailUrl");

            //Saving user data to sharedPrefs
            if(userName.toString().equals("")){
                userName.append("Unknown");
            }else{
                String userNameArray[] = userName.toString().split("\\s+");
                userName.setLength(0);
                if(userNameArray.length == 1)
                    Log.d(TAG,"Empty array");
                for (String userNamePart : userNameArray) {
                    userName.append(userNamePart.substring(0, 1).toUpperCase()).append(userNamePart.substring(1)).append(" ");
                }
            }
            userName.trimToSize();

            editor.putString(SHARED_PREF_USERNAME, userName.toString());
            editor.putString(SHARED_PREF_EMAIL_ID, emailId);
            editor.putString(SHARED_PREF_GID, gId);
            editor.putString(SHARED_PREF_THUMBNAIL_URL, thumbnailUrl);
            editor.putInt(SHARED_PREF_NOTIFY_BEFORE,10);

            if (loginType == STUDENT_LOGIN) {
                String clubs[] = intent.getStringArrayExtra("clubs");
                Set<String> clubsSet = new HashSet<String>(Arrays.asList(clubs));
                editor.putStringSet(SHARED_PREF_CLUBS, clubsSet);

                //Only register user if not already registered
                if (!intent.hasExtra("isRegistered")) {
                    sendDataToServer(userName.toString(), gId, thumbnailUrl, clubs, emailId);
                }
            } else {
                //Only register user if not already registered
                if (!intent.hasExtra("isRegistered")) {
                    sendDataToServer(userName.toString(), gId, thumbnailUrl, null, emailId);
                }
            }

            setUserDetails(thumbnailUrl, userName.toString(), emailId);
            editor.apply();
            loadEvents();
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

    private void loadEvents() {
        Log.d(TAG, "Load events called");
        mSwipeRefreshLayout.setRefreshing(true);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        RetroFitInterface service = retrofit.create(RetroFitInterface.class);
        Log.d(TAG, "Sending GET request...");
        service.getEvents().enqueue(new Callback<List<EventResponse>>() {
            @Override
            public void onResponse(Call<List<EventResponse>> call, Response<List<EventResponse>> response) {
                String jsonResponse = new Gson().toJson(response.body());
                SharedPreferences sharedPrefs = MainActivity.this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                final SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(SHARED_PREF_EVENTS_DATA, jsonResponse);
                editor.apply();
                mSwipeRefreshLayout.setRefreshing(false);
                BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
                CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
                switch (bottomNavigationView.getSelectedItemId()) {
                    case R.id.bnav_today_view:
                        collapsingToolbarLayout.setTitle("Today");
                        loadTodayFragment();
                        break;
                    case R.id.bnav_week_view:
                        collapsingToolbarLayout.setTitle("This week");
                        loadWeekFragment();
                        break;
                    case R.id.bnav_all_view:
                        collapsingToolbarLayout.setTitle("All");
                        loadAllFragment();
                        break;
                    default:
                        loadTodayFragment();
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<EventResponse>> call, Throwable t) {
                Log.d(TAG, "Error in response: " + t.getCause());
                mSwipeRefreshLayout.setRefreshing(false);
                BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
                switch (bottomNavigationView.getSelectedItemId()) {
                    case R.id.bnav_today_view:
                        loadTodayFragment();
                        break;
                    case R.id.bnav_week_view:
                        loadWeekFragment();
                        break;
                    case R.id.bnav_all_view:
                        loadAllFragment();
                        break;
                    default:
                        loadTodayFragment();
                        break;
                }
                Toast.makeText(MainActivity.this, "Couldn't load data.Server error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTodayFragment() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = sdf.format(Calendar.getInstance().getTime());
        Date date = new Date();
        try {
            date = sdf.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long startTime = date.getTime() + 19800000;
        long endTime = startTime + (86400000);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        EventsFragment eventsFragment = EventsFragment.newInstance();
        eventsFragment.setParams(startTime, endTime);
        transaction.replace(R.id.fragment_container, eventsFragment);
        transaction.commitAllowingStateLoss();
    }

    private void loadWeekFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        WeekViewFragment weekViewFragment = WeekViewFragment.newInstance();
        transaction.replace(R.id.fragment_container, weekViewFragment);
        transaction.commitAllowingStateLoss();
    }

    private void loadAllFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        EventsFragment eventsFragment = EventsFragment.newInstance();
        eventsFragment.setParams(0, 0);
        transaction.replace(R.id.fragment_container, eventsFragment);
        transaction.commitAllowingStateLoss();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("Today");
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#ffffff"));
        collapsingToolbarLayout.setExpandedTitleColor(Color.parseColor("#ffffff"));
    }

    private void sendDataToServer(String userName, String gId, String thumbnailUrl, String clubs[], String emailId) {
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

        service.post(userName, gId, thumbnailUrl, clubs, emailId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if(dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(MainActivity.this, "Couldn't contact server", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void settings(View view){
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }

    public void helpFeedback(View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // Set type to "email"
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {"infinitysolutionsv1.1@gmail.com"};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        // the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Help or Feedback");
        startActivity(Intent.createChooser(emailIntent, "Help or Feedback"));
    }

    public void developers(View view){
        Intent intent = new Intent(this, Developers.class);
        startActivity(intent);
    }

    public void logout(View view) {

        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to Logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPrefs = MainActivity.this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                        final SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putString(SHARED_PREF_EVENTS_DATA, "{}");
                        editor.putString(SHARED_PREF_USERNAME, "");
                        editor.putString(SHARED_PREF_EMAIL_ID, "");
                        editor.putString(SHARED_PREF_GID, "");
                        editor.putString(SHARED_PREF_THUMBNAIL_URL, "NullPhoto");
                        editor.putString(SHARED_PREF_BATCH, "");
                        editor.putStringSet(SHARED_PREF_CLUBS, null);
                        editor.putInt(SHARED_PREF_NOTIFY_BEFORE,10);
                        editor.apply();
                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .build();

                        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
                        googleSignInClient.signOut();
                        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                        drawerLayout.closeDrawers();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivityForResult(intent, LOGIN_REQUEST_CODE);
                    }
                })
                .setNegativeButton("No", null)
                .show();

    }

    private void setUserDetails(String profileImageUrl, String userName, String emailId) {
        final ImageView profileImageView = findViewById(R.id.profile_image_view);
        TextView altProfileImageView = findViewById(R.id.alt_profile_photo_view);

        if (profileImageUrl.equals("NullPhoto")) {

            profileImageView.setVisibility(View.GONE);
            altProfileImageView.setVisibility(View.VISIBLE);
            altProfileImageView.setBackgroundResource(R.drawable.round_image_view_background);
            altProfileImageView.setText(userName.substring(0, 1).toUpperCase());
        } else {
            profileImageView.setVisibility(View.VISIBLE);
            altProfileImageView.setVisibility(View.GONE);
            Glide.with(this)
                    .load(profileImageUrl)
                    .apply(new RequestOptions().placeholder(R.drawable.default_profile_photo))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            //Here we detect that image loading has occurred so clipToOutline to make imageView circular
                            profileImageView.setClipToOutline(true);
                            return false;
                        }
                    })
                    .into(profileImageView);
        }

        TextView nameTextView = findViewById(R.id.name_text_view);
        nameTextView.setText(userName);
        TextView rollNoTextView = findViewById(R.id.roll_no_text_view);
        rollNoTextView.setText(emailId);
    }

    public void enableSwipeToRefresh(boolean enable){
            mSwipeRefreshLayout.setEnabled(enable);
    }

    public boolean isBeingNotified(String id) {
        return notifyEventsMap.containsKey(id);
    }

    public void doNotNotify(String id) {
        notifyEventsMap.remove(id);
        if(notifyEventsMap.size() == 0){
            Intent intent = new Intent(this, EventNotificationPublisher.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 123, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }else{
            setEventAlarm(getEarliestEvent());
        }

        SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPrefs.edit();
        String hashMapJson = new Gson().toJson(notifyEventsMap);
        editor.putString(SHARED_PREF_NOTIFY_EVENTS, hashMapJson);
        editor.commit();
    }

    public void notifyEvent(String id, Event event) {
        notifyEventsMap.put(id, event);
        setEventAlarm(getEarliestEvent());

        SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPrefs.edit();
        String hashMapJson = new Gson().toJson(notifyEventsMap);
        editor.putString(SHARED_PREF_NOTIFY_EVENTS, hashMapJson);
        editor.commit();
    }

    private Event getEarliestEvent() {
        Map.Entry<String, Event> min = null;
        for (Map.Entry<String, Event> entry : notifyEventsMap.entrySet()) {
            if (min == null || min.getValue().getStartTime() > entry.getValue().getStartTime()) {
                min = entry;
            }
        }

        return min.getValue();
    }

    private void setEventAlarm(Event alarmEvent){
        Intent intent = new Intent(this, EventNotificationPublisher.class);
        intent.putExtra("id",alarmEvent.getId());
        intent.putExtra("title", alarmEvent.getEventTitle());
        intent.putExtra("description",alarmEvent.getEventDescription());
        intent.putExtra("startTime", alarmEvent.getStartTime());
        intent.putExtra("endTime", alarmEvent.getEndTime());
        intent.putExtra("venue", alarmEvent.getVenue());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 123, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
        int notifyBefore = sharedPrefs.getInt(SHARED_PREF_NOTIFY_BEFORE,0);
        notifyBefore = notifyBefore * 60000;
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmEvent.getStartTime() - 19800000 - notifyBefore, pendingIntent);
    }
}
