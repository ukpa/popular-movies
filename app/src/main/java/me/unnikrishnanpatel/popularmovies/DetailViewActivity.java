package me.unnikrishnanpatel.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

public class DetailViewActivity extends AppCompatActivity {

    ImageView poster;
    TextView releaseDate;
    TextView movieLength;
    TextView rating;
    TextView overview;
    TextView tagline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        Intent i = getIntent();
        HashMap<String,String> movieData = (HashMap<String,String>) i.getSerializableExtra("movieData");
        this.setTitle(movieData.get("title"));
        FetchDetails details = new FetchDetails();
        details.execute(movieData.get("id"));


    }

    public class FetchDetails extends AsyncTask<String, Void, Void> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String baseUrl = "http://image.tmdb.org/t/p/w185/";
        String preUrl = "https://api.themoviedb.org/3/movie/";
        String api = "?api_key=<>";
        String movieDataJson = null;

        @Override
        protected Void doInBackground(String... params) {

            try {
                URL url = new URL(preUrl+params[0]+api);

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
            try{
                JSONObject jsonObject = new JSONObject(movieDataJson);
                poster = (ImageView)findViewById(R.id.moviePoster);
                movieLength = (TextView)findViewById(R.id.movieLength);
                releaseDate = (TextView)findViewById(R.id.release);
                overview = (TextView)findViewById(R.id.overview);
                rating = (TextView)findViewById(R.id.rating);
                tagline = (TextView)findViewById(R.id.tagline);
                System.out.println(baseUrl+jsonObject.get("poster_path"));
                Picasso.with(getBaseContext()).load(baseUrl+jsonObject.get("poster_path")).into(poster);
                movieLength.setText("Duration: "+jsonObject.get("runtime").toString()+" Minutes");
                releaseDate.setText("Release: "+jsonObject.get("release_date").toString());
                rating.setText("Rating: "+jsonObject.get("vote_average").toString()+"/10");
                overview.setText("Overview:\n\n"+jsonObject.get("overview").toString());
                tagline.setText(jsonObject.get("tagline").toString());

            }
            catch (JSONException e){
                e.printStackTrace();
            }
            catch (NullPointerException e){
                e.printStackTrace();

            }





        }
    }
}
