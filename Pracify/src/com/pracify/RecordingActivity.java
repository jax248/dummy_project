package com.pracify;

import java.io.File;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.pracify.util.CommonHelpers;
import com.pracify.util.PracifyConstants;

public class RecordingActivity extends ActionBarActivity {

	private Button startRecording, stopRecording;
	private static final String LOG_TAG = "Recording";
	private String mFileName = null;

	private MediaRecorder mRecorder = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recording);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		startRecording = (Button) findViewById(R.id.start_record);
		stopRecording = (Button) findViewById(R.id.stop_record);

		stopRecording.setEnabled(false);

	}

	public void startRecording(View view) {
		try {
			File outputDir = this.getCacheDir();
			File outputFile = File
					.createTempFile("pracify_", ".3gp", outputDir);

			mFileName = outputFile.getAbsolutePath();

			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mRecorder.setOutputFile(mFileName);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			mRecorder.prepare();
			mRecorder.start();
			startRecording.setEnabled(false);
			stopRecording.setEnabled(true);
			CommonHelpers.showLongToast(this, "Recording Started");
		} catch (Exception e) {
			CommonHelpers.showLongToast(this, "Error!! Try again later.");
			Log.e(LOG_TAG, "Recording failed : " + e.getMessage());
		}
	}

	public void stopRecording(View view) {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
		startRecording.setEnabled(true);
		stopRecording.setEnabled(false);
		Log.d(LOG_TAG, "Saved File Path : " + mFileName);
		CommonHelpers.showLongToast(this, "Recording Stopped");
		Intent intent = new Intent(this, SaveRecordingActivity.class);
		intent.putExtra(PracifyConstants.filePathIntent, mFileName);
		intent.putExtra(PracifyConstants.fileID, "NULL");
		startActivity(intent);
		finish();
	}
}
