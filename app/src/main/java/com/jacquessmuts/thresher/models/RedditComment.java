package com.jacquessmuts.thresher.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Jacques Smuts on 6/7/2018.
 * This is a model based on the JRAW Comment.class model
 * Will probably use this model instead of dealing with JRAW directly.
 */
public class RedditComment implements Parcelable{

    String id;
    String author;
    String author_flair_text;
    Date created_utc;
    String body;
    int score;
    int depth;

    public RedditComment(){}
    public RedditComment(String author, String author_flair_text, Date created_utc, String body, int depth) {
        this.author = author;
        this.author_flair_text = author_flair_text;
        this.created_utc = created_utc;
        this.body = body;
        this.depth = depth;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor_flair_text() {
        return author_flair_text;
    }

    public void setAuthor_flair_text(String author_flair_text) {
        this.author_flair_text = author_flair_text;
    }

    public Date getCreated_utc() {
        return created_utc;
    }

    public void setCreated_utc(Date created_utc) {
        this.created_utc = created_utc;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.author);
        dest.writeString(this.author_flair_text);
        dest.writeLong(this.created_utc != null ? this.created_utc.getTime() : -1);
        dest.writeString(this.body);
        dest.writeInt(this.score);
        dest.writeInt(this.depth);
    }

    protected RedditComment(Parcel in) {
        this.id = in.readString();
        this.author = in.readString();
        this.author_flair_text = in.readString();
        long tmpCreated_utc = in.readLong();
        this.created_utc = tmpCreated_utc == -1 ? null : new Date(tmpCreated_utc);
        this.body = in.readString();
        this.score = in.readInt();
        this.depth = in.readInt();
    }

    public static final Creator<RedditComment> CREATOR = new Creator<RedditComment>() {
        @Override
        public RedditComment createFromParcel(Parcel source) {
            return new RedditComment(source);
        }

        @Override
        public RedditComment[] newArray(int size) {
            return new RedditComment[size];
        }
    };
}
