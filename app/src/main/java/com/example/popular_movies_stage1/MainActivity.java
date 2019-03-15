package com.example.popular_movies_stage1;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieClickListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String CALLBACK_QUERY = "callbackQuery";
    private static final String CALLBACK_NAMESORT= "callbackNamesort";
    private String queryMovie = "popular";
    private String nameSort = "Popular Movies";
    private Movie[] mMovie;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        mRecyclerView = findViewById( R.id.rv_main );
        mRecyclerView.setLayoutManager( new GridLayoutManager( this, 2 ) );
        mRecyclerView.setHasFixedSize( true );

        setTitle(nameSort);
        if (isOnline()) {
            return;
        }

        if (savedInstanceState != null){
            if (savedInstanceState.containsKey(CALLBACK_QUERY) || savedInstanceState.containsKey(CALLBACK_NAMESORT)){
                queryMovie = savedInstanceState.getString(CALLBACK_QUERY);
                nameSort = savedInstanceState.getString(CALLBACK_NAMESORT);
                setTitle(nameSort);
                new MovieFetchTask().execute(queryMovie);
                return;
            }
        }
        new MovieFetchTask().execute(queryMovie);

    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo == null || !netInfo.isConnectedOrConnecting();
    }

    // From menu lesson on Udacity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemClicked = item.getItemId();
        if (itemClicked == R.id.action_popular){
            //sort by popularity.desc
            queryTheMovieDatabase(R.id.action_popular);
            return true;

        }else if (itemClicked == R.id.action_highest_rated){
            //use sort by vote_average.desc
            queryTheMovieDatabase(R.id.action_popular);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void queryTheMovieDatabase(int sortType){

        URL dataUrl = MovieUrlUtils.buildUrlMENU(sortType);
        new MovieFetchTask().execute();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClickMovie(int position) {

        if (isOnline()) {
            mRecyclerView.setVisibility(View.INVISIBLE);

            return;
        }

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("title", mMovie[position].getmTitle());
        intent.putExtra("poster", mMovie[position].getmMoviePoster());
        intent.putExtra("plot", mMovie[position].getmPlot());
        intent.putExtra("rating", mMovie[position].getmRating());
        intent.putExtra("releaseDate", mMovie[position].getmReleaseDate());
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String queryMovieSaved = queryMovie;
        String nameSortSaved = nameSort;
        outState.putString(CALLBACK_QUERY, queryMovieSaved);
        outState.putString(CALLBACK_NAMESORT, nameSortSaved);

    }
    private class MovieFetchTask extends AsyncTask<String, Void, Movie[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mRecyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Movie[] doInBackground(String... strings) {
            if (isOnline()) {
                return null;
            }
            URL movieUrl = MovieUrlUtils.buildUrl(strings[0]);
            String movieResponse;
            try {
                movieResponse = MovieUrlUtils.getResponseFromHttp(movieUrl);
                mMovie = MovieJsonUtils.parseJsonMoview(movieResponse);
            } catch (Exception e) {

                e.printStackTrace();
            }
            return mMovie;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            new MovieFetchTask().cancel(true);
            if (movies != null) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mMovie = movies;
                MovieAdapter movieAdapter = new MovieAdapter(movies, MainActivity.this, MainActivity.this);
                mRecyclerView.setAdapter(movieAdapter);

            } else {
                Log.e(LOG_TAG, "You're stupid");
            }
        }
    }

}