package com.pracify.js;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.media.MediaRecorder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.pracify.util.CommonHelpers;
import com.pracify.util.PracifyConstants;

public class RecordAudio {

	private static final String LOG_TAG = "AudioRecordTest";
	private String mFileName = null;
	private ActionBarActivity activity;

	private MediaRecorder mRecorder = null;

	public RecordAudio(ActionBarActivity activity) {

		this.activity = activity;
		String dateInString = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
				.format(new Date()).toString();

		String fileName = "Pracify_" + dateInString + " record.3gp";
		String outputDir;
		File file;
		if (PracifyConstants.isSDPresent) {
			outputDir = PracifyConstants.externalStoragePath;
			file = new File(outputDir);
		} else {
			file = new File(activity.getFilesDir(),
					PracifyConstants.recordingPath);
			outputDir = file.getAbsolutePath();
		}

		file.mkdirs();
		mFileName = outputDir + "/" + fileName;

		Log.d(LOG_TAG, "File Path : " + mFileName);
	}

	@JavascriptInterface
	public void startRecording() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
			CommonHelpers.showLongToast(activity, "Recording Started");
			mRecorder.start();
		} catch (Exception e) {
			CommonHelpers.showLongToast(activity, "Error!! Try again later.");
			Log.e(LOG_TAG, "prepare() failed : " + e.getMessage());
		}
	}

	@JavascriptInterface
	public void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
		CommonHelpers.showLongToast(activity, "Recording Stopped");
	}
}
