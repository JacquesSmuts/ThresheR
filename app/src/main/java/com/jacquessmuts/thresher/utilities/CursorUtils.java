package com.jacquessmuts.thresher.utilities;

import android.database.Cursor;

import com.jacquessmuts.thresher.database.RedditContract;
import com.jacquessmuts.thresher.models.RedditPost;

import java.util.ArrayList;

/**
 * Created by Jacques Smuts on 7/3/2018.
 * TODO: set header description
 */
public class CursorUtils {


    private static String getStringFromColumn(Cursor cursor, String columnName){
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    private static int getIntFromColumn(Cursor cursor, String columnName){
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }
    public static ArrayList<RedditPost> redditPostsFromCursor(Cursor cursor){
        ArrayList<RedditPost> redditPosts = new ArrayList<>();

        while (cursor.moveToNext()) {
            RedditPost redditPost = new RedditPost();

            redditPost.setId(getStringFromColumn(cursor, RedditContract.RedditPostsEntry.COLUMN_REDDIT_POST_ID));
            redditPost.setScore(getIntFromColumn(cursor, RedditContract.RedditPostsEntry.COLUMN_SCORE));
            redditPost.setTitle(getStringFromColumn(cursor, RedditContract.RedditPostsEntry.COLUMN_TITLE));
            redditPost.setThumbnail(getStringFromColumn(cursor, RedditContract.RedditPostsEntry.COLUMN_THUMBNAIL));

            redditPosts.add(redditPost);
        }
        return redditPosts;
    }

}
