package com.hm.timelinesample.model;



import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.hm.timelinesample.R;


public final class TwitterHelper {

    private static final String __TWITTER_TOKEN__ = "token";
    private static final String __TWITTER_TOKEN_SECRET__ = "tokenSecret";
    private static boolean mConsumerKeysSet = false;

   
    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String token = sharedPreferences.getString(__TWITTER_TOKEN__, "");
        if (token.length() > 0) {
            return token;
        }
        else {
            return null;
        }
    }

    
    public static String getTokenSecret(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String tokenSecret = sharedPreferences.getString(__TWITTER_TOKEN_SECRET__, "");
        if (tokenSecret.length() > 0) {
            return tokenSecret;
        }
        else {
            return null;
        }
    }

   
    private static void setTokenAndtokenSecret(Context context, String token, String tokenSecret) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(__TWITTER_TOKEN__, token);
        sharedPreferencesEditor.putString(__TWITTER_TOKEN_SECRET__, tokenSecret);
        sharedPreferencesEditor.commit();
    }

   
    public static boolean isLoggedIn(Context context) {
        String token = getToken(context);
        String tokenSecret = getTokenSecret(context);
        return ((token != null) && (tokenSecret != null) && (token.length() > 0) && (tokenSecret.length()) > 0);
    }

    
    public static void logout(Context context) {
        setTokenAndtokenSecret(context, "", "");
        Twitter twitter = TwitterFactory.getSingleton();
        twitter.shutdown();
        if (mConsumerKeysSet) {
            twitter.setOAuthAccessToken(null);
        }
    }

    
    public static AccessToken makeOAuthAccessToken(Context context) {
        String token = getToken(context);
        String tokenSecret = getTokenSecret(context);
        return new AccessToken(token, tokenSecret);
    }

    
    private static RequestToken makeRequestToken(Context context) {
        String twitterConsumerKey = context.getString(R.string.twitterConsumerKey);
        String twitterConsumerSecret = context.getString(R.string.twitterConsumerSecret);
        RequestToken requestToken;

        try {

            Twitter twitter = TwitterFactory.getSingleton();
            if (!mConsumerKeysSet) {
                twitter.setOAuthConsumer(twitterConsumerKey, twitterConsumerSecret);
                mConsumerKeysSet = true;
            }
            requestToken = twitter.getOAuthRequestToken();

        } catch (Exception exception) {
            exception.printStackTrace();
            requestToken = null;
        }

        return requestToken;
    }

    
    public static void getRequestToken(final Context context, final IRequestTokenRequestCallback callback) {

        AsyncTask<Void, Void, Void> backgroundThread = new AsyncTask<Void, Void, Void>() {

            private RequestToken mRequestToken;

            @Override
            protected Void doInBackground(Void... voids) {
                mRequestToken = makeRequestToken(context);
                return null;
            }

            @Override
            protected void onPostExecute(Void object) {
                if (callback != null) {
                    callback.onRequestCompleted(mRequestToken);
                }
            }
        };
        backgroundThread.execute();
    }

    
    public static void processOAuthVerifier(Context context, RequestToken requestToken, String pinCode) {
        try {
            AccessToken accessToken;
            if (pinCode.length() > 0) {
                accessToken = TwitterFactory.getSingleton().getOAuthAccessToken(requestToken, pinCode);
            }
            else {
                accessToken = TwitterFactory.getSingleton().getOAuthAccessToken();
            }

            setTokenAndtokenSecret(context, accessToken.getToken(), accessToken.getTokenSecret());

        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }



}
