package com.pracify;

import java.io.File;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pracify.db.FileDetailsTableHandler;
import com.pracify.db.UserDetailsTableHandler;
import com.pracify.db.tableClasses.FileDetails;
import com.pracify.util.CommonHelpers;
import com.pracify.util.PracifyConstants;

public class SaveRecordingActivity extends ActionBarActivity {

	private MediaPlayer myPlayer;
	private Button startPlaying, stopPlay;
	private static final String LOG_TAG = "SaveRecording";
	private String mFileName = null;

	EditText fileName, fileDescription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save_recording);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		startPlaying = (Button) findViewById(R.id.startPlay);
		stopPlay = (Button) findViewById(R.id.stopPlay);

		Intent intent = getIntent();
		mFileName = intent.getStringExtra(PracifyConstants.filePathIntent);
		Log.d(LOG_TAG, "Got File : " + mFileName);

		fileName = (EditText) findViewById(R.id.editText1);
		fileDescription = (EditText) findViewById(R.id.editText2);

		fileName.setText(CommonHelpers.getCurrentTimestamp());

	}

	public void saveRecord(View view) {

		String name, description, path, owner, group;
		FileDetails fileDetails;

		name = fileName.getText().toString();

		if (name.isEmpty()) {
			CommonHelpers.showLongToast(this, "Please enter a Valid Name");
			return;
		}

		description = fileDescription.getText().toString();

		path = copyToAppFolder(mFileName, name);

		UserDetailsTableHandler userDetailsTableHandler = new UserDetailsTableHandler(
				this);
		owner = userDetailsTableHandler.getUserID();

		if (owner.contains("Error")) {
			CommonHelpers.showLongToast(this, owner);
			return;
		}

		group = "DummyGroup";

		fileDetails = new FileDetails(name, description, path, owner, group);

		FileDetailsTableHandler fileDetailsTableHandler = new FileDetailsTableHandler(
				this);
		fileDetailsTableHandler.addFileDetails(fileDetails);

		finish();
	}

	private String copyToAppFolder(String path, String name) {

		String newPath = CommonHelpers.getOutputDir(this) + "/" + name + ".3gp";

		File src = new File(path);
		File dst = new File(newPath);

		CommonHelpers.copyFile(src, dst);

		return newPath;
	}

	public void playRecording(View view) {
		try {
			myPlayer = new MediaPlayer();
			myPlayer.setDataSource(mFileName);
			myPlayer.setLooping(true);
			myPlayer.prepare();
			myPlayer.start();

			startPlaying.setEnabled(false);
			stopPlay.setEnabled(true);

			CommonHelpers.showLongToast(this, "Start play the recording...");
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
			e.printStackTrace();
		}
	}

	public void stopPlay(View view) {
		try {
			if (myPlayer != null) {
				myPlayer.stop();
				myPlayer.release();
				myPlayer = null;
				startPlaying.setEnabled(true);
				stopPlay.setEnabled(false);
				CommonHelpers.showLongToast(this,
						"Stop playing the recording...");
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
			e.printStackTrace();
		}
	}
}
