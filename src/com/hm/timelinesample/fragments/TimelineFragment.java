package com.hm.timelinesample.fragments;

import twitter4j.ResponseList;
import twitter4j.Status;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.hm.timelinesample.R;
import com.hm.timelinesample.adapters.TimelineStatusAdapter;
import com.hm.timelinesample.loaders.TwitterTimelineLoader;
import com.hm.timelinesample.model.TwitterHelper;
import com.hm.timelinesample.wrappers.ActionItem;
import com.hm.timelinesample.wrappers.QuickAction;


@SuppressLint("NewApi")
public class TimelineFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<ResponseList<Status>>,
		OnItemClickListener {

	private static final int __MENU_REFRESH_OPTION__ = Menu.FIRST;
	private static final int __MENU_LOGOUT_OPTION__ = Menu.FIRST + 1;
	private static final int __TIMELINE_LOADER_ID__ = 0;

	private boolean mLoginVisible;
	private boolean mRefreshing;
	private ListView mTimelineListView;
	private TimelineStatusAdapter mTimelineAdapter;
	private int mSelectedRow = 0;

//	private static final int ID_ADD = 1;
//	private static final int ID_ACCEPT = 2;
//	private static final int ID_UPLOAD = 3;
//	QuickAction mQuickAction = null ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.timeline_fragment, viewGroup,
				false);
		// UIConfig goes first because
		initUIConfig((ViewGroup) view);
		// dataConfig sets the listView's adapter
		initDataConfig();
		return view;
	}

	private void initUIConfig(ViewGroup viewGroup) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		mTimelineListView = (ListView) viewGroup
				.findViewById(R.id.timelineFragment_timelineListView);
		mTimelineListView.setOnItemClickListener(this);
		/*ActionItem addItem = new ActionItem(ID_ADD, "Add", getResources()
				.getDrawable(R.drawable.ic_add));
		ActionItem acceptItem = new ActionItem(ID_ACCEPT, "Accept",
				getResources().getDrawable(R.drawable.ic_accept));
		ActionItem uploadItem = new ActionItem(ID_UPLOAD, "Upload",
				getResources().getDrawable(R.drawable.ic_up));*/

//		 mQuickAction = new QuickAction(getActivity()
//				.getApplicationContext());

		/*mQuickAction.addActionItem(addItem);
		mQuickAction.addActionItem(acceptItem);
		mQuickAction.addActionItem(uploadItem);
		mQuickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction quickAction, int pos,
							int actionId) {
						ActionItem actionItem = quickAction.getActionItem(pos);

						if (actionId == ID_ADD) { // Add item selected
							Toast.makeText(
									getActivity().getApplicationContext(),
									"Add item selected on row " + mSelectedRow,
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(
									getActivity().getApplicationContext(),
									actionItem.getTitle()
											+ " item selected on row "
											+ mSelectedRow, Toast.LENGTH_SHORT)
									.show();
						}
					}
				});

		// setup on dismiss listener, set the icon back to normal
		mQuickAction.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
			}
		});*/

	}

	private void initDataConfig() {
		mLoginVisible = false;
		mRefreshing = false;
		mTimelineAdapter = new TimelineStatusAdapter(this.getActivity());
		mTimelineListView.setAdapter(mTimelineAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		updateUI();
	}
	private void updateUI() {
		if (!TwitterHelper.isLoggedIn(this.getActivity())) {
			if (!mLoginVisible) {
				TwitterLoginFragment.showLoginFragment(this.getActivity(),
						new ITwitterLoginListener() {
							@Override
							public void onLoginSuccessful() {
								mLoginVisible = false;
								refreshTimeline();
							}
						});
				mLoginVisible = true;
			}
		} else {
			refreshTimeline();
		}
	}

	
	private void refreshTimeline() {
		if (TwitterHelper.isLoggedIn(this.getActivity())) {
			mRefreshing = true;
			if (mTimelineAdapter.isEmpty()) {
				getActivity().getSupportLoaderManager().initLoader(
						__TIMELINE_LOADER_ID__, null, this);
			} else {
				getActivity().getSupportLoaderManager().restartLoader(
						__TIMELINE_LOADER_ID__, null, this);
			}
		}

		reloadMenu();
	}

	private void logout() {
		TwitterHelper.logout(getActivity());
		
		this.getActivity().getSupportLoaderManager()
				.destroyLoader(__TIMELINE_LOADER_ID__);
		
		mTimelineAdapter.clear();
		
		updateUI();
	}

	/**
	 * The Menu is how the User can both refresh the Timeline and Logout, so
	 * updating its state is very important, so we have a method to deal with it
	 * specifically.
	 */
	private void reloadMenu() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActivity().invalidateOptionsMenu();
		} else {
			// alternative method from the Android Support Library
			ActivityCompat.invalidateOptionsMenu(getActivity());
		}
	}

	/*
	 * Loader Callbacks implementation
	 */
	public Loader<ResponseList<Status>> onCreateLoader(int loaderId, Bundle args) {
		return new TwitterTimelineLoader(this.getActivity(),
				TwitterHelper.makeOAuthAccessToken(this.getActivity()));
	}

	@Override
	public void onLoadFinished(Loader<ResponseList<Status>> loader,
			ResponseList<Status> newStatus) {
		mTimelineAdapter.clear();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (newStatus != null) {
				// new API added at Level 11
				for (Status status : newStatus) {
					// if(status.getUser().getScreenName().equalsIgnoreCase("ChennaiIPL"))
					// {
					mTimelineAdapter.add(status);
					// }
				}

			}
		} else {
			if (newStatus != null) {
				
				for (Status status : newStatus) {
					// if(status.getUser().getScreenName().equalsIgnoreCase("ChennaiIPL"))
					// {
					mTimelineAdapter.add(status);
					// }
				}
			}
		}

		mRefreshing = false;
		// the user can refresh again now
		reloadMenu();
	}

	@Override
	public void onLoaderReset(Loader<ResponseList<Status>> loader) {
		
	}

	/*
	 * Menu handling
	 */

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if (TwitterHelper.isLoggedIn(this.getActivity())) {
			if (!mRefreshing) {
				menu.add(
						0,
						__MENU_REFRESH_OPTION__,
						0,
						R.string.refresh);
			}
			menu.add(
					0,
					__MENU_LOGOUT_OPTION__,
					0,
					R.string.logout);
		} else {
			menu.clear();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case __MENU_LOGOUT_OPTION__:
			logout();
			return true;
		case __MENU_REFRESH_OPTION__: {
			updateUI();
			return true;
		}
		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		mSelectedRow = position; //set the selected row
	//	mQuickAction.show(view);
		
	}

}
