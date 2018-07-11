package com.jacquessmuts.thresher.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.jacquessmuts.thresher.R;
import com.jacquessmuts.thresher.ThresherApp;
import com.jacquessmuts.thresher.adapters.RedditCommentAdapter;
import com.jacquessmuts.thresher.eventbusses.RedditSubmissionVotedBus;
import com.jacquessmuts.thresher.models.RedditComment;
import com.jacquessmuts.thresher.models.RedditPost;
import com.jacquessmuts.thresher.utilities.JrawConversionUtils;

import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Comment;
import net.dean.jraw.models.PublicContribution;
import net.dean.jraw.tree.CommentNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * A fragment representing a single Submission detail screen.
 * This fragment is either contained in a {@link RedditPostListActivity}
 * in two-pane mode (on tablets) or a {@link RedditPostDetailActivity}
 * on handsets.
 */
public class SubmissionDetailFragment extends Fragment {

    private RedditPost redditPost;

    @BindView(R.id.recyclerview_comments) RecyclerView recyclerView;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    RedditCommentAdapter commentAdapter;

    public CompositeDisposable eventDisposables = new CompositeDisposable();

    public SubmissionDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Timber.d("OnCreate Beginning");
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
        Timber.d("OnCreate Done");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("OnCreateView Beginning");
        View rootView = inflater.inflate(R.layout.submission_detail, container, false);
        ButterKnife.bind(this, rootView);

        // Show the dummy content as text in a TextView.
        if (redditPost != null) {
            //((TextView) rootView.findViewById(R.id.submission_detail)).setText(redditPost.getTitle());
        }

        setupCommentAdapter();
        Timber.d("OnCreateView Done");
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        eventDisposables.add(RedditSubmissionVotedBus.getInstance().listen()
                .observeOn(Schedulers.computation())
                .map(voteAction -> {
                    RedditClient redditClient = ThresherApp.getAccountHelper().getReddit();
                    try {
                        switch (voteAction.getVoteDirection()) {
                            case UP:
                                redditClient.submission(voteAction.getRedditComment().getId()).upvote();
                                break;
                            case DOWN:
                                redditClient.submission(voteAction.getRedditComment().getId()).downvote();
                                break;
                        }
                    } catch (ApiException e){
                        //TODO the JRAW client currently has this issue so upvotes sometimes don't work. Make more robust.
                        Timber.w(e.getMessage());
                    }
                    return voteAction;
                } )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<RedditSubmissionVotedBus.VoteAction>() {
                    @Override
                    public void onNext(RedditSubmissionVotedBus.VoteAction voteAction) {
                        Timber.d("voted on item " + voteAction.toString());

                        RedditComment redditComment = JrawConversionUtils.implementVote(voteAction).getRedditComment();
                        commentAdapter.commentUpdated(redditComment);
                    }
                    @Override
                    public void onError(Throwable e) {
                        Timber.e("error");
                    }
                    @Override
                    public void onComplete() {
                        Timber.v("complete");
                    }
                }));
    }

    @Override
    public void onPause() {
        super.onPause();
        eventDisposables.clear();
    }

    private void setupCommentAdapter() {
        commentAdapter = new RedditCommentAdapter(getActivity(), null);
        recyclerView.setAdapter(commentAdapter);
    }

    private void refreshCommentAdapter(List<RedditComment> comments){
        commentAdapter.setRedditComments(comments);
        hideProgress();
    }

    private void getComments(){
        if (redditPost == null || redditPost.getId() == null) return;
        if (progressBar != null) showProgress();

        Observable.fromCallable(this::downloadComments)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((redditComments) -> {
                    refreshCommentAdapter(redditComments);
                    Timber.d("GetComments: Done!");
                });
    }

    private void showProgress(){
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress(){
        progressBar.setVisibility(View.GONE);
    }

    private List<RedditComment> downloadComments(){
        RedditClient redditClient = ThresherApp.getAccountHelper().getReddit();

        // By default, this Iterable will use pre-order traversal.
        // By passing a TraversalMethod in the walkTree() method,
        // you can change the way in which the comments will be iterated.
        CommentNode rootNode = redditClient.submission(redditPost.getId()).comments();
        Iterator<CommentNode<PublicContribution<?>>> iterable = rootNode.walkTree().iterator();

        List<RedditComment> comments = new ArrayList<>();
        while (iterable.hasNext()) {
            // A PublicContribution is either a Submission or a Comment.
            CommentNode<PublicContribution<?>> commentNode = iterable.next();
            PublicContribution<?> thing = commentNode.getSubject();
            if (!(thing instanceof Comment)) continue; //Ignore non-comments for now

            Comment originalComment = (Comment) thing;
            RedditComment redditComment = JrawConversionUtils.getRedditComment(originalComment);
            redditComment.setDepth(commentNode.getDepth());
            comments.add(redditComment);

            StringBuilder depth = new StringBuilder();
            for (int i = 0; i <= redditComment.getDepth(); i++){
                depth.append("  ");
            }

            Timber.v(redditComment.getAuthor() +  ": \n  " + depth + redditComment.getBody());
        }

        return comments;
    }
}
