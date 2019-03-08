package com.example.popular_movies_stage1;

import android.net.Uri;
import java.net.URL;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Scanner;

    class MovieUrlUtils {

       // private static final String API_KEY = ;
        private static final String LOG_TAG = MovieUrlUtils.class.getSimpleName();
        private static final String MOVIE_QUERY_API = "api_key";
        private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";

        //Connecting to the internet lesson
        public static URL buildUrl(String movieUrl) {

            Uri uri = Uri.parse(MOVIE_BASE_URL)
                    .buildUpon()
                    .appendPath(movieUrl)
                  //  .appendQueryParameter(MOVIE_QUERY_API, API_KEY)
                    .build();
            URL url = null;
            try {
                url = new URL(uri.toString());
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Problems create url", e);
            }

            return url;
        }

        public static String getResponseFromHttp(URL url) throws IOException {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream inputStream = urlConnection.getInputStream();
                Scanner scanner = new Scanner(inputStream);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            } finally {
                urlConnection.disconnect();
            }
        }
    }

