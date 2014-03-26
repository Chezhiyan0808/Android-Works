package com.hm.timelinesample.fragments;

import twitter4j.auth.RequestToken;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hm.timelinesample.R;
import com.hm.timelinesample.model.IRequestTokenRequestCallback;
import com.hm.timelinesample.model.TwitterHelper;


public class TwitterLoginFragment extends DialogFragment {

    
    public static final String __LOGIN_FRAGMENT_TAG__ = TwitterLoginFragment.class.getSimpleName();

    private RequestToken mRequestToken;
    private ITwitterLoginListener mLoginListener;

    private ProgressBar mProgressBar;
    private TextView mDescriptionTextView;
    private EditText mPinEditText;
    private Button mOkButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewgroup, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.twitterlogin_fragment, viewgroup);
        initUIConfig((ViewGroup)v);
        return v;
    }

    private void initUIConfig(ViewGroup viewGroup) {
        getDialog().setTitle(R.string.twitterLoginFragment_title);

        mProgressBar = (ProgressBar) viewGroup.findViewById(R.id.twitterLoginFragment_progressBar);
        mDescriptionTextView = (TextView) viewGroup.findViewById(R.id.twitterLoginFragment_descriptionTextView);
        mPinEditText = (EditText) viewGroup.findViewById(R.id.twitterLoginFragment_pinEditText);
        mOkButton = (Button) viewGroup.findViewById(R.id.twitterLoginFragment_okButton);

       
        getDialog().setCanceledOnTouchOutside(false);
        setCancelable(false);

        // 'OK' button used to process PIN
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pin = mPinEditText.getText().toString();
                processPin(pin);
            }
        });

        // until we get a Request Token, we do not show the description text, the PIN code EditText
        // nor the 'OK' button.
        TwitterHelper.getRequestToken(getActivity(), new IRequestTokenRequestCallback() {
            @Override
            public void onRequestCompleted(RequestToken requestToken) {

                mRequestToken = requestToken;

                String requestURL = requestToken.getAuthorizationURL();
                String descriptionString = String.format(getResources().getString(R.string.twitterLoginFragment_description), requestURL);
                mDescriptionTextView.setText(descriptionString);

                mProgressBar.setVisibility(View.GONE);
                mDescriptionTextView.setVisibility(View.VISIBLE);
                mPinEditText.setVisibility(View.VISIBLE);
                mOkButton.setVisibility(View.VISIBLE);
            }
        });

    }

    public void setLoginListener(ITwitterLoginListener loginListener) {
        mLoginListener = loginListener;
    }

    private void processPin(String pin) {
        TwitterHelper.processOAuthVerifier(this.getActivity(), mRequestToken, pin);

        // if login successful
        if (TwitterHelper.isLoggedIn(getActivity())) {

            dismiss();
            if (mLoginListener != null) {
                mLoginListener.onLoginSuccessful();
            }
        }
        else {
            // message user
            Toast.makeText(getActivity(), getString(R.string.twitterLoginFragment_error), Toast.LENGTH_LONG).show();
        }
    }

    
    public static void showLoginFragment(FragmentActivity activity, ITwitterLoginListener listener) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        // pre-detect if there was a previous Dialog and pop it off the Fragment Stack
        Fragment previousDialog = activity.getSupportFragmentManager().findFragmentByTag(__LOGIN_FRAGMENT_TAG__);
        if (previousDialog != null) {
            ft.remove(previousDialog);
        }
        ft.addToBackStack(null);

        TwitterLoginFragment newDialog = new TwitterLoginFragment();
        newDialog.setLoginListener(listener);
       
        newDialog.show(ft, __LOGIN_FRAGMENT_TAG__);
    }

}
