package com.jacquessmuts.thresher.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jacquessmuts.thresher.R;
import com.jacquessmuts.thresher.ThresherApp;
import com.jacquessmuts.thresher.models.RedditPost;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Comment;
import net.dean.jraw.models.PublicContribution;
import net.dean.jraw.tree.CommentNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * A fragment representing a single Submission detail screen.
 * This fragment is either contained in a {@link RedditPostListActivity}
 * in two-pane mode (on tablets) or a {@link RedditPostDetailActivity}
 * on handsets.
 */
public class SubmissionDetailFragment extends Fragment {

    private RedditPost redditPost;

    public SubmissionDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(RedditPostDetailActivity.KEY_REDDIT_POST)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            redditPost = getArguments().getParcelable(RedditPostDetailActivity.KEY_REDDIT_POST);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(redditPost.getTitle());
            }
        }

        getComments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.submission_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (redditPost != null) {
            ((TextView) rootView.findViewById(R.id.submission_detail)).setText(redditPost.getTitle());
        }

        return rootView;
    }

    private void getComments(){

        if (redditPost == null || redditPost.getId() == null) return;

        Observable.fromCallable(this::downloadComments)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((redditPosts) -> {
                    Log.i("getComments", "DONE!");
//                    insertDBValues(redditPosts);
//                    updatePage(redditPosts);
//                    setLoading(false);
                });
    }

    private List<Comment> downloadComments(){
        RedditClient redditClient = ThresherApp.getAccountHelper().getReddit();

        // By default, this Iterable will use pre-order traversal.
        // By passing a TraversalMethod in the walkTree() method,
        // you can change the way in which the comments will be iterated.
        CommentNode rootNode = redditClient.submission(redditPost.getId()).comments();
        Iterator<CommentNode<PublicContribution<?>>> iterable = rootNode.walkTree().iterator();

        List<Comment> comments = new ArrayList<>();
        while (iterable.hasNext()) {
            // A PublicContribution is either a Submission or a Comment.
            PublicContribution<?> thing = iterable.next().getSubject();
            if (!(thing instanceof Comment)) continue; //Ignore non-comments for now

            Comment comment = (Comment) thing;
            comments.add(comment);

            Log.i(comment.getAuthor(), comment.getBody());
            // Do something with each Submission/Comment
        }

        return comments;
    }
}
