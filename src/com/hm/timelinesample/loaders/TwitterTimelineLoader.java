package com.hm.timelinesample.loaders;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.hm.timelinesample.R;
import com.hm.timelinesample.model.TwitterHelper;


public class TwitterTimelineLoader extends AsyncTaskLoader<ResponseList<Status>> {
	public static int lastPagingId=1;
    private static Twitter mTwitter;
    private static ResponseList<Status> mTimeline;
    private static Paging mPaging;

    public TwitterTimelineLoader(Context context, AccessToken accessToken) {
        super(context);
        init(accessToken);
    }

  
    private void init(AccessToken accessToken) {

        if (mTwitter == null) {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setOAuthConsumerKey(getContext().getString(R.string.twitterConsumerKey));
            cb.setOAuthConsumerSecret(getContext().getString(R.string.twitterConsumerSecret));
            cb.setOAuthAccessToken(accessToken.getToken());
            cb.setOAuthAccessTokenSecret(accessToken.getTokenSecret());

            TwitterFactory twitterFactory = new TwitterFactory(cb.build());
            mTwitter = twitterFactory.getInstance();
        }
    }

   
    @Override protected void onStartLoading() {
        // we always forceLoad, because we always want to check
        // for new content
        // and since our content 'lives' through Loader destruction and creation,
        // we're fine
        forceLoad();
    }

  
    @Override
    public ResponseList<Status> loadInBackground() {
        try {
        	
        	
            if (mPaging == null) {
            	mPaging = new Paging(lastPagingId,20);
            	
                // initialize timeline
                mTimeline = mTwitter.getHomeTimeline(mPaging);
                //getUserTimeline("ChennaiIPL", mPaging);
                lastPagingId = lastPagingId+1;
                mPaging.setPage(lastPagingId);
            }
            else {
            	
                // update the timeline
                ResponseList<Status> newStatus =  mTwitter.getHomeTimeline(mPaging);
                //getUserTimeline("ChennaiIPL", mPaging);
                lastPagingId = lastPagingId+1;
                mPaging.setPage(lastPagingId);

                // add new Statuses in reverse order to preserve
                // the Timeline
                for (int i = newStatus.size() - 1; i >= 0; i--) {
                    Status status = newStatus.get(i);
                   
                    mTimeline.add(status);
                    
                }
            }

            // update paging variable so we
            // can request Twitter tweets newer than
            // the most recent one we have
            
           // mPaging = new Paging(firstStatus.getId());
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return mTimeline;
    }

}
