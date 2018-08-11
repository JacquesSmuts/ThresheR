package com.jacquessmuts.thresher.eventbusses;

import com.jacquessmuts.thresher.models.RedditComment;
import com.jacquessmuts.thresher.models.RedditPost;

import net.dean.jraw.models.VoteDirection;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Jacques Smuts on 5/16/2018.
 *  This bus is used by RxJava to communicate a submission has been clicked/selected
 */
public class SubmissionVotedBus {

    private static SubmissionVotedBus instance;

    private PublishSubject<VoteAction> subject = PublishSubject.create();

    public static SubmissionVotedBus getInstance() {
        if (instance == null) {
            instance = new SubmissionVotedBus();
        }

        return instance;
    }

    public void onNext(VoteAction voteAction) {
            subject.onNext(voteAction);
    }

    public Observable<VoteAction> listen()  {
        return subject.hide();
    }

    public static class VoteAction{
        private RedditPost redditPost;
        private RedditComment redditComment;
        private VoteDirection voteDirection;

        @Override
        public String toString() {
            String id = "";
            if (redditPost != null) id = redditPost.getId();
            if (redditComment != null) id = redditComment.getId();
            return "{" + voteDirection.toString() + " id = " + id + "}";
        }

        public RedditPost getRedditPost() {
            return redditPost;
        }

        public void setRedditPost(RedditPost redditPost) {
            this.redditPost = redditPost;
        }

        public RedditComment getRedditComment() {
            return redditComment;
        }

        public void setRedditComment(RedditComment redditComment) {
            this.redditComment = redditComment;
        }

        public VoteDirection getVoteDirection() {
            return voteDirection;
        }

        public VoteAction(RedditPost redditPost, VoteDirection voteDirection) {
            this.redditPost = redditPost;
            this.voteDirection = voteDirection;
        }

        public VoteAction(RedditComment redditComment, VoteDirection voteDirection) {
            this.redditComment = redditComment;
            this.voteDirection = voteDirection;
        }
    }
}
