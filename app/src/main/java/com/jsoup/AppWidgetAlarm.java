package com.jsoup;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.Objects;

public class AppWidgetAlarm
{
    private final int ALARM_ID = 0;
    private final int INTERVAL_MILLIS = 60000;

    private Context mContext;


    public AppWidgetAlarm(Context context)
    {
        mContext = context;
    }


    @SuppressLint("ShortAlarm")
    public void startAlarm(Context context)
    {
        Log.w("Alarm", "start alarm");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 0);
        long oneMinute = calendar.getTimeInMillis();

        Intent alarmIntent = new Intent(AppWidget.ACTION_AUTO_UPDATE);
        /*Intent alarmIntent = new Intent(mContext, AppWidget.class);
        alarmIntent.setAction(AppWidget.ACTION_AUTO_UPDATE);*/
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // RTC does not wake the device up
        Objects.requireNonNull(alarmManager).setRepeating(AlarmManager.RTC, oneMinute, INTERVAL_MILLIS, pendingIntent);
    }


    public void stopAlarm(Context context)
    {
        Log.w("Alarm", "stop alarm");
        Intent alarmIntent = new Intent(AppWidget.ACTION_AUTO_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Objects.requireNonNull(alarmManager).cancel(pendingIntent);
    }
}
