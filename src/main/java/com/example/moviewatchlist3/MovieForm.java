package com.example.moviewatchlist3;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class MovieForm extends AppCompatActivity {

    private JSONObject jo = new JSONObject();
    private HashMap<String, String> movie = new HashMap<>();

    private class JsonTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader br = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream is = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = br.readLine()) != null){
                    buffer.append(line + "\n");
                }

                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(String result) {
            try {
                jo = new JSONObject(result);
                TextView titleTxtView = findViewById(R.id.title_txt_view);
                titleTxtView.setText("Title: " + jo.getString("Title"));
                TextView yearTxtView = findViewById(R.id.year_txt_view);
                yearTxtView.setText("Year: " + jo.getString("Year"));
                movie.put("Title", jo.getString("Title"));
                movie.put("Year", jo.getString("Year"));
                movie.put("Poster", jo.getString("Poster"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class ImageLoader extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                URL imgUrl = new URL(movie.get("Poster"));
                HttpURLConnection connection = (HttpURLConnection) imgUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                return BitmapFactory.decodeStream(connection.getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Bitmap result) {
            ImageView movieImgView = findViewById(R.id.movie_image_view);
            movieImgView.setImageBitmap(result);
            movieImgView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_form);

        final SearchView movieSearchView = (SearchView) findViewById(R.id.search_view);
        movieSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                new JsonTask().execute("http://www.omdbapi.com/?apikey=38d39d0b&t=" + movieSearchView.getQuery());
                new ImageLoader().execute();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });

        Button addToListBtn = (Button) findViewById(R.id.add_to_list_btn);
        addToListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spnr = findViewById(R.id.list_spnr);
                movie.put("selection", spnr.getSelectedItem().toString());
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", movie);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                return;
            }
        });
    }
}
