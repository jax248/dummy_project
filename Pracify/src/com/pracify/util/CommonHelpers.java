package com.pracify.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

public class CommonHelpers {

	private static final String LOG_TAG = "SaveRecording";

	public static void showLongToast(ActionBarActivity activity, String msg) {
		Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
	}

	public static void showSmallToast(ActionBarActivity activity, String msg) {
		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
	}

	public static String getCurrentTimestamp() {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd MMM, yyyy_HH:mm:ss");
		String returnDate = dateFormat.format(cal.getTime());

		returnDate = returnDate.replace("_", " at ");
		Log.d(LOG_TAG, "Return Date : " + returnDate);

		return returnDate;
	}

	public static int getRandomNumber(int digits) {

		int min = (int) Math.pow(10, digits - 1);
		int max = (int) (Math.pow(10, digits) - 1);

		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	public static String getOutputDir(Context activity) {
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

	public static boolean deleteDir(File dir) {

		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

	public static void copyFile(File src, File dst) {
		InputStream in;
		OutputStream out;
		try {
			in = new FileInputStream(src);
			out = new FileOutputStream(dst);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (Exception e) {

			Log.e(LOG_TAG, "Error in copyFile!! " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static ArrayList<HashMap<String, String>> getPlayList(
			ActionBarActivity activity) {
		String outputDir = getOutputDir(activity);
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
