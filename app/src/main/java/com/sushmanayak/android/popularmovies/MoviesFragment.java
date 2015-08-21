package com.sushmanayak.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesFragment extends Fragment {

    GridView mMoviesGridView;
    ImageAdapter mMoviesAdapter;
    static SearchMethod mCurrentSortMethod = SearchMethod.POPULARITY;
    FloatingActionButton mLoadMovies;
    Toolbar mToolbar;
    static int mPageNumber = 1;
    Boolean mIncrementPage = false;

    static final String CURRENT_PAGE = "com.sushmanayak.PopularMovies.CurrentMoviePage";

    public static final String LOG_TAG = MoviesActivity.class.getSimpleName();

    enum SearchMethod {
        POPULARITY,
        USER_RATING
    }

    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);

        mMoviesGridView = (GridView) view.findViewById(R.id.movieGridView);
        mMoviesGridView.setOnItemClickListener(movieItemClicked);

        // Set the adapter
        mMoviesAdapter = new ImageAdapter(getActivity());
        mMoviesGridView.setAdapter(mMoviesAdapter);

        // Create and set the EmptyView
        ImageView imgView = new ImageView(getActivity());
        imgView.setImageResource(R.drawable.pagenotfound);
        ((ViewGroup) mMoviesGridView.getParent()).addView(imgView);
        mMoviesGridView.setEmptyView(imgView);

        // Fetch the movie list
        FetchMoviesTask task = new FetchMoviesTask();
        task.execute(SearchMethod.POPULARITY.ordinal());

        // Load the next page of movies when the floating button is clicked
        mLoadMovies = (FloatingActionButton) view.findViewById(R.id.loadMovies);
        mLoadMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FetchMoviesTask task = new FetchMoviesTask();
                task.execute(mCurrentSortMethod.ordinal());
                mIncrementPage = true;
            }
        });
        return view;
    }

    public AdapterView.OnItemClickListener movieItemClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), MovieInfoActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, position);
            startActivity(intent);
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_movies, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sortbypopularity)
            mCurrentSortMethod = SearchMethod.POPULARITY;
        else if (id == R.id.action_sortbyratings)
            mCurrentSortMethod = SearchMethod.USER_RATING;

        // Reset the page number when a new SortMethod is requested
        mPageNumber = 1;
        FetchMoviesTask task = new FetchMoviesTask();
        task.execute(mCurrentSortMethod.ordinal());

        return super.onOptionsItemSelected(item);
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<Movie> mMovieList;

        public ImageAdapter(Context c) {
            mContext = c;
            mMovieList = MovieList.getInstance().getMovieArrayList();
        }

        @Override
        public int getCount() {
            return mMovieList.size();
        }

        @Override
        public Object getItem(int position) {
            return mMovieList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ImageView view = (ImageView) convertView;
            if (view == null) {
                view = new ImageView(mContext);
                view.setAdjustViewBounds(true);
            }
            Movie currentMovie = mMovieList.get(position);
            Picasso.with(mContext).load(currentMovie.getPosterPath())
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.image_unavailable)
                    .into(view);

            return view;
        }
    }

    public class FetchMoviesTask extends AsyncTask<Integer, Void, Boolean> {

        @Override
        protected void onPostExecute(Boolean bRet) {
            super.onPostExecute(bRet);
            mMoviesAdapter.notifyDataSetChanged();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            final String THEMOVIEDB_BASE_URL =
                    "http://api.themoviedb.org/3/discover/movie?";
            final String SORTBY_PARAM = "sort_by";
            final String API_KEY_PARAM = "api_key";
            final String SORTBY_POPULARITY = "popularity.desc";
            final String SORTBY_RATING = "vote_count.desc";

            String sortBy = SORTBY_POPULARITY;
            if (params[0] == 1)
                sortBy = SORTBY_RATING;

            if (mIncrementPage) {
                mPageNumber++;
                mIncrementPage = false;
            }

            try {
                String uriString = Uri.parse(THEMOVIEDB_BASE_URL)
                        .buildUpon()
                        .appendQueryParameter("page", String.valueOf(mPageNumber))
                        .appendQueryParameter(SORTBY_PARAM, sortBy)
                        .appendQueryParameter(API_KEY_PARAM, getString(R.string.api_key))
                        .build().toString();

                URL url = new URL(uriString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return false;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return false;
                }
                moviesJsonStr = buffer.toString();
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MoviesFragment", "Error closing stream", e);
                    }
                }
            }
            try {
                ExtractDataFromJSONString(moviesJsonStr);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
            return true;
        }

        private void ExtractDataFromJSONString(String moviesJsonStr) throws JSONException {
            final String MOVIEDB_RESULTS = "results";

            JSONObject jsonObject = new JSONObject(moviesJsonStr);
            JSONArray movieList = jsonObject.getJSONArray(MOVIEDB_RESULTS);

            ArrayList<Movie> movieArrayList = MovieList.getInstance().getMovieArrayList();
            movieArrayList.clear();

            for (int i = 0; i < movieList.length(); i++) {
                Movie movie = new Movie(getActivity(), (JSONObject) movieList.get(i));
                movieArrayList.add(movie);
            }
        }
    }
}
