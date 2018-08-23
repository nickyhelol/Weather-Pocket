package com.example.nickyhe.weatherpocket;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText cityEditText;
    private TextView weatherTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = findViewById(R.id.cityEditText);
        weatherTextView = findViewById(R.id.weatherTextView);
    }

    public class weatherDownloader extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... urls) {

            URL url;
            HttpURLConnection urlConnection;
            String result = "";

            System.out.println(urls[0]);
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());

                int data = reader.read();

                while(data != -1)
                {
                    char current = (char) data;

                    result += current;

                    data = reader.read();
                }

                return  result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String message = "";

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray array = jsonObject.getJSONArray("weather");

                for(int i =0;i<array.length();i++)
                {
                    //get each object in the JsonArray
                    JSONObject object = array.getJSONObject(i);

                    String main = object.getString("main");
                    String description = object.getString("description");

                    message += main+": "+description+"\n";
                }

                //Get the main JsonObject
                JSONObject mainObject = jsonObject.getJSONObject("main");
                String temp = mainObject.getString("temp") + "˚F";
                String pressure = mainObject.getString("pressure")+" hPa";
                String humidity = mainObject.getString("humidity")+"%";

                message += "Temp: "+temp+"\n"+
                        "Pressure: "+pressure+"\n"+
                        "Humidity: "+humidity;

                weatherTextView.setText(message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void findWeather(View view)
    {

        String city = cityEditText.getText().toString();

        weatherDownloader downloader = new weatherDownloader();
        downloader.execute("http://api.openweathermap.org/data/2.5/weather?q="
                +city+"&appid=cdc325a1121a5e7e1445559f03712d5e");
    }
}
