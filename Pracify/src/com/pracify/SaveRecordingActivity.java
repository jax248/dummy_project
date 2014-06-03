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
	
	private static final String LOG_TAG = "SaveRecording";
	private String mFileName = null;

	EditText fileName, fileDescription;
	String fileID;
	FileDetails fileDetails = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save_recording);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		fileName = (EditText) findViewById(R.id.editText1);
		fileDescription = (EditText) findViewById(R.id.editText2);

		Intent intent = getIntent();
		fileID = intent.getStringExtra(PracifyConstants.fileID);
		if (!(fileID == null || fileID.isEmpty() || fileID.contains("NULL"))) {

			FileDetailsTableHandler fileDetailsTableHandler = new FileDetailsTableHandler(
					this);

			fileDetails = fileDetailsTableHandler.getFileDetails(fileID);

			if (fileDetails != null) {
				fileName.setText(fileDetails.getName());
				fileDescription.setText(fileDetails.getDesc());
				mFileName = fileDetails.getPath();
			} else {
				CommonHelpers.showLongToast(this, "Error getting file!!");
			}

		} else {
			mFileName = intent.getStringExtra(PracifyConstants.filePathIntent);
			fileName.setText(CommonHelpers.getCurrentTimestamp());
		}
		Log.d(LOG_TAG, "Got File Path : " + mFileName);
	}

	public void saveRecord(View view) {

		String name, description, path, owner, group;

		name = fileName.getText().toString();

		if (name.isEmpty()) {
			CommonHelpers.showLongToast(this, "Please enter a Valid Name");
			return;
		}

		description = fileDescription.getText().toString();

		if (fileDetails == null) {

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

		} else {

			group = "DummyGroup";
			path = fileDetails.getPath();
			owner = fileDetails.getOwner();
			String id = fileDetails.getId();

			fileDetails = new FileDetails(id, name, description, path, owner,
					group);

			FileDetailsTableHandler fileDetailsTableHandler = new FileDetailsTableHandler(
					this);
			if (!(fileDetailsTableHandler.updateFileDetails(fileDetails) > 0)) {
				CommonHelpers.showLongToast(this,
						"Unable to update record. Please try again later!");
			}
		}
		finish();
	}

	private String copyToAppFolder(String path, String name) {

		String newPath = CommonHelpers.getOutputDir(this) + "/" + name
				+ PracifyConstants.musicFileExtension;

		File src = new File(path);
		File dst = new File(newPath);

		CommonHelpers.copyFile(src, dst);

		return newPath;
	}

	public void playRecording(View view) {
		Intent intent = new Intent(this, Visualize.class);
		intent.putExtra(PracifyConstants.filePathIntent, mFileName);
		intent.putExtra(PracifyConstants.fileID, "NULL");
		startActivity(intent);
	}

	public void cancelRecord(View view) {
		finish();
	}
}
