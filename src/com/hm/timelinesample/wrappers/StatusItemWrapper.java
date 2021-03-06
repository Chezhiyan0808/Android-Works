package com.hm.timelinesample.wrappers;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hm.timelinesample.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import twitter4j.Status;


public class StatusItemWrapper {

    private final View mRow;
    private ImageView mUserProfilePicture;
    private TextView mUsername;
    private TextView mTime;
    private TextView mContent;
    private TextView mVia;
    private TextView mReTweet;
    private TextView mReply;
    private DateTimeFormatter mDateTimeFormatter;
    private ImageLoader mImageLoader;
    private String mUserProfilePictureUri;
    private SimpleImageLoadingListener mImageLoadingListener;

    /**
     * When the user scrolls, it's very possible that the same View will be re-used to display a different
     * Tweet, and if we don't remedy it, we will show an incorrect Profile Image before the right one is loaded.
     * There is a much better way of dealing with this, and it's all in the Official Documentation
     * for this <a href="https://github.com/nostra13/Android-Universal-Image-Loader">Image Loader Library in Git</a>,
     * but for the sake of this sample code, we'll solve it by simply making sure the profile picture's URL that we're
     * going to show is the same one as our current Tweet's user.
     */
    public StatusItemWrapper(final View row, final ImageLoader imageLoader) {
        this.mRow = row;
        mDateTimeFormatter = DateTimeFormat.forPattern("HH:mm");
        mImageLoader = imageLoader;

        mImageLoadingListener = new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (mUserProfilePictureUri.contentEquals(imageUri)) {
                    getUserProfilePicture().setImageBitmap(loadedImage);
                }
            }
        };
    }

    /**
     * Quick note here, too: I'm aware a couple of lines of code here (notably the User's name and
     * perhaps even the User's profile image URL, although the latter is less likely) might be
     * going to the Web, slowing down the whole sample code. But again, it is just a sample
     * code for logging in and displaying a Twitter user's Timeline, not for software
     * engineering.
     */
    public void setForItem(Status item) {

        getUsername().setText(item.getUser().getName());
        getContent().setText(item.getText());
        
        getTime().setText(mDateTimeFormatter.print(item.getCreatedAt().getTime()));

        String via = item.getSource();

        // the 'via' member can come embeded in a classic HTML tag,
        // so we need to get rid of them to properly show
        // the source application with which the Tweet was produced
        via = via.replaceFirst("</.+>", "");
        via = via.replaceFirst("<.*>", "");

        getVia().setText("via " + via);

        mUserProfilePictureUri = item.getUser().getOriginalProfileImageURL();
        getUserProfilePicture().setImageBitmap(null);
        mImageLoader.loadImage(mUserProfilePictureUri, mImageLoadingListener);

    }

    public ImageView getUserProfilePicture() {
        if (mUserProfilePicture == null) {
            mUserProfilePicture = (ImageView) mRow.findViewById(R.id.statusItem_userProfilePicture);
        }
        return mUserProfilePicture;
    }

    public TextView getUsername() {
        if (mUsername == null) {
            mUsername = (TextView) mRow.findViewById(R.id.statusItem_userName);
        }
        return mUsername;
    }

    public TextView getTime() {
        if (mTime == null) {
            mTime = (TextView) mRow.findViewById(R.id.statusItem_time);
        }
        return mTime;
    }

    public TextView getContent() {
        if (mContent == null) {
            mContent = (TextView) mRow.findViewById(R.id.statusItem_content);
        }
        return mContent;
    }

    public TextView getVia() {
        if (mVia == null) {
            mVia = (TextView) mRow.findViewById(R.id.statusItem_via);
        }
        return mVia;
    }
    public TextView getRetweet(){
    	if(null == mReTweet){
    		mReTweet = (TextView)mRow.findViewById(R.id.retweet_view);
    	}
    	return mReTweet;
    }
    public TextView getReply(){
    	if(mReply== null){
    		mReply = (TextView)mRow.findViewById(R.id.replies_view);
    	}
    	return mReply;
    }
}
