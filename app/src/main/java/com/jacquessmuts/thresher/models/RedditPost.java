package com.jacquessmuts.thresher.models;

import android.os.Parcel;
import android.os.Parcelable;

import net.dean.jraw.databind.UnixTime;
import net.dean.jraw.models.CommentSort;
import net.dean.jraw.models.VoteDirection;

import java.util.Date;

/**
 * Created by Jacques Smuts on 4/15/2018.
 * This is a model based on the JRAW Submission.class model.
 * Use this model instead of dealing with JRAW directly.
 */
public class RedditPost implements Parcelable {

    boolean isNsfw;
    String author;
    @UnixTime Date created_utc;
    @UnixTime Date edited;
    String domain;
//    EmbeddedMedia embeddedMedia;
    String id;
    String name;
    boolean isScoreHidden;
    boolean isSelfPost;
    boolean isLocked;
    String permalink;
    String postHint;
//    SubmissionPreview preview;
    String selfText;
    boolean isSpam;
    boolean isSpoiler;
    String subreddit;
    String subredditFullName;
    CommentSort suggestedSort;
    String thumbnail;
    String title;
    String url;
    boolean isRemoved;
    VoteDirection vote; //aka likes
    Integer commentCount;
    int score;

    public boolean isNsfw() {
        return isNsfw;
    }

    public void setNsfw(boolean nsfw) {
        isNsfw = nsfw;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getCreated_utc() {
        return created_utc;
    }

    public void setCreated_utc(Date created_utc) {
        this.created_utc = created_utc;
    }

    public Date getEdited() {
        return edited;
    }

    public void setEdited(Date edited) {
        this.edited = edited;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isScoreHidden() {
        return isScoreHidden;
    }

    public void setScoreHidden(boolean scoreHidden) {
        isScoreHidden = scoreHidden;
    }

    public boolean isSelfPost() {
        return isSelfPost;
    }

    public void setSelfPost(boolean selfPost) {
        isSelfPost = selfPost;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getPostHint() {
        return postHint;
    }

    public void setPostHint(String postHint) {
        this.postHint = postHint;
    }

    public String getSelfText() {
        return selfText;
    }

    public void setSelfText(String selfText) {
        this.selfText = selfText;
    }

    public boolean isSpam() {
        return isSpam;
    }

    public void setSpam(boolean spam) {
        isSpam = spam;
    }

    public boolean isSpoiler() {
        return isSpoiler;
    }

    public void setSpoiler(boolean spoiler) {
        isSpoiler = spoiler;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getSubredditFullName() {
        return subredditFullName;
    }

    public void setSubredditFullName(String subredditFullName) {
        this.subredditFullName = subredditFullName;
    }

    public CommentSort getSuggestedSort() {
        return suggestedSort;
    }

    public void setSuggestedSort(CommentSort suggestedSort) {
        this.suggestedSort = suggestedSort;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public void setRemoved(boolean removed) {
        isRemoved = removed;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public VoteDirection getVote() {
        return vote;
    }

    public void setVote(VoteDirection vote) {
        this.vote = vote;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RedditPost)) return false;
        RedditPost redditPost = (RedditPost) obj;
        return this.id.equals(redditPost.id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isNsfw ? (byte) 1 : (byte) 0);
        dest.writeString(this.author);
        dest.writeLong(this.created_utc != null ? this.created_utc.getTime() : -1);
        dest.writeLong(this.edited != null ? this.edited.getTime() : -1);
        dest.writeString(this.domain);
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeByte(this.isScoreHidden ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSelfPost ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLocked ? (byte) 1 : (byte) 0);
        dest.writeString(this.permalink);
        dest.writeString(this.postHint);
        dest.writeString(this.selfText);
        dest.writeByte(this.isSpam ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSpoiler ? (byte) 1 : (byte) 0);
        dest.writeString(this.subreddit);
        dest.writeString(this.subredditFullName);
        dest.writeInt(this.suggestedSort == null ? -1 : this.suggestedSort.ordinal());
        dest.writeString(this.thumbnail);
        dest.writeString(this.title);
        dest.writeString(this.url);
        dest.writeByte(this.isRemoved ? (byte) 1 : (byte) 0);
        dest.writeInt(this.vote == null ? -1 : this.vote.ordinal());
        dest.writeValue(this.commentCount);
        dest.writeInt(this.score);
    }

    public RedditPost() {
    }

    protected RedditPost(Parcel in) {
        this.isNsfw = in.readByte() != 0;
        this.author = in.readString();
        long tmpCreated_utc = in.readLong();
        this.created_utc = tmpCreated_utc == -1 ? null : new Date(tmpCreated_utc);
        long tmpEdited = in.readLong();
        this.edited = tmpEdited == -1 ? null : new Date(tmpEdited);
        this.domain = in.readString();
        this.id = in.readString();
        this.name = in.readString();
        this.isScoreHidden = in.readByte() != 0;
        this.isSelfPost = in.readByte() != 0;
        this.isLocked = in.readByte() != 0;
        this.permalink = in.readString();
        this.postHint = in.readString();
        this.selfText = in.readString();
        this.isSpam = in.readByte() != 0;
        this.isSpoiler = in.readByte() != 0;
        this.subreddit = in.readString();
        this.subredditFullName = in.readString();
        int tmpSuggestedSort = in.readInt();
        this.suggestedSort = tmpSuggestedSort == -1 ? null : CommentSort.values()[tmpSuggestedSort];
        this.thumbnail = in.readString();
        this.title = in.readString();
        this.url = in.readString();
        this.isRemoved = in.readByte() != 0;
        int tmpVote = in.readInt();
        this.vote = tmpVote == -1 ? null : VoteDirection.values()[tmpVote];
        this.commentCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.score = in.readInt();
    }

    public static final Creator<RedditPost> CREATOR = new Creator<RedditPost>() {
        @Override
        public RedditPost createFromParcel(Parcel source) {
            return new RedditPost(source);
        }

        @Override
        public RedditPost[] newArray(int size) {
            return new RedditPost[size];
        }
    };
}
