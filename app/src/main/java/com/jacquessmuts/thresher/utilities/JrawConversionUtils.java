package com.jacquessmuts.thresher.utilities;

import com.jacquessmuts.thresher.models.RedditComment;
import com.jacquessmuts.thresher.models.RedditPost;

import net.dean.jraw.models.Comment;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacques Smuts on 6/7/2018.
 * Utility class for converting between JRAW classes and ThresheR/Android custom classes.
 */
public class JrawConversionUtils {

    private static RedditPost getRedditPost(Submission submission){
        RedditPost toReturn = new RedditPost();

        toReturn.setId(submission.getId());
        toReturn.setThumbnail(submission.getThumbnail());
        toReturn.setTitle(submission.getTitle());
        toReturn.setScore(submission.getScore());

        return toReturn;
    }

    public static RedditComment getRedditComment(Comment comment){
        RedditComment toReturn = new RedditComment();

        toReturn.setId(comment.getId());
        toReturn.setAuthor(comment.getAuthor());
        toReturn.setAuthor_flair_text(comment.getAuthorFlairText());
        toReturn.setBody(comment.getBody());
        toReturn.setCreated_utc(comment.getCreated());
        toReturn.setScore(comment.getScore());

        return toReturn;
    }



    public static List<RedditPost> getRedditPosts(Listing<Submission> submissionListing){
        List<RedditPost> toReturn = new ArrayList<>();
        for (Submission submission: submissionListing){
            toReturn.add(getRedditPost(submission));
        }
        return toReturn;
    }
}
