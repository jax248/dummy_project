package com.pracify;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.pracify.util.CommonHelpers;

public class HomeActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
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
