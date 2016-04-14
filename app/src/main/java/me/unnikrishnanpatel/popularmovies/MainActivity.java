package me.unnikrishnanpatel.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.GridView;

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
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    MovieAdapter movieAdapter;
    JSONArray results = null;
    ArrayList<HashMap<String, String>> movieList;
    GridView movieGrid;
    FetchMoviewData movieData;
    String mostPopular = "http://api.themoviedb.org/3/movie/popular?api_key=7baf82d2c99ba2997a60d4af8b763034";
    String topRated = "http://api.themoviedb.org/3/movie/top_rated?api_key=7baf82d2c99ba2997a60d4af8b763034";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        movieGrid = (GridView) findViewById(R.id.movie_grid);

        movieData = new FetchMoviewData();
        movieData.execute(topRated);
        movieGrid.setAdapter(movieAdapter);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.top:
                new FetchMoviewData().execute(topRated);
                return true;
            case R.id.pop:
                new FetchMoviewData().execute(mostPopular);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class FetchMoviewData extends AsyncTask<String, Void, Void> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String baseUrl = "http://image.tmdb.org/t/p/w185/";

        String movieDataJson = null;

        @Override
        protected Void doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

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
                movieDataJson = buffer.toString();
                if(movieDataJson!=null){
                    try{
                        JSONObject jsonObject = new JSONObject(movieDataJson);
                        movieList = new ArrayList<HashMap<String, String>>();
                        results = jsonObject.getJSONArray("results");
                        System.out.println(results.length());
                        for(int i=0;i<results.length();i++){
                            JSONObject movieData = results.getJSONObject(i);
                            HashMap<String, String> movie = new HashMap<String, String>();
                            movie.put("poster_path",baseUrl+movieData.getString("poster_path"));
                            movie.put("adult",movieData.getString("adult"));
                            movie.put("original_title",movieData.getString("original_title"));
                            movie.put("overview",movieData.getString("overview"));
                            movie.put("popularity",movieData.getString("popularity"));
                            movie.put("vote_count",movieData.getString("vote_count"));
                            movie.put("vote_average",movieData.getString("vote_average"));
                            movieList.add(movie);
                        }


                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                }


            } catch (IOException e) {

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(this.getClass().getSimpleName(), "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            movieAdapter = new MovieAdapter(getBaseContext(),movieList);
            movieGrid.setAdapter(movieAdapter);
        }
    }
}
