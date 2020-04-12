package com.jsoup;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

    private TextView title;
    //private TextView titleCountry;

    String countryName;

    Spinner spCountry;

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

        title = findViewById(R.id.title);
        //titleCountry = findViewById(R.id.tvCountry);

        spCountry = findViewById(R.id.spCountry);

        if(isOnline()) {
            title.setText("Coronavirus live update");
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        //ClickMe();
                        while (!isInterrupted()) {
                            Thread.sleep(60000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ClickMe();
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        Log.e("Thread", "error");
                    }

                    // super.run();
                }
            };

            thread.start();

            //////////////////     Spinner country adapter and listener     ////////////////////////

            ArrayAdapter<CharSequence> spinnerArrayAdapter = ArrayAdapter.createFromResource(this,
                    R.array.string_array_country, android.R.layout.simple_spinner_item); //selected item will look like a spinner set from XML
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCountry.setAdapter(spinnerArrayAdapter);
            loadPosition();

            spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    savePosition();
                    ClickMe();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {

                }

            });

            ////////////////////////////////////////////////////////////////////////////////////////




            /*titleCountry.setOnClickListener(new View.OnClickListener() {
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
                            text4.setText("loading…");
                            text5.setText("loading…");
                            text6.setText("loading…");
                            text51.setText("");
                            text61.setText("");
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
            });*/

        }else{
            title.setText("No internet connection");
            Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return Objects.requireNonNull(Objects.requireNonNull(connectivityManager).getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState() == NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState() == NetworkInfo.State.CONNECTED;
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

    void savePosition() {
        sPrefCountry = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPrefCountry.edit();
        ed.putInt("SAVED_POSITION", spCountry.getSelectedItemPosition());
        ed.apply();
        //Toast.makeText(this, "Country saved", Toast.LENGTH_SHORT).show();
    }

    void loadPosition() {
        sPrefCountry = getPreferences(MODE_PRIVATE);
        int savedPosition = sPrefCountry.getInt("SAVED_POSITION", 0);
        spCountry.setSelection(savedPosition);
        //Toast.makeText(this, "Selected is " + spCountry.getSelectedItem().toString() + ".", Toast.LENGTH_SHORT).show();
    }


    public void ClickMe() {
        MyTask mt = new MyTask();
        mt.execute();

        //countryName = titleCountry.getText().toString().toLowerCase();
        countryName = spCountry.getSelectedItem().toString().toLowerCase();

        switch (countryName) {
            case "usa":
                countryName = "us";
                break;
            case "s. korea":
                countryName = "south-korea";
                break;
            case "saudi arabia":
                countryName = "saudi-arabia";
                break;
            case "uae":
                countryName = "united-arab-emirates";
                break;
            case "dominican republic":
                countryName = "dominican-republic";
                break;
            case "south africa":
                countryName = "south-africa";
                break;
            case "new zealand":
                countryName = "new-zealand";
                break;
            case "antigua and barbuda":
                countryName = "antigua-and-barbuda";
                break;
            case "bosnia and herzegovina":
                countryName = "bosnia-and-herzegovina";
                break;
            case "burkina faso":
                countryName = "burkina-faso";
                break;
            case "british virgin islands":
                countryName = "british-virgin-islands";
                break;
            case "cabo verde":
                countryName = "cabo-verde";
                break;
            case "caribbean netherlands":
                countryName = "caribbean-netherlands";
                break;
            case "cayman islands":
                countryName = "cayman-islands";
                break;
            case "channel islands":
                countryName = "channel-islands";
                break;
            case "costa rica":
                countryName = "costa-rica";
                break;
            case "Curaçao":
                countryName = "curacao";
                break;
            case "drc":
                countryName = "democratic-republic-of-the-congo";
                break;
            case "el salvador":
                countryName = "el-salvador";
                break;
            case "equatorial guinea":
                countryName = "equatorial-guinea";
                break;
            case "":
                countryName = "";
                break;
            case "faeroe islands":
                countryName = "faeroe-islands";
                break;
            case "falkland islands":
                countryName = "falkland-islands-malvinas";
                break;
            case "french guiana":
                countryName = "french-guiana";
                break;
            case "french polynesia":
                countryName = "french-polynesia";
                break;
            case "hong kong":
                countryName = "china-hong-kong-sar";
                break;
            case "isle of man":
                countryName = "isle-of-man";
                break;
            case "ivory coast":
                countryName = "cote-d-ivoire";
                break;
            case "new caledonia":
                countryName = "new-caledonia";
                break;
            case "north macedonia":
                countryName = "macedonia";
                break;
            case "palestine":
                countryName = "state-of-palestine";
                break;
            case "papua new guinea":
                countryName = "papua-new-guinea";
                break;
            case "réunion":
                countryName = "reunion";
                break;
            case "saint kitts and nevis":
                countryName = "saint-kitts-and-nevis";
                break;
            case "saint lucia":
                countryName = "saint-lucia";
                break;
            case "saint martin":
                countryName = "saint-martin";
                break;
            case "saint pierre miquelon":
                countryName = "saint-pierre-and-miquelon";
                break;
            case "san marino":
                countryName = "san-marino";
                break;
            case "sao tome and principe":
                countryName = "sao-tome-and-principe";
                break;
            case "sierra leone":
                countryName = "sierra-leone";
                break;
            case "sint maarten":
                countryName = "sint-maarten";
                break;
            case "south sudan":
                countryName = "south-sudan";
                break;
            case "sri lanka":
                countryName = "sri-lanka";
                break;
            case "st. barth":
                countryName = "saint-barthelemy";
                break;
            case "st. vincent grenadines":
                countryName = "saint-vincent-and-the-grenadines";
                break;
            case "timor-leste":
                countryName = "timor-leste";
                break;
            case "trinidad and tobago":
                countryName = "trinidad-and-tobago";
                break;
            case "turks and caicos":
                countryName = "turks-and-caicos-islands";
                break;
            case "western sahara":
                countryName = "western-sahara";
                break;
            case "guinea-bissau":
                countryName = "guinea-bissau";
                break;
            case "car":
                countryName = "central-african-republic";
                break;
            case "vatican city":
                countryName = "holy-see";
                break;
            case "czechia":
                countryName = "czech-republic";
                break;
            default:
                //Toast.makeText(MainActivity.this, "Country not found", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    class MyTask extends AsyncTask<Void, Void, Void> {

        String subtitle;        //Тут храним значение element


        String subtitleCountry;

        @Override
        protected Void doInBackground(Void... params) {

            Document doc = null;            //Здесь хранится будет разобранный html документ
            Document docCountry = null;     //Здесь хранится будет разобранный html документ

            Elements masthead = null;
            Elements mastheadPlus = null;

            Elements mastheadCountry = null;
            if(isOnline()) {

                    try {
                        //Считываем заглавную страницу https://www.worldometers.info/coronavirus/

                        doc = Jsoup.connect("https://www.worldometers.info/coronavirus/").get();
                        masthead = doc.select("div.maincounter-number");
                        mastheadPlus = doc.select("div.main_table_countries_div"); //    #main_table_countries_today > tbody:nth-child(2) > tr.total_row_world.odd

                        if(spCountry.getSelectedItemPosition() !=0){
                            docCountry = Jsoup.connect("https://www.worldometers.info/coronavirus/country/" + countryName + "/").get();
                            mastheadCountry = docCountry.select("div.maincounter-number");
                            if(docCountry != null){
                                subtitleCountry = mastheadCountry.text();
                            }
                        }else {
                            //docCountry = null;
                            subtitleCountry = "Error";
                        }

                    } catch (IOException e) {
                        //Если не получилось считать
                        e.printStackTrace();
                    }


                        //Если всё считалось, что вытаскиваем из считанного html документа заголовок
                        if (doc != null) {
                            subtitle = masthead.text();
                            subtitlePlus = mastheadPlus.text();
                        }else {
                            subtitle = "Error";
                            subtitlePlus = "Error";
                        }

            }else{
                subtitleCountry = "Error";
                subtitle = "Error";
                subtitlePlus = "Error";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if(!subtitlePlus.equals("Error")){
                String[] separatedAll = subtitlePlus.substring(subtitlePlus.indexOf("World ")).split(" ");
                if (separatedAll[2].substring(0, 1).equals("+")) {
                    text21.setText(separatedAll[2].replace(",", " "));
                } else {
                    text21.setText("");
                }
                if (separatedAll[4].substring(0, 1).equals("+")) {
                    text31.setText(separatedAll[4].replace(",", " "));
                } else {
                    text31.setText("");
                }

                if(spCountry.getSelectedItemPosition() != 0){
                    String[] separatedCountry = subtitlePlus.substring(subtitlePlus.indexOf(spCountry.getSelectedItem().toString())).split(" ");
                    Log.w("Check", spCountry.getSelectedItem().toString());
                    Log.w("Check[]", Arrays.toString(separatedCountry));
                    if (separatedCountry[2].substring(0, 1).equals("+")) {
                        text51.setText(separatedCountry[2].replace(",", " "));
                    } else {
                        text51.setText("");
                    }
                    if (separatedCountry[4].substring(0, 1).equals("+")) {
                        text61.setText(separatedCountry[4].replace(",", " "));
                    } else {
                        text61.setText("");
                    }
                }else{
                    text51.setText("");
                    text61.setText("");
                }


            }else {
                text21.setText("");
                text31.setText("");
            }

            if(!subtitle.equals("Error")){
                String[] separated = subtitle.split(" ");

                if (separated.length > 0) {
                    text2.setCompoundDrawablePadding(10 + (separated[0].length() - separated[1].length()) * 20);
                    text3.setCompoundDrawablePadding(10 + (separated[0].length() - separated[2].length()) * 20);
                }

                text1.setText(separated[0].replace(",", " "));
                text2.setText(separated[1].replace(",", " "));
                text3.setText(separated[2].replace(",", " "));
                }else {

                text1.setText("loading");
                text2.setText("loading");
                text1.setText("loading");
                text21.setText("");
                text31.setText("");
            }

            if(!subtitleCountry.toString().equals("Error")){
                String[] separatedRus = subtitleCountry.split(" ");

                text4.setText(separatedRus[0].replace(",", " "));
                text5.setText(separatedRus[1].replace(",", " "));
                text6.setText(separatedRus[2].replace(",", " "));
            }else{
                Log.w("PLUS_post_exe_null", "empty");
                text4.setText("---");
                text5.setText("---");
                text6.setText("---");
                text51.setText("");
                text61.setText("");
            }




            /*if (subtitleCountry == null || subtitleCountry.equals("Error") ||
                    subtitle == null || subtitle.equals("Error")) {
                Log.w("PLUS1", "empty");
            } else {




            }*/
        }
    }

}