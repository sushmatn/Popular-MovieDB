package com.sushmanayak.android.popularmovies;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SushmaNayak on 8/17/2015.
 */
public class Movie {

    Context mContext;
    private String mTitle;
    private String mOverview;
    private String mReleaseDate;
    private String mPosterPath;
    private String mThumbNailPath;
    private String mUserRating;

    static final String MOVIEDB_TITLE = "original_title";
    static final String MOVIEDB_OVERVIEW = "overview";
    static final String MOVIEDB_RELEASEDATE = "release_date";
    static final String MOVIEDB_POSTERPATH = "poster_path";
    static final String MOVIEDB_THUMBNAILPATH = "backdrop_path";
    static final String MOVIEDB_USERRATING = "vote_average";
    static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w300/";

    public Movie(Context context, JSONObject movieObject) throws JSONException
    {
        mContext = context;
        mTitle = movieObject.getString(MOVIEDB_TITLE);
        mOverview = movieObject.getString(MOVIEDB_OVERVIEW);
        mPosterPath = movieObject.getString(MOVIEDB_POSTERPATH);
        mReleaseDate = movieObject.getString(MOVIEDB_RELEASEDATE);
        mThumbNailPath = movieObject.getString(MOVIEDB_THUMBNAILPATH);
        mUserRating = movieObject.getString(MOVIEDB_USERRATING);
    }

    public String getTitle() {
        // In some use cases 'null' as a literal string is returned.
        // Handle that case by compar
        if (mTitle.equals(null) || mTitle == "null")
            return mContext.getResources().getString(R.string.infoNA);
        else
            return mTitle;
    }

    public String getOverview() {
        if (mOverview.equals(null) || mOverview == "null")
            return mContext.getResources().getString(R.string.infoNA);
        else
            return mOverview;
    }

    public String getReleaseDate() {
        if (mReleaseDate.equals(null) || mReleaseDate == "null")
            return mContext.getResources().getString(R.string.infoNA);
        else
            return mReleaseDate;
    }

    public String getPosterPath() {
        return BASE_IMAGE_URL + mPosterPath;
    }

    public String getThumbNailPath() {
        return BASE_IMAGE_URL + mThumbNailPath;
    }

    public String getUserRating() {
        return mUserRating;
    }
}
