package com.pracify;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.pracify.authenticator.AccountAuthenticator;
import com.pracify.contentprovider.FileDetailsContract;
import com.pracify.db.FileDetailsTableHandler;
import com.pracify.db.tableClasses.FileDetails;
import com.pracify.listview.CustomAdapter;
import com.pracify.listview.ListModel;
import com.pracify.util.CommonHelpers;
import com.pracify.util.PracifyConstants;

public class HomeActivity extends ActionBarActivity {

	public static final String ACTION_FINISHED_SYNC = "com.pracify.ACTION_FINISHED_SYNC";
	private static IntentFilter syncIntentFilter = new IntentFilter(
			ACTION_FINISHED_SYNC);

	ListView fileListView;
	CustomAdapter adapter;
	List<FileDetails> fileDetailsList;
	Resources mRes;

	private static final String LOG_TAG = "HomeActivity";
	public ArrayList<ListModel> CustomListViewValuesArr = new ArrayList<ListModel>();
	private Account mAccount;

	// Sync interval constants
	public static final long SECONDS_PER_MINUTE = 60L;
	public static final long SYNC_INTERVAL_IN_MINUTES = 60L;
	public static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES
			* SECONDS_PER_MINUTE;

	private BroadcastReceiver syncBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Sync Completed. Update List");
			refereshList(null);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		AccountManager am = AccountManager.get(this);

		mAccount = am.getAccountsByType(AccountAuthenticator.ACCOUNT_TYPE)[0];

		/******** Take some data in Arraylist ( CustomListViewValuesArr ) ***********/
		setListData(null);

		fileListView = (ListView) findViewById(R.id.listView);

		mRes = getResources();

		/**************** Create Custom Adapter *********/
		adapter = new CustomAdapter(this, CustomListViewValuesArr, mRes);
		fileListView.setAdapter(adapter);

		ContentResolver mResolver = getContentResolver();

		/******** Turn on periodic Sync ******/
		ContentResolver.addPeriodicSync(mAccount,
				FileDetailsContract.AUTHORITY, new Bundle(), SYNC_INTERVAL);

		/*
		 * Create a content observer object. Its code does not mutate the
		 * provider, so set selfChange to "false"
		 */
		TableObserver observer = new TableObserver(null);
		/*
		 * Register the observer for the data table. The table's path and any of
		 * its subpaths trigger the observer.
		 */
		mResolver.registerContentObserver(FileDetailsContract.CONTENT_URI,
				true, observer);
	}

	public class TableObserver extends ContentObserver {

		public TableObserver(Handler handler) {
			super(handler);
		}

		/*
		 * Define a method that's called when data in the observed content
		 * provider changes. This method signature is provided for compatibility
		 * with older platforms.
		 */
		@Override
		public void onChange(boolean selfChange) {
			/*
			 * Invoke the method signature available as of Android platform
			 * version 4.1, with a null URI.
			 */
			onChange(selfChange, null);
		}

		/*
		 * Define a method that's called when data in the observed content
		 * provider changes.
		 */
		@Override
		public void onChange(boolean selfChange, Uri changeUri) {
			/*
			 * Ask the framework to run your sync adapter. To maintain backward
			 * compatibility, assume that changeUri is null.
			 */
			ContentResolver.requestSync(mAccount,
					FileDetailsContract.AUTHORITY, null);
		}
	}

	/**
	 * Respond to Sync click by calling requestSync(). This is an asynchronous
	 * operation.
	 * 
	 * This method is attached to the refresh button in the layout XML file
	 * 
	 * @param v
	 *            The View associated with the method call, in this case a
	 *            Button
	 */
	public void onRefreshButtonClick() {
		// Pass the settings flags by inserting them in a bundle
		Bundle settingsBundle = new Bundle();
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		/*
		 * Request the sync for the default account, authority, and manual sync
		 * settings
		 */
		ContentResolver.requestSync(mAccount, FileDetailsContract.AUTHORITY,
				settingsBundle);
	}

	/****** Function to set data in ArrayList *************/
	public void setListData(String fileName) {

		CustomListViewValuesArr.clear();

		FileDetailsTableHandler fileDetailsTableHandler = new FileDetailsTableHandler(
				this);

		fileDetailsList = fileDetailsTableHandler.getAllFileDetails(fileName);

		for (FileDetails fileDetails : fileDetailsList) {

			final ListModel fileListModel = new ListModel();

			/******* Firstly take data in model object ******/
			fileListModel.setFileName(fileDetails.getName());
			fileListModel.setFileOwner(fileDetails.getOwner());
			fileListModel.setDateTime(fileDetails.getCreation_date());

			/******** Take Model Object in ArrayList **********/
			CustomListViewValuesArr.add(fileListModel);
		}

	}

	/***************** This function used by adapter ****************/
	public void onItemClick(int mPosition) {

		String fileID = fileDetailsList.get(mPosition).getId();
		Log.d(LOG_TAG, "File ID : " + fileID);
		Intent intent = new Intent(this, SaveRecordingActivity.class);
		intent.putExtra(PracifyConstants.fileID, fileID);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(syncBroadcastReceiver, syncIntentFilter);
		refereshList(null);
	}

	private void refereshList(String fileName) {

		setListData(fileName);
		adapter = new CustomAdapter(this, CustomListViewValuesArr, mRes);
		fileListView.setAdapter(adapter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(syncBroadcastReceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home, menu);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) MenuItemCompat
				.getActionView(searchItem);

		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setQueryHint(getString(R.string.search_hint));
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(String query) {

				Log.d(LOG_TAG, "Search Query onQueryTextChange : " + query);
				refereshList(query);

				return true;

			}

			@Override
			public boolean onQueryTextSubmit(String arg0) {

				Log.d(LOG_TAG, "Search Query onQueryTextSubmit : " + arg0);
				refereshList(arg0);

				return true;
			}

		});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_new:
			Intent intent = new Intent(this, RecordingActivity_New.class);
			startActivity(intent);
			return true;
		case R.id.action_refresh:
			onRefreshButtonClick();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		try {
			File dir = this.getCacheDir();
			if (dir != null && dir.isDirectory()) {
				CommonHelpers.deleteDir(dir);
			}
		} catch (Exception e) {
			CommonHelpers.showLongToast(this,
					"Unable to delete Cache directory!!");
			e.printStackTrace();
		}
	}
}
