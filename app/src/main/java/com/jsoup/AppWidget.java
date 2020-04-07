package com.jsoup;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Objects;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {

    public static final String ACTION_AUTO_UPDATE = "AUTO_UPDATE";

    static String wRecovered = null;
    static String wCases = null;
    static String wDeaths = null;

    static String wNewCases = null;
    static String wNewDeaths = null;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        new MyTask(views, appWidgetId, appWidgetManager).execute();

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    static class MyTask extends AsyncTask<Void, Void, Void> {
        String subtitle;
        String subtitlePlus;
        private RemoteViews views;
        private int WidgetID;
        private AppWidgetManager WidgetManager;

        public MyTask(RemoteViews views, int appWidgetID, AppWidgetManager appWidgetManager){
            this.views = views;
            this.WidgetID = appWidgetID;
            this.WidgetManager = appWidgetManager;
        }

        @Override
        protected Void doInBackground(Void... params) {

            Document doc = null;            //Здесь хранится будет разобранный html документ
            Document docRus = null;            //Здесь хранится будет разобранный html документ

            Elements masthead = null;
            Elements mastheadPlus = null;
            try {
                //Считываем заглавную страницу https://www.worldometers.info/coronavirus/
                doc = Jsoup.connect("https://www.worldometers.info/coronavirus/").get();
                masthead = doc.select("div.maincounter-number");
                mastheadPlus = doc.select("div.main_table_countries_div");
            } catch (IOException e) {
                //Если не получилось считать
                e.printStackTrace();
            }
            //Если всё считалось, что вытаскиваем из считанного html документа заголовок
            if (doc != null){
                subtitle = masthead.text();
                subtitlePlus = mastheadPlus.text();
            }else{
                subtitle = "Ошибка";
                subtitlePlus = "Ошибка";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            String[] separated = subtitle.split(" ");
            wRecovered = (separated[2].replace(",", " "));
            wCases = (separated[0].replace(",", " "));
            wDeaths = (separated[1].replace(",", " "));

            String[] separatedAll = subtitlePlus.substring(subtitlePlus.indexOf("World ", 0)).split(" ");
            wNewCases = (separatedAll[2].replace(",", " "));
            wNewDeaths = (separatedAll[4].replace(",", " "));



            views.setTextViewText(R.id.appwidget_text_recovered, wRecovered);
            views.setTextViewText(R.id.appwidget_text_cases, wCases);
            views.setTextViewText(R.id.appwidget_text_deaths, wDeaths);

            views.setTextViewText(R.id.appwidget_text_NewCases, wNewCases);
            views.setTextViewText(R.id.appwidget_text_NewDeaths, wNewDeaths);

            WidgetManager.updateAppWidget(WidgetID, views);
        }
    }



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        // start alarm
        AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context.getApplicationContext());
        appWidgetAlarm.startAlarm();
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        // stop alarm only if all widgets have been disabled
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidgetComponentName = new ComponentName(context.getPackageName(),getClass().getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidgetComponentName);
        if (appWidgetIds.length == 0) {
            // stop alarm
            AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context.getApplicationContext());
            appWidgetAlarm.stopAlarm();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        RemoteViews views;
        AppWidgetManager WidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context.getPackageName(),getClass().getName());
        int[] WidgetID = WidgetManager.getAppWidgetIds(componentName);


        super.onReceive(context, intent);

        if(Objects.equals(intent.getAction(), ACTION_AUTO_UPDATE))
        {
            AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context.getApplicationContext());
            appWidgetAlarm.startAlarm();
            Toast.makeText(context, "Upd", Toast.LENGTH_SHORT).show();
            for (int appWidgetId : WidgetID) {
                updateAppWidget(context, WidgetManager, appWidgetId);
            }
        }

    }

}

