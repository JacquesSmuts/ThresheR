package com.jacquessmuts.thresher.eventbusses;

import com.jacquessmuts.thresher.models.RedditPost;

import net.dean.jraw.models.VoteDirection;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Jacques Smuts on 5/16/2018.
 *  This bus is used by RxJava to communicate a submission has been clicked/selected
 */
public class RedditPostVotedBus {

    private static RedditPostVotedBus instance;

    private PublishSubject<VoteAction> subject = PublishSubject.create();

    public static RedditPostVotedBus getInstance() {
        if (instance == null) {
            instance = new RedditPostVotedBus();
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
        private VoteDirection voteDirection;

        public RedditPost getRedditPost() {
            return redditPost;
        }

        public VoteDirection getVoteDirection() {
            return voteDirection;
        }

        public VoteAction(RedditPost redditPost, VoteDirection voteDirection) {
            this.redditPost = redditPost;
            this.voteDirection = voteDirection;
        }

    }
}
