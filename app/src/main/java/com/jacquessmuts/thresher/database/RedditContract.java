package com.jacquessmuts.thresher.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database. This class is not necessary, but keeps
 * the code organized.
 */
public class RedditContract {

    public static final String CONTENT_AUTHORITY = "com.jacquessmuts.thresher";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for this app.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SUBMISSION = "submission";

    /* Inner class that defines the table contents of the weather table */
    public static final class RedditPostsEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the Weather table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_SUBMISSION)
                .build();

        /* Used internally as the name of our movies table. */
        public static final String TABLE_NAME = "reddit_posts";

        public static final String COLUMN_REDDIT_POST_ID = "reddit_post_id";
        public static final String COLUMN_THUMBNAIL = "thumbnail";
        public static final String COLUMN_TITLE = "title";
        //TODO: rename the columns
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_TRAILERS = "trailers";
        public static final String COLUMN_REVIEWS = "reviews";
        public static final String COLUMN_IS_FAVORITE = "isFavorite";

        public static String getAllSubmissions() {
            return RedditPostsEntry.COLUMN_REDDIT_POST_ID + " NOT NULL";//" >= 0";
        }

        public static String getRedditPostById(String id){
            return RedditPostsEntry.COLUMN_REDDIT_POST_ID + " = " + id;
        }
    }
}