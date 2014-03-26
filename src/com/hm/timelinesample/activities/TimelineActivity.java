package com.hm.timelinesample.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.hm.timelinesample.fragments.TimelineFragment;


public class TimelineActivity extends FragmentActivity {

    private TimelineFragment mTimelineFragment;
    private static final String __TIMELINE_FRAGMENT_TAG__ = TimelineFragment.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mTimelineFragment = (TimelineFragment) getSupportFragmentManager().findFragmentByTag(__TIMELINE_FRAGMENT_TAG__);
        } else {
            mTimelineFragment = new TimelineFragment();
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, mTimelineFragment, __TIMELINE_FRAGMENT_TAG__).commit();
        }

    }

    
}
