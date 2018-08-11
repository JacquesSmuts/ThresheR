package com.jacquessmuts.thresher.utilities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.jacquessmuts.thresher.database.RedditContract;
import com.jacquessmuts.thresher.models.RedditPost;

import net.dean.jraw.models.VoteDirection;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Jacques Smuts on 7/3/2018.
 * These are functions used to extract data from a cursor and return relevant objects
 */
public class DbUtils {

    private static String getStringFromColumn(Cursor cursor, String columnName){
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    private static int getIntFromColumn(Cursor cursor, String columnName){
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }

    private static boolean getBooleanFromColumn(Cursor cursor, String columnName){
        return (cursor.getInt(cursor.getColumnIndex(columnName)) > 0);
    }
    public static ArrayList<RedditPost> redditPostsFromCursor(Cursor cursor){
        ArrayList<RedditPost> redditPosts = new ArrayList<>();

        while (cursor.moveToNext()) {
            RedditPost redditPost = new RedditPost();

            redditPost.setId(getStringFromColumn(cursor, RedditContract.RedditPostsEntry.COLUMN_REDDIT_POST_ID));
            redditPost.setScore(getIntFromColumn(cursor, RedditContract.RedditPostsEntry.COLUMN_SCORE));
            redditPost.setTitle(getStringFromColumn(cursor, RedditContract.RedditPostsEntry.COLUMN_TITLE));
            redditPost.setThumbnail(getStringFromColumn(cursor, RedditContract.RedditPostsEntry.COLUMN_THUMBNAIL));
            redditPost.setAuthor(getStringFromColumn(cursor, RedditContract.RedditPostsEntry.COLUMN_AUTHOR));
            redditPost.setPermalink(getStringFromColumn(cursor, RedditContract.RedditPostsEntry.COLUMN_PERMALINK));
            redditPost.setSelfText(getStringFromColumn(cursor, RedditContract.RedditPostsEntry.COLUMN_SELF_TEXT));
            redditPost.setSubredditFullName(getStringFromColumn(cursor, RedditContract.RedditPostsEntry.COLUMN_FULLNAME));
            redditPost.setVote(VoteDirection.values()[getIntFromColumn(cursor, RedditContract.RedditPostsEntry.COLUMN_VOTE)]);
            redditPost.setNsfw(getBooleanFromColumn(cursor, RedditContract.RedditPostsEntry.COLUMN_NSFW));
            redditPost.setCreated_utc(new Date(getIntFromColumn(cursor, RedditContract.RedditPostsEntry.COLUMN_TIME_CREATED)));

            redditPosts.add(redditPost);
        }
        return redditPosts;
    }

    public static void insert(ContentResolver contentResolver, List<RedditPost> redditPosts){
        ArrayList<ContentValues> submissionsArrayList = new ArrayList<>();
        for (RedditPost redditPost : redditPosts){
            ContentValues contentValues = new ContentValues();
            contentValues.put(RedditContract.RedditPostsEntry.COLUMN_REDDIT_POST_ID, redditPost.getId());
            contentValues.put(RedditContract.RedditPostsEntry.COLUMN_SCORE, redditPost.getScore());
            contentValues.put(RedditContract.RedditPostsEntry.COLUMN_TITLE, redditPost.getTitle());
            contentValues.put(RedditContract.RedditPostsEntry.COLUMN_THUMBNAIL, redditPost.getThumbnail());
            contentValues.put(RedditContract.RedditPostsEntry.COLUMN_AUTHOR, redditPost.getAuthor());
            contentValues.put(RedditContract.RedditPostsEntry.COLUMN_PERMALINK, redditPost.getPermalink());
            contentValues.put(RedditContract.RedditPostsEntry.COLUMN_SELF_TEXT, redditPost.getSelfText());
            contentValues.put(RedditContract.RedditPostsEntry.COLUMN_FULLNAME, redditPost.getSubredditFullName());
            contentValues.put(RedditContract.RedditPostsEntry.COLUMN_VOTE, redditPost.getVote().ordinal());
            contentValues.put(RedditContract.RedditPostsEntry.COLUMN_NSFW, redditPost.isNsfw());
            contentValues.put(RedditContract.RedditPostsEntry.COLUMN_TIME_CREATED, redditPost.getCreated_utc().getTime());
            submissionsArrayList.add(contentValues);
        }
        contentResolver.bulkInsert(RedditContract.RedditPostsEntry.CONTENT_URI,
                submissionsArrayList.toArray(new ContentValues[submissionsArrayList.size()]));
    }

}
