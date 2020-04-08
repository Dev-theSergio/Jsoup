package com.jsoup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private TextView text1;
    private TextView text2;
    private TextView text3;
    private TextView text4;
    private TextView text5;
    private TextView text6;

    private TextView text21;
    private TextView text31;
    private TextView text51;
    private TextView text61;

    private TextView titleCountry;

    String countryName;

    SharedPreferences sPrefCountry;

    String subtitlePlus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text1 = findViewById(R.id.tvCases);
        text2 = findViewById(R.id.tvDeaths);
        text3 = findViewById(R.id.tvRecovered);

        text4 = findViewById(R.id.tvCasesRus);
        text5 = findViewById(R.id.tvDeathsRus);
        text6 = findViewById(R.id.tvRecoveredRus);

        text31 = findViewById(R.id.tvNewDeaths);
        text21 = findViewById(R.id.tvNewCases);

        text51 = findViewById(R.id.tvNewCasesRus);
        text61 = findViewById(R.id.tvNewDeathsRus);

        titleCountry = findViewById(R.id.tvCountry);


        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    ClickMe();
                    while (!isInterrupted()){
                        Thread.sleep(10000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ClickMe();
                            }
                        });
                    }
                }catch (InterruptedException e){
                    Log.e("Thread", "error");
                }

                // super.run();
            }
        };

        thread.start();



        titleCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogSend = new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
                LayoutInflater inflater = getLayoutInflater();
                @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.counrty_dialog, null);

                final EditText editText = view.findViewById(R.id.etCountryName);

                alertDialogSend.setView(view);
                alertDialogSend.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    if (!editText.getText().toString().replace(" ", "").equals("")) {
                                        String etCountry = Objects.requireNonNull(editText.getText()).toString();
                                        titleCountry.setText(etCountry);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                            }
                        });
                alertDialogSend.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                //alertDialog = alertDialogSend.create();
                alertDialogSend.show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ClickMe();
                    }
                });
            }
        });

    }

    public void getCountry(){
        //String country = getApplicationContext().getResources().getConfiguration().locale.getDisplayCountry();

        String country_name = null;
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Geocoder geocoder = new Geocoder(getApplicationContext());
        for(String provider: Objects.requireNonNull(lm).getAllProviders()) {
            @SuppressWarnings("ResourceType") Location location = lm.getLastKnownLocation(provider);
            if(location!=null) {
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if(addresses != null && addresses.size() > 0) {
                        country_name = addresses.get(0).getCountryName();
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Toast.makeText(getApplicationContext(), country_name, Toast.LENGTH_LONG).show();

        Log.w("Country", Objects.requireNonNull(country_name));
    }

    void saveText() {
        sPrefCountry = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPrefCountry.edit();
        ed.putString("SAVED_TEXT", titleCountry.getText().toString());
        ed.apply();
        //Toast.makeText(this, "Country saved", Toast.LENGTH_SHORT).show();
    }

    void loadText() {
        sPrefCountry = getPreferences(MODE_PRIVATE);
        String savedText = sPrefCountry.getString("SAVED_TEXT", "");
        titleCountry.setText(savedText);
        //Toast.makeText(this, "Country loaded", Toast.LENGTH_SHORT).show();
    }


    public void ClickMe() {
        MyTask mt = new MyTask();
        mt.execute();

        countryName = titleCountry.getText().toString().toLowerCase();
        if(countryName.equals("usa")){
            countryName = "us";
        }else if(countryName.equals("south korea")){
            countryName = "south-korea";
        }else if(countryName.equals("saudi arabia")) {
            countryName = "saudi-arabia";
        }else if(countryName.equals("united arab emirates")||countryName.equals("uae")){
            countryName = "united-arab-emirates";
        }else if(countryName.equals("dominican republic")){
            countryName = "dominican-republic";
        }else if(countryName.equals("south africa")){
            countryName = "south-africa";
        }else if(countryName.equals("new zealand")) {
            countryName = "new-zealand";
        }else{
            //Toast.makeText(MainActivity.this, "Country not found", Toast.LENGTH_SHORT).show();

        }
    }


    class MyTask extends AsyncTask<Void, Void, Void> {

        String subtitle;        //Тут храним значение element


        String subtitleRus;

        @Override
        protected Void doInBackground(Void... params) {

            Document doc = null;            //Здесь хранится будет разобранный html документ
            Document docRus = null;            //Здесь хранится будет разобранный html документ

            Elements masthead = null;
            Elements mastheadPlus = null;

            Elements mastheadRus = null;
            try {
                //Считываем заглавную страницу https://www.worldometers.info/coronavirus/

                doc = Jsoup.connect("https://www.worldometers.info/coronavirus/").get();



                docRus = Jsoup.connect("https://www.worldometers.info/coronavirus/country/" + countryName + "/" ).get();

                masthead = doc.select("div.maincounter-number");
                mastheadPlus = doc.select("div.main_table_countries_div"); //.select("total_row_world odd"); //    #main_table_countries_today > tbody:nth-child(2) > tr.total_row_world.odd
                //mastheadPlus = doc.getAllElements();
                //Log.w("PLUS11", mastheadPlus.text().substring(1,10));


                mastheadRus = docRus.select("div.maincounter-number");

            } catch (IOException e) {
                //Если не получилось считать
                e.printStackTrace();
            }


            //Если всё считалось, что вытаскиваем из считанного html документа заголовок
            if (doc != null && docRus != null){
                subtitle = masthead.text();
                subtitlePlus = mastheadPlus.text();
                subtitleRus = mastheadRus.text();
            }else{
                subtitle = "Ошибка";
                subtitlePlus = "Ошибка";
                subtitleRus = "Ошибка";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (subtitlePlus == null){
                Log.w("PLUS1", "empty");
            }else{
                /*String s = subtitlePlus.substring(subtitlePlus.indexOf("World ", 0));
                Log.w("World_s", s);

                String ru = subtitlePlus.substring(subtitlePlus.indexOf("Russia ", 0));
                Log.w("World_ru", ru);*/

                String[] separatedAll = subtitlePlus.substring(subtitlePlus.indexOf("World ")).split(" ");
                if(separatedAll[2].substring(0,1).equals("+")){
                    text21.setText(separatedAll[2].replace(",", " "));
                }else{
                    text21.setText("");
                }
                if(separatedAll[4].substring(0,1).equals("+")){
                    text31.setText(separatedAll[4].replace(",", " "));
                }else{
                    text31.setText("");
                }
                /*text21.setText(separatedAll[2].replace(",", " "));
                text31.setText(separatedAll[4].replace(",", " "));*/

                String[] separatedRu = subtitlePlus.substring(subtitlePlus.indexOf(titleCountry.getText().toString())).split(" ");
                Log.w("Check",titleCountry.getText().toString());
                Log.w("Check[]", Arrays.toString(separatedRu));
                if(separatedRu[2].substring(0,1).equals("+")){
                    text51.setText(separatedRu[2].replace(",", " "));
                }else{
                    text51.setText("");
                }
                if(separatedRu[4].substring(0,1).equals("+")){
                    text61.setText(separatedRu[4].replace(",", " "));
                }else{
                    text61.setText("");
                }
                /*Log.w("Check",separatedRu[2].substring(0,1));
                Log.w("Check",separatedAll[2].substring(0,1));*/

            }

            String[] separated = subtitle.split(" ");
            String[] separatedRus = subtitleRus.split(" ");

            if(separated.length > 0){
                text2.setCompoundDrawablePadding(10 + (separated[0].length() - separated[1].length()) * 20);
                text3.setCompoundDrawablePadding(10 + (separated[0].length() - separated[2].length()) * 20);
            }

            text1.setText(separated[0].replace(",", " "));
            text2.setText(separated[1].replace(",", " "));
            text3.setText(separated[2].replace(",", " "));

            text4.setText(separatedRus[0].replace(",", " "));
            text5.setText(separatedRus[1].replace(",", " "));
            text6.setText(separatedRus[2].replace(",", " "));

        }
    }

}