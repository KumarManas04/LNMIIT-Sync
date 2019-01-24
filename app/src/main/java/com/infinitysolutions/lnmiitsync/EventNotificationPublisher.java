package com.infinitysolutions.lnmiitsync;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_NAME;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_NOTIFY_BEFORE;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_NOTIFY_EVENTS;

public class EventNotificationPublisher extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("EP", "Event Publisher", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Used to display notifications of events that user chose to be notified about.");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "EP");
        if (intent.hasExtra("title")) {
            builder.setContentTitle(intent.getStringExtra("title"));
        } else {
            builder.setContentTitle("");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        String startTimeString = sdf.format(new Date(intent.getLongExtra("startTime", 0)));
        String notificationText;
        String bigNotificationText;
        if (intent.getLongExtra("endTime", 0) == 0) {
            notificationText = startTimeString + " | " + intent.getStringExtra("venue");
            bigNotificationText = "Time: " + startTimeString + "\nVenue: " + intent.getStringExtra("venue");
        } else {
            String endTimeString = sdf.format(new Date(intent.getLongExtra("endTime", 0)));
            notificationText = startTimeString + "-" + endTimeString + " | " + intent.getStringExtra("venue");
            bigNotificationText = "Time: " + startTimeString + "-" + endTimeString + "\nVenue: " + intent.getStringExtra("venue");
        }

        builder.setContentText(notificationText);
        builder.setColor(Color.parseColor("#3498DB"));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(bigNotificationText));
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_large_icon));
        builder.setSmallIcon(R.drawable.notification_small_icon);

        Intent contentIntent = new Intent(intent);
        contentIntent.setClass(context, EventDetailsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 101, contentIntent, PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(0, builder.build());

        cancelAllAlarms(context);

        SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        String notifyEventsJson = sharedPrefs.getString(SHARED_PREF_NOTIFY_EVENTS, "{}");
        Map<String, Event> notifyEventsMap = null;
        if (notifyEventsJson.equals("{}")) {
            notifyEventsMap = new HashMap<String, Event>();
        } else {
            Type type = new TypeToken<HashMap<String, Event>>() {
            }.getType();
            try {
                notifyEventsMap = new Gson().fromJson(notifyEventsJson, type);
            }catch(JsonSyntaxException e){
                e.printStackTrace();
            }
        }

        if (notifyEventsMap != null) {
            if (notifyEventsMap.size() != 0) {
                if (intent.hasExtra("id")) {
                    notifyEventsMap.remove(intent.getStringExtra("id"));
                }
                if (notifyEventsMap.size() != 0) {
                    setEventAlarm(context, getEarliestEvent(notifyEventsMap));
                }
            }

            final SharedPreferences.Editor editor = sharedPrefs.edit();
            String hashMapJson = new Gson().toJson(notifyEventsMap);
            editor.putString(SHARED_PREF_NOTIFY_EVENTS, hashMapJson);
            editor.commit();
        }
    }

    private Event getEarliestEvent(Map<String, Event> notifyEventsMap) {
        Map.Entry<String, Event> min = null;
        for (Map.Entry<String, Event> entry : notifyEventsMap.entrySet()) {
            if (min == null || min.getValue().getStartTime() > entry.getValue().getStartTime()) {
                min = entry;
            }
        }

        return min.getValue();
    }

    private void setEventAlarm(Context context, Event alarmEvent) {
        Intent intent = new Intent(context, EventNotificationPublisher.class);
        intent.putExtra("title", alarmEvent.getEventTitle());
        intent.putExtra("startTime", alarmEvent.getStartTime());
        intent.putExtra("endTime", alarmEvent.getEndTime());
        intent.putExtra("venue", alarmEvent.getVenue());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 123, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        int notifyBefore = sharedPrefs.getInt(SHARED_PREF_NOTIFY_BEFORE, 0);
        notifyBefore = notifyBefore * 60000;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmEvent.getStartTime() - 19800000 - notifyBefore, pendingIntent);
    }

    public void cancelAllAlarms(Context context) {
        Intent intent = new Intent(context, EventNotificationPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 123, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
