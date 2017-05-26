package com.example.android.tmdbmovies;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import static android.R.attr.id;


public class MainActivity extends AppCompatActivity {

    private static final String THUMBNAIL_URL_BASE = "https://image.tmdb.org/t/p/w92/";

    final String DATA_URL_BASE =
            "https://api.themoviedb.org/3/movie/";
    final String API_KEY = "api_key";


    String api_key;

    public MovieData movieData;

    public TextView msgView;
    public int selectIndex = 0;

    public String[] sortBy = {"popular","top_rated"};

    public String errorMsg = "";

    Menu selectMenu;

    /**
     * Movie details layout contains title, release date, movie poster, vote average, and plot synopsis.
     */

    boolean loadinginProcess = false;
    TNAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<TNUnit> TNUnits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        movieData = new MovieData((Context) this);
        TNUnits = new ArrayList<TNUnit>();
        msgView = (TextView) findViewById(R.id.msg_view);
        api_key = getResources().getString(R.string.api_key);

        initViews();

        loadData();
    }
    private void initViews(){
        recyclerView = (RecyclerView)findViewById(R.id.rv_thumbnails);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                (RecyclerItemClickListener.OnItemClickListener) new TNClickHander()));

    }


    public class TNClickHander implements RecyclerItemClickListener.OnItemClickListener {
        public void onItemClick(View v,int position) {
            Context context = MainActivity.this;
            Class destinationClass = ShowMovieDetailsActivity.class;
            Intent intent = new Intent(context,destinationClass);
            intent.putExtra("title",movieData.movieTitle[position]);
            intent.putExtra("poster_path",movieData.posterPath[position]);
            intent.putExtra("synopsis","Synopsis: " + movieData.synopsis[position]);
            intent.putExtra("rating","USER RATING: " + movieData.voteAverage[position]);
            intent.putExtra("release_date","RELEASE DATE: " + movieData.releaseDate[position]);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
    }
    private void populateViews() {
        TNUnits.clear();
        adapter = new TNAdapter(getApplicationContext(),TNUnits);
        recyclerView.setAdapter(adapter);
        for(int i=0;i<movieData.movieId.length;i++){

            TNUnit tnUnit = new TNUnit();
            tnUnit.setTN_Text(movieData.movieId[i]);
            tnUnit.setTN_image_url(THUMBNAIL_URL_BASE + movieData.posterPath[i]);
            TNUnits.add(tnUnit);
        }
        adapter.notifyDataSetChanged();

        if (selectIndex == 0) {
            MenuItem item = selectMenu.findItem(R.id.by_popular);
            item.setEnabled(false);
            item = selectMenu.findItem(R.id.by_top_rated);
            item.setEnabled(true);
            msgView.setText(R.string.fetch_success_popular);
        }
        else {
            MenuItem item = selectMenu.findItem(R.id.by_popular);
            item.setEnabled(true);
            item = selectMenu.findItem(R.id.by_top_rated);
            item.setEnabled(false);
            msgView.setText(R.string.fetch_success_top_rated);
        }
    }

    @Override
    // Menu of sort selection
    public boolean onCreateOptionsMenu(Menu menu) {
        selectMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.selection, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (loadinginProcess)
            return true;
        int id = item.getItemId();
        if (id == R.id.by_popular) {
            if (selectIndex == 0)
                return true;
            selectIndex = 0;
            loadData();
        }
        else {
            if (selectIndex == 1)
                return true;
            selectIndex = 1;
            loadData();
        }
        return true;
    }

    private void loadData() {
        msgView.setText(R.string.fetching);
        String[] params = new String[2];
        params[0] = sortBy[selectIndex];
        params[1] = api_key;
        new FetchDataTask().execute(params);

    }


    public class FetchDataTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {


            if (params == null)
                return null;
            String retVal = null;
            URL url = null;
            try {
                Uri uri = Uri.parse(DATA_URL_BASE + params[0]).buildUpon()
                        .appendQueryParameter(API_KEY, params[1]).build();
                url = new URL(uri.toString());
                if (url == null) {
                    return null;
                }

                retVal = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException io) {
                io.printStackTrace();
                return null;
            }


            if (retVal == null)
                return null;

            String[] retVals = new String[1];
            retVals[0] = retVal;
            return retVals;
        }


        @Override
        protected void onPostExecute(String[] retVals) {
            if (retVals == null) {
                msgView.setText(R.string.fetch_failed);
                return;
            }
            boolean success = movieData.populate(retVals[0]);
            if (!success) {
                msgView.setText(R.string.json_failed);
                return;
            }
            populateViews();
            return;

        }
    }

}
