package com.hm.timelinesample.model;

import twitter4j.auth.RequestToken;


public interface IRequestTokenRequestCallback {

    public void onRequestCompleted(RequestToken requestToken);

}
