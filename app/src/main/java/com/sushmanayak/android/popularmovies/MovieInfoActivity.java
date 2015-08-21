package com.sushmanayak.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieInfoActivity extends AppCompatActivity {

    public final static String CURR_POS = "com.sushmanayak.popularmovies.CurrentMoviePosition";

    ArrayList<Movie> mMovieList;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    Toolbar mToolbar;
    int currentPosition = 0;
    TextView titleTextView;
    TextView overviewTextView;
    TextView releaseDateTextView;
    TextView userRating;
    RatingBar userRatingBar;
    ImageView moviePosterView;
    ImageView thumbnailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);

        mMovieList = MovieList.getInstance().getMovieArrayList();
        moviePosterView = (ImageView)findViewById(R.id.moviePosterView);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        titleTextView = (TextView)findViewById(R.id.title_textview);
        overviewTextView = (TextView)findViewById(R.id.overview_textview);
        releaseDateTextView = (TextView)findViewById(R.id.releasedate_textview);
        userRatingBar = (RatingBar)findViewById(R.id.userRatingBar);
        userRating = (TextView)findViewById(R.id.userRating);
        thumbnailView = (ImageView)findViewById(R.id.thumbnailView);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        mToolbar.setBackgroundColor(0);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().hasExtra(Intent.EXTRA_TEXT))
            currentPosition = getIntent().getIntExtra(Intent.EXTRA_TEXT,0);

        if(savedInstanceState != null)
            currentPosition = savedInstanceState.getInt(CURR_POS);

        updateView(currentPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURR_POS,currentPosition);
    }

    private void updateView(int itemIndex)
    {
        Movie movie = MovieList.getInstance().getMovieArrayList().get(itemIndex);
        titleTextView.setText(movie.getTitle());
        overviewTextView.setText(movie.getOverview());
        releaseDateTextView.setText(movie.getReleaseDate());
        userRatingBar.setRating(Float.valueOf(movie.getUserRating()));
        userRating.setText(movie.getUserRating());
        Picasso.with(this).load(movie.getPosterPath()).into(thumbnailView);
        Picasso.with(this).load(movie.getThumbNailPath()).into(moviePosterView);
        mCollapsingToolbarLayout.setTitle(movie.getTitle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }
}
