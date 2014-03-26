package com.hm.timelinesample.model;

import twitter4j.auth.AccessToken;

public interface IAccessTokenRequestCallback {

    public void onAccessTokenDenied();
    public void onAccessTokenObtained(AccessToken accessToken);

}
