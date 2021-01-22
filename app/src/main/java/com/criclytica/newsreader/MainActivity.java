package com.criclytica.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    ListView headlineView;
    static ArrayList<String> titles = new ArrayList<String>();
    static ArrayAdapter arrayAdapter;
    SQLiteDatabase articlesDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        headlineView = findViewById(R.id.headlineView);

        articlesDB= this.openOrCreateDatabase("Articles", MODE_PRIVATE, null);

        articlesDB.execSQL("CREATE TABLE IF NOT EXISTS articles (id INTEGER PRIMARY KEY, title VARCHAR, content VARCHAR)");

        try {
            DownloadJSON downloadJSON = new DownloadJSON();
            downloadJSON.execute("https://gnews.io/api/v4/top-headlines?country=in&lang=en&token=b8ddfa150c749547938d48d8358b558b");
        } catch(Exception e) {
            Log.i("Error", "BOOOOOOO");
            e.printStackTrace();
        }

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, titles);
        headlineView.setAdapter(arrayAdapter);
    }

    public class DownloadJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpsURLConnection urlConnection = null;

            try {
                url = new URL (urls[0]);
                urlConnection = (HttpsURLConnection) url.openConnection();

                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();

                while(data != -1) {
                    char current = (char) data;
                    result += current;

                    data = inputStreamReader.read();
                }
                JSONObject jsonObject = new JSONObject(result);
                Integer totalArticles = jsonObject.getInt("totalArticles");
                String articles = jsonObject.getString("articles");

                JSONArray Articles = new JSONArray(articles);
                int maxHeadlines = 10;

                articlesDB.execSQL("DELETE FROM articles");

                for(int i=0; i<maxHeadlines; i++) {
                    JSONObject articleObject = Articles.getJSONObject(i);
                    String title = articleObject.getString("title");
                    String articleURL = articleObject.getString("url");

                    url = new URL (articleURL);
                    urlConnection = (HttpsURLConnection) url.openConnection();

                    inputStream = urlConnection.getInputStream();
                    inputStreamReader = new InputStreamReader(inputStream);
                    data = inputStreamReader.read();

                    String content = "";

                    while(data != -1) {
                        char current = (char) data;
                        content += current;

                        data = inputStreamReader.read();
                    }

                    String sql = "INSERT INTO articles (title, content) VALUES(?, ?)";
                    SQLiteStatement statement = articlesDB.compileStatement(sql);
                    statement.bindString(1, title);
                    statement.bindString(2, content);

                    statement.execute();
                }

                return result;

            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }

        }

//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            try {
//                JSONObject jsonObject = new JSONObject(s);
//
//                int totalResults = jsonObject.getInt("totalResults");
//                String articles = jsonObject.getString("articles");
//
//                JSONArray Articles = new JSONArray(articles);
//
//
//            } catch(Exception e) {
//                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            }
//        }
    }
}