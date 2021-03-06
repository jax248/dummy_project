package com.pracify;

import java.io.File;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.pracify.util.CommonHelpers;
import com.pracify.util.PracifyConstants;

public class RecordingActivity_New extends ActionBarActivity {

	private static final String LOG_TAG = "Recording";
	static final private double EMA_FILTER = 0.6;

	private double mEMA = 0.0;
	private String mFileName = null;

	private MediaRecorder mRecorder = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recording_new);

	}

	public void startRecording(View view) {
		try {
			File outputDir = this.getCacheDir();
			File outputFile = File.createTempFile("pracify_",
					PracifyConstants.musicFileExtension, outputDir);

			mFileName = outputFile.getAbsolutePath();

			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			mRecorder.setOutputFile(mFileName);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			mRecorder.prepare();
			mRecorder.start();
			// startRecording.setEnabled(false);
			// stopRecording.setEnabled(true);
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
		// startRecording.setEnabled(true);
		// stopRecording.setEnabled(false);
		Log.d(LOG_TAG, "Saved File Path : " + mFileName);
		CommonHelpers.showLongToast(this, "Recording Stopped");
		Intent intent = new Intent(this, SaveRecordingActivity.class);
		intent.putExtra(PracifyConstants.filePathIntent, mFileName);
		intent.putExtra(PracifyConstants.fileID, "NULL");
		startActivity(intent);
		finish();
	}

	public double getAmplitude() {
		if (mRecorder != null)
			return (mRecorder.getMaxAmplitude() / 2700.0);
		else
			return 0;

	}

	public double getAmplitudeEMA() {
		double amp = getAmplitude();
		mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
		return mEMA;
	}
}
