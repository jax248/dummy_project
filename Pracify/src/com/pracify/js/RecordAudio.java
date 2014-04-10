package com.pracify.js;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import android.media.MediaRecorder;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.pracify.util.PracifyConstants;

public class RecordAudio {

	private static final String LOG_TAG = "AudioRecordTest";
	private String mFileName = null;

	private MediaRecorder mRecorder = null;

	public RecordAudio() {

		Date date = new Date();
		mFileName = PracifyConstants.externalStoragePath + "/"
				+ (new Timestamp(date.getTime())) + ".3gp";
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
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}

		mRecorder.start();
	}

	@JavascriptInterface
	public void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}
}
