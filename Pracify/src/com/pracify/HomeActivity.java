package com.pracify;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.pracify.db.FileDetailsTableHandler;
import com.pracify.db.tableClasses.FileDetails;
import com.pracify.listview.CustomAdapter;
import com.pracify.listview.ListModel;
import com.pracify.util.CommonHelpers;
import com.pracify.util.PracifyConstants;

public class HomeActivity extends ActionBarActivity {

	ListView fileListView;
	CustomAdapter adapter;
	List<FileDetails> fileDetailsList;
	private static final String LOG_TAG = "HomeActivity";
	public ArrayList<ListModel> CustomListViewValuesArr = new ArrayList<ListModel>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		/******** Take some data in Arraylist ( CustomListViewValuesArr ) ***********/
		setListData();

		fileListView = (ListView) findViewById(R.id.listView);

		Resources res = getResources();

		/**************** Create Custom Adapter *********/
		adapter = new CustomAdapter(this, CustomListViewValuesArr, res);
		fileListView.setAdapter(adapter);
	}

	/****** Function to set data in ArrayList *************/
	public void setListData() {

		CustomListViewValuesArr.clear();

		FileDetailsTableHandler fileDetailsTableHandler = new FileDetailsTableHandler(
				this);

		fileDetailsList = fileDetailsTableHandler.getAllFileDetails();

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
		setListData();
		Resources res = getResources();

		/**************** Create Custom Adapter *********/
		adapter = new CustomAdapter(this, CustomListViewValuesArr, res);
		fileListView.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home, menu);

		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) MenuItemCompat
				.getActionView(searchItem);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_search:
			CommonHelpers.showLongToast(this, "Search Action");
			return true;
		case R.id.action_new:
			Intent intent = new Intent(this, RecordingActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_refresh:
			CommonHelpers.showLongToast(this, "Sync Action");
			return true;
		case R.id.action_settings:
			CommonHelpers.showLongToast(this, "Settings Action");
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
