package com.example.nickyhe.weatherpocket;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private EditText cityEditText;
    private TextView weatherTextView;
    String weatherMsg;
    String city;

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

                weatherMsg = city+" weather:\n"+message;

                weatherTextView.setText(message);

            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(), "Invalid input!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void findWeather(View view)
    {

        city = cityEditText.getText().toString();

        //Hiding the Input keyboard
        InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityEditText.getWindowToken(), 0);

        try {

            //Format the city name
            String encodedCityName = URLEncoder.encode(city, "UTF-8");

            weatherDownloader downloader = new weatherDownloader();
            downloader.execute("http://api.openweathermap.org/data/2.5/weather?q="
                    +city+"&appid=cdc325a1121a5e7e1445559f03712d5e");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(View view)
    {
        Intent intent = new Intent(getApplicationContext(), SendMessageActivity.class);
        intent.putExtra("weather", weatherMsg);
        startActivity(intent);
    }
}
