package com.example.android.tmdbmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ShowMovieDetailsActivity extends AppCompatActivity {

    final String POSTER_URL_BASE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

	POSTER_URL_BASE = getResources().getString(R.string.image_url_base);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_movie_details);
        showDetails();
    }

    private void showDetails() {
        Intent intent = getIntent();
        ((TextView) findViewById(R.id.s_title)).setText(intent.getStringExtra("title"));
        ((TextView) findViewById(R.id.s_synopsis)).setText(intent.getStringExtra("synopsis"));
        ((TextView) findViewById(R.id.s_rating)).setText(intent.getStringExtra("rating"));
        ((TextView) findViewById(R.id.s_release_date)).setText(intent.getStringExtra("release_date"));
        Picasso.with(this).load(POSTER_URL_BASE + intent.getStringExtra("poster_path"))
                .into((ImageView) findViewById(R.id.s_poster));
    }
}
