package com.pracify.util;

import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

public class CommonHelpers {

	public static void showLongToast(ActionBarActivity activity, String msg) {
		Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
	}

	public static void showSmallToast(ActionBarActivity activity, String msg) {
		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
	}
}
