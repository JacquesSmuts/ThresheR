package com.jacquessmuts.thresher.eventbusses;

import com.jacquessmuts.thresher.models.RedditPost;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Jacques Smuts on 5/16/2018.
 *  This bus is used by RxJava to communicate a submission has been clicked/selected
 */
public class RedditPostSelectedBus {

    private static RedditPostSelectedBus instance;

    private PublishSubject<RedditPost> subject = PublishSubject.create();

    public static RedditPostSelectedBus getInstance() {
        if (instance == null) {
            instance = new RedditPostSelectedBus();
        }

        return instance;
    }

    public void onNext(RedditPost redditPost) {
        subject.onNext(redditPost);
    }

    public Observable<RedditPost> listen()  {
        return subject.hide();
    }

}
