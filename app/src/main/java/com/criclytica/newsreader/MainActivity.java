package com.criclytica.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    ListView headlineView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        headlineView = findViewById(R.id.headlineView);

        try {
            DownloadJSON downloadJSON = new DownloadJSON();
            downloadJSON.execute("https://newsapi.org/v2/top-headlines?country=in&apiKey=c117d0af3ee44794bffac3058e44cfa7");
        } catch(Exception e) {
            Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public class DownloadJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL (urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();

                while(data != -1) {
                    char current = (char) data;
                    result += current;

                    data = inputStreamReader.read();
                }

                return result;

            } catch(Exception e) {
                Log.i("ERROR","Internet issue maybe.");
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);

                int totalResults = jsonObject.getInt("totalResults");
                String articles = jsonObject.getString("articles");

                JSONArray Articles = new JSONArray(articles);
                

            } catch(Exception e) {
                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}