package com.udacity.yuriy.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import data.MovieImageContract;
import data.MovieImageDbHelper;
import entities.Movie;
import layout.OverviewFragment;

/**
 * Created by yuriy on 10/09/16.
 */
public class FetchPopularMoviesTask extends AsyncTask<String, Void, Void> {

    private OverviewFragment.ImageAdapter imageAdapter;
    private SQLiteOpenHelper dbHelper;
    private int position;
    public static int page = 0;

    public FetchPopularMoviesTask(OverviewFragment.ImageAdapter imageAdapter, Context context) {
        this.imageAdapter = imageAdapter;
        dbHelper = new MovieImageDbHelper(context);
        position = imageAdapter.count;
    }

    @Override
    protected Void doInBackground(String... params) {

        synchronized (FetchPopularMoviesTask.class) {

            if (params.length == 0) {
                params = new String[]{"1"};
            }

            if (page >= Integer.parseInt(params[0])) {
                return null;
            } else {
                page = Integer.parseInt(params[0]);
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String jsonStr = null;

            try {
                Uri builtUri = Uri.parse(SessionConfigurations.baseUrl + SessionConfigurations.discover).buildUpon()
                        .appendQueryParameter("sort_by", "popularity.desc")
                        .appendQueryParameter("page", params[0])
                        .appendQueryParameter(SessionConfigurations.APPID_PARAM, SessionConfigurations.APPID_VAL)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                // Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        //   Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                getMoviesFromJson(jsonStr);
            } catch (JSONException e) {
                //  Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }
    }


    private void getMoviesFromJson(String jsonStr) throws JSONException{

        JSONObject jsonObject = new JSONObject(jsonStr);

        int page = jsonObject.getInt("page");

        //List<Movie> movies = new ArrayList<>();

        JSONArray jsonArray = jsonObject.getJSONArray("results");

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (int i = 0; i < jsonArray.length(); i++) {
            parseMovie(jsonArray.getJSONObject(i), page, db);
        }
        db.close();
        //return movies;
    }

    private void parseMovie(JSONObject obj, int page, SQLiteDatabase db) throws JSONException{
//        Movie movie = new Movie();
//        movie.setPosterPath(obj.getString("poster_path"));
//        movie.setOverview(obj.getString("overview"));
//        movie.setId(obj.getString("id"));
//        movie.setTitle(obj.getString("title"));
//        movie.setPage(page);

        ContentValues value = new ContentValues();
        value.put(MovieImageContract.POSTER_PATH, obj.getString("poster_path"));
        value.put(MovieImageContract.POSITION, position++);

        db.insert(MovieImageContract.TABLE_NAME, null, value);
        //return movie;
    }

    @Override
    protected void onPostExecute(Void result) {
        //imageAdapter.addToMovies(result);
        imageAdapter.count = position;
        imageAdapter.wentToFetch = false;
        imageAdapter.refresh();
    }

}
