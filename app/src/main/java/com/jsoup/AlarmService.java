package com.jsoup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AlarmService extends Service {

    private final int ALARM_ID = 0;
    private final int INTERVAL_MILLIS = 60000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        buildUpdate(getApplicationContext());
        return super.onStartCommand(intent, flags, startId);
    }

    private void buildUpdate(final Context context)
    {
        new Thread(new Runnable() {
            public void run() {

                String lastUpdated = DateFormat.format("hh:mm:ss", new Date()).toString();

                //RemoteViews view = new RemoteViews(getPackageName(), R.layout.app_widget);
                //view.setTextViewText(R.id.btn_widget, lastUpdated);

                Log.i("Service", lastUpdated);

//        ComponentName thisWidget = new ComponentName(this, AppWidget.class);
//        AppWidgetManager manager = AppWidgetManager.getInstance(this);
//        manager.updateAppWidget(thisWidget, view);


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
        }).start();

    }
}
