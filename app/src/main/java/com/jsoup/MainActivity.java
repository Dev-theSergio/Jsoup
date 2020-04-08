package com.jsoup;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;


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


        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    ClickMe(text1);
                    while (!isInterrupted()){
                        Thread.sleep(10000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ClickMe(text1);
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

    }




    public void ClickMe(View v) {
        MyTask mt = new MyTask();
        mt.execute();
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
                docRus = Jsoup.connect("https://www.worldometers.info/coronavirus/country/russia/").get();

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

                String[] separatedAll = subtitlePlus.substring(subtitlePlus.indexOf("World ",0)).split(" ");
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

                String[] separatedRu = subtitlePlus.substring(subtitlePlus.indexOf("Russia ",0)).split(" ");
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