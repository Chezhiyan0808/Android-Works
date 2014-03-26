package com.hm.timelinesample.adapters;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.hm.timelinesample.R;
import com.hm.timelinesample.model.TwitterHelper;
import com.hm.timelinesample.wrappers.StatusItemWrapper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class TimelineStatusAdapter extends ArrayAdapter<Status> implements OnClickListener {

    private LayoutInflater mLayoutInflater;
    private ImageLoader mImageLoader;
    private Twitter mTwitter = null; 
    public TimelineStatusAdapter(Context context) {
        super(context, R.layout.timeline_listviewitem);
        init();
    }

    private void init() {
        mLayoutInflater = LayoutInflater.from(getContext());
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext()).build();
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(config);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        StatusItemWrapper wrapper;

        if (row == null) {
            row = mLayoutInflater.inflate(R.layout.timeline_listviewitem, null);
            wrapper = new StatusItemWrapper(row, mImageLoader);
            row.setTag(wrapper);
        }
        else {
            wrapper = (StatusItemWrapper) row.getTag();
        }

        wrapper.setForItem(getItem(position));
        (wrapper.getReply()).setTag(getItem(position));
        (wrapper.getReply()).setOnClickListener(this);
        (wrapper.getRetweet()).setOnClickListener(this);
        (wrapper.getRetweet()).setTag(getItem(position));
        return row;
    }

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.replies_view:
			Toast.makeText(getContext(), "hsahdhsad",0).show();
			replyTweetDialogue(((Status)v.getTag()));
			break;
		case R.id.retweet_view:
			Toast.makeText(getContext(), "RT",0).show();
			reTweet(((Status)v.getTag()).getId());
			
			break;		
			}
		
	}

	private void replyTweetDialogue(final Status status) {
		LayoutInflater li = LayoutInflater.from(getContext());
		View promptsView = li.inflate(R.layout.reply_dialogue, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView
				.findViewById(R.id.editTextDialogUserInput);

		// set dialog message
		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("OK",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				if(userInput.getText().toString().length()>0){
			    	replyTweet(status,userInput.getText().toString());
			    	dialog.cancel();	
				}
				else
					Toast.makeText(getContext(), "Please enter a message.", 1).show();
			    }
			  })
			.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}
		
	

	public void reTweet(long startusId){
		try{
		if (mTwitter == null) {
	            getTwitterOauth();
	        }
		 mTwitter.retweetStatus(startusId);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void replyTweet(Status status, String msg){
		try{
			if(null==mTwitter){
				getTwitterOauth();
			}
			 StatusUpdate stat= new StatusUpdate("@"+status.getUser().getScreenName()+"  "+msg);
			 stat.inReplyToStatusId(status.getId());
			 mTwitter.updateStatus(stat);
			 

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getTwitterOauth() {
		
	 TwitterHelper twH = new TwitterHelper();
	 ConfigurationBuilder cb = new ConfigurationBuilder();
  	 cb.setOAuthConsumerKey(getContext().getString(R.string.twitterConsumerKey));
  	 cb.setOAuthConsumerSecret(getContext().getString(R.string.twitterConsumerSecret));
  	 cb.setOAuthAccessToken(twH.getToken(getContext()));
  	 cb.setOAuthAccessTokenSecret(twH.getTokenSecret(getContext()));
    
  	 TwitterFactory twitterFactory = new TwitterFactory(cb.build());
  	 mTwitter = twitterFactory.getInstance();
		
	}
}
