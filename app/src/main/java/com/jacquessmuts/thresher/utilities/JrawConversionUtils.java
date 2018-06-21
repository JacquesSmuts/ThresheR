package com.jacquessmuts.thresher.utilities;

import com.jacquessmuts.thresher.eventbusses.RedditSubmissionVotedBus;
import com.jacquessmuts.thresher.models.RedditComment;
import com.jacquessmuts.thresher.models.RedditPost;

import net.dean.jraw.models.Comment;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.VoteDirection;

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

    /**
     * This function takes a given VoteAction, actions the VoteAction on the redditpost/comment
     * inside, and returns the same voteAction after doing the vote.
     * @param voteAction
     * @return
     */
    public static RedditSubmissionVotedBus.VoteAction implementVote(RedditSubmissionVotedBus.VoteAction voteAction){
        RedditPost redditPost = voteAction.getRedditPost();
        if (redditPost != null) {
            switch (voteAction.getVoteDirection()) {
                case UP:
                    switch (redditPost.getVote()) {
                        case UP:
                        case NONE:
                            redditPost.setVote(VoteDirection.UP);
                            break;
                        case DOWN:
                            redditPost.setVote(VoteDirection.NONE);
                            break;
                    }
                    break;
                case DOWN:
                    switch (redditPost.getVote()) {
                        case UP:
                            redditPost.setVote(VoteDirection.NONE);
                            break;
                        case NONE:
                        case DOWN:
                            redditPost.setVote(VoteDirection.DOWN);
                            break;
                    }
                    break;
            }
            voteAction.setRedditPost(redditPost);
        }

        RedditComment redditComment = voteAction.getRedditComment();
        if (redditComment != null){
            switch (voteAction.getVoteDirection()) {
                case UP:
                    switch (redditComment.getVote()) {
                        case UP:
                        case NONE:
                            redditComment.setVote(VoteDirection.UP);
                            break;
                        case DOWN:
                            redditComment.setVote(VoteDirection.NONE);
                            break;
                    }
                    break;
                case DOWN:
                    switch (redditComment.getVote()) {
                        case UP:
                            redditComment.setVote(VoteDirection.NONE);
                            break;
                        case NONE:
                        case DOWN:
                            redditComment.setVote(VoteDirection.DOWN);
                            break;
                    }
                    break;
            }
            voteAction.setRedditComment(redditComment);
        }

        return voteAction;
    }
}
