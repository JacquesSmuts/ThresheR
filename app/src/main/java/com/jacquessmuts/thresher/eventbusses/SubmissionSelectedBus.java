package com.jacquessmuts.thresher.eventbusses;

import net.dean.jraw.models.Submission;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Jacques Smuts on 5/16/2018.
 *  This bus is used by RxJava to communicate a submission has been clicked/selected
 */
public class SubmissionSelectedBus {

    private static SubmissionSelectedBus instance;

    private PublishSubject<Submission> subject = PublishSubject.create();

    public static SubmissionSelectedBus getInstance() {
        if (instance == null) {
            instance = new SubmissionSelectedBus();
        }

        return instance;
    }

    public void onNext(Submission submission) {
        subject.onNext(submission);
    }

    public Observable<Submission> listen()  {
        return subject.hide();
    }

}
