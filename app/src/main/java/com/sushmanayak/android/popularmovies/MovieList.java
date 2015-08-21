package com.sushmanayak.android.popularmovies;

import java.util.ArrayList;

/**
 * Created by SushmaNayak on 8/17/2015.
 */
public class MovieList {

    private static ArrayList<Movie> movieArrayList = new ArrayList<>();
    private static MovieList movieListObj;

    private MovieList()
    {}

    public static MovieList getInstance()
    {
        if(movieListObj== null)
            movieListObj = new MovieList();
        return  movieListObj;
    }

    public ArrayList<Movie> getMovieArrayList()
    {
        return movieArrayList;
    }

    public Movie get(int position)
    {
        if(position < movieArrayList.size())
            return movieArrayList.get(position);
        return null;
    }
}
