package com.jacquessmuts.thresher.eventbusses;

import com.jacquessmuts.thresher.models.RedditComment;
import com.jacquessmuts.thresher.models.RedditPost;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Jacques Smuts on 5/16/2018.
 *  This bus is used by RxJava to communicate a submission has been clicked/selected
 */
public class CommentSelectedBus {

    private static CommentSelectedBus instance;

    private PublishSubject<RedditComment> subject = PublishSubject.create();

    public static CommentSelectedBus getInstance() {
        if (instance == null) {
            instance = new CommentSelectedBus();
        }

        return instance;
    }

    public void onNext(RedditComment redditComment) {
        subject.onNext(redditComment);
    }

    public Observable<RedditComment> listen()  {
        return subject.hide();
    }

}
