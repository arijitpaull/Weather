package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    TextView weatherTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        weatherTextView = findViewById(R.id.weatherTextView);
    }

    public void findWeather(View view) {
        String cityName = editText.getText().toString().trim();
        if(cityName.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter the name of a city", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            DownloadTask task = new DownloadTask();
            String encodedCityName = URLEncoder.encode(cityName, "UTF-8");
            task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=d5455a8db15e9d9bbb3bcf3fdb9ccb9f");
            InputMethodManager man = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            man.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Couldn't find weather", Toast.LENGTH_SHORT).show();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data!=-1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
            } catch(Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Couldn't find weather", Toast.LENGTH_SHORT).show();
                return null;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("Weather Info",weatherInfo);
                JSONArray arr = new JSONArray(weatherInfo);
                String message = "";
                for(int i=0;i<arr.length();i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String desc = jsonPart.getString("description");
                    if(!main.equals("") && !desc.equals("")) {
                        message += main +": "+ desc + "\n";
                    }
                }
                if(!message.equals("")) {
                    weatherTextView.setText(message);
                } else {
                    Toast.makeText(getApplicationContext(), "Couldn't find weather", Toast.LENGTH_SHORT).show();
                }
            } catch(Exception e) {
                Toast.makeText(getApplicationContext(), "Couldn't find weather", Toast.LENGTH_SHORT).show();
                e.printStackTrace();

            }
        }
    }
}