package com.pracify.js;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.pracify.util.CommonHelpers;
import com.pracify.util.FileExtensionFilter;
import com.pracify.util.PracifyConstants;

public class RecordAudio {

	private static final String LOG_TAG = "AudioRecordTest";
	private String mFileName = null;
	private ActionBarActivity activity;

	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;

	public RecordAudio(ActionBarActivity activity) {

		this.activity = activity;
	}

	private String getOutputDir() {
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

		return outputDir;
	}

	public void startPlaying() {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(mFileName);
			mPlayer.prepare();
			mPlayer.start();
			CommonHelpers.showLongToast(activity, "Playing Started");
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
			CommonHelpers.showLongToast(activity, "Error Playing");
		}
	}

	@JavascriptInterface
	public void startPlaying(String filePath) {
		mFileName = filePath;
		if (mPlayer.isPlaying()) {
			mPlayer.release();
			mPlayer = null;
		}
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(mFileName);
			mPlayer.prepare();
			mPlayer.start();
			CommonHelpers.showLongToast(activity, "Playing Audio");
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
			CommonHelpers.showLongToast(activity, "Error Playing");
		}
	}

	@JavascriptInterface
	public void stopPlaying() {
		mPlayer.release();
		mPlayer = null;
		CommonHelpers.showLongToast(activity, "Playing Stopped");
	}

	@JavascriptInterface
	public void startRecording() {

		String dateInString = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
				.format(new Date()).toString();

		String fileName = dateInString + ".3gp";
		String outputDir = getOutputDir();
		mFileName = outputDir + "/" + fileName;

		Log.d(LOG_TAG, "File Path : " + mFileName);

		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
			mRecorder.start();
			CommonHelpers.showLongToast(activity, "Recording Started");
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

	@JavascriptInterface
	public String listFiles() {
		CommonHelpers.showLongToast(activity, "Getting Files");
		ArrayList<HashMap<String, String>> songsList = getPlayList();

		JSONObject obj = new JSONObject();
		JSONArray arr = new JSONArray();
		try {
			for (int i = 0; i < songsList.size(); i++) {
				Log.d("Music File", songsList.get(i).get("fileTitle"));

				obj.put("fileTitle", songsList.get(i).get("fileTitle"));
				obj.put("filePath", songsList.get(i).get("filePath"));

				arr.put(obj);

				obj = new JSONObject();
			}

			return arr.toString();
		} catch (Exception e) {

			Log.e("Music File", "Error!! " + e.getMessage());
			return null;
		}
	}

	/**
	 * Function to read all mp3 files from sdcard and store the details in
	 * ArrayList
	 * */
	private ArrayList<HashMap<String, String>> getPlayList() {
		String outputDir = getOutputDir();
		File home = new File(outputDir);

		ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
		if (home.listFiles(new FileExtensionFilter()).length > 0) {
			for (File file : home.listFiles(new FileExtensionFilter())) {
				HashMap<String, String> song = new HashMap<String, String>();
				song.put(
						"fileTitle",
						file.getName().substring(0,
								(file.getName().length() - 4)));
				song.put("filePath", file.getPath());

				// Adding each song to SongList
				songsList.add(song);
			}
		}
		// return songs list array
		return songsList;
	}
}
