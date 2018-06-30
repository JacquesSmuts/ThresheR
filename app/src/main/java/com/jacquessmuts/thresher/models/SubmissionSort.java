package com.jacquessmuts.thresher.models;

import net.dean.jraw.models.SubredditSort;

/**
 * Created by Jacques Smuts on 6/30/2018.
 * When on the frontpage or on a subreddit, or comments, get the submissions with the following filter
 */
public enum SubmissionSort {
    HOT("Hot", SubredditSort.HOT),
    NEW("New", SubredditSort.NEW),
    RISING("Rising", SubredditSort.RISING),
    TOP("Top", SubredditSort.TOP),
    CONTROVERSIAL("Controversial", SubredditSort.CONTROVERSIAL);

    private final String name;
    private final SubredditSort sort;

    SubmissionSort(String name, SubredditSort sort){
        this.name = name;
        this.sort = sort;
    }

    public String getName() {return this.name;}

    public SubredditSort getSort() {
        return sort;
    }
}
