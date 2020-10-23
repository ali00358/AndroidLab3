package com.example.androidlab2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WeatherForecast extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        ProgressBar pBar = findViewById(R.id.progressBar);
        pBar.setVisibility(View.VISIBLE);

        ForecastQuery req = new ForecastQuery();
        req.execute("http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric");  //Type 1
    }

    @SuppressLint("StaticFieldLeak")
    private class ForecastQuery extends AsyncTask<String, Integer, String>{

        String uv;
        String min;
        String max;
        String currTemp;
        String icon;
        Bitmap bm;

        @Override
        protected String doInBackground(String... args) {
            try{
                URL url = new URL(args[0]);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream response = urlConnection.getInputStream();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( response  , "UTF-8");

                int eventType = xpp.getEventType();

                while(eventType != XmlPullParser.END_DOCUMENT){
                    if(eventType == XmlPullParser.START_TAG){

                        if(xpp.getName().equals("temperature")){
                            min = xpp.getAttributeValue(null, "min");
                            publishProgress(25);
                            max = xpp.getAttributeValue(null, "max");
                            publishProgress(50);
                            currTemp = xpp.getAttributeValue(null, "value");
                            publishProgress(75);
                        }else if(xpp.getName().equals("weather")){

                            icon = xpp.getAttributeValue(null, "icon");
                        }
                    }
                    eventType = xpp.next();
                }

                URL urlForUV = new URL("http://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=45.348945&lon=-75.759389");
                urlConnection = (HttpURLConnection) urlForUV.openConnection();
                response = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8), 8);
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line).append("\n");
                }
                String result = sb.toString();

                JSONObject uvReport = new JSONObject(result);
                uv = String.valueOf(uvReport.getDouble("value"));

                String iconUrl = "http://openweathermap.org/img/w/";
                iconUrl += icon;
                iconUrl += ".png";

                String imageFile = icon + ".png";

                if(fileExistance(imageFile)){
                    Log.e("File Existance", "File already exists");
                    FileInputStream fis = null;
                    try {
                        fis = openFileInput(imageFile);
                    }catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    bm = BitmapFactory.decodeStream(fis);

                    assert fis != null;
                    fis.close();
                }else {
                    Log.e("File Existance", "File doesn't exist");
                    URL urlForIcon = new URL(iconUrl);
                    urlConnection = (HttpURLConnection) urlForIcon.openConnection();
                    urlConnection.connect();
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == 200) {
                        bm = BitmapFactory.decodeStream(urlConnection.getInputStream());
                    }

                    FileOutputStream outputStream = openFileOutput(icon + ".png", Context.MODE_PRIVATE);
                    bm.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                    outputStream.flush();
                    outputStream.close();
                }

                publishProgress(100);


            } catch (Exception e) {
                e.printStackTrace();
            }

            return "Min temp: " + min + "\n Max temp: " + max + "\n Current temp: " + currTemp + "\n Icon: " + icon;
        }

        public void onProgressUpdate(Integer ... args)
        {
            ProgressBar pBar = findViewById(R.id.progressBar);
            pBar.setVisibility(View.VISIBLE);
            pBar.setProgress(args[0]);
        }

        public void onPostExecute(String fromDoInBackground)
        {
            TextView current = findViewById(R.id.currentTemp);
            current.setText("Temperature right now: " + currTemp);

            TextView minTemp = findViewById(R.id.minTemp);
            minTemp.setText("Min Temperature: " + min);

            TextView maxTemp = findViewById(R.id.maxTemp);
            maxTemp.setText("Max Temperature: " + max);

            TextView uvRating = findViewById(R.id.uvRating);
            uvRating.setText("UV ratings: " + uv);

            ImageView img = findViewById(R.id.currentWeather);
            img.setImageBitmap(bm);

            ProgressBar pBar = findViewById(R.id.progressBar);
            pBar.setVisibility(View.INVISIBLE);

        }

        boolean fileExistance(String fname){
            File file = getBaseContext().getFileStreamPath(fname);
            return file.exists();
        }
    }
}