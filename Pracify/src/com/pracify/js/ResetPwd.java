package com.pracify.js;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.pracify.HomeActivity;
import com.pracify.LoginActivity;
import com.pracify.db.UserDetailsTableHandler;
import com.pracify.db.tableClasses.UserDetails;
import com.pracify.network.AsyncJSONParser;
import com.pracify.network.AsyncTaskCompleteListener;
import com.pracify.util.CommonHelpers;
import com.pracify.util.PracifyConstants;

public class ResetPwd implements AsyncTaskCompleteListener<JSONObject> {

	private LoginActivity activity;

	public ResetPwd(LoginActivity activity) {
		this.activity = activity;
	}

	/**
	 * function make Login Request
	 * 
	 * @param email
	 * @param password
	 * */
	@JavascriptInterface
	public void performResetpwd(String email) {

		Log.d("Resetpwd", "Performing Reset Password");

		ConnectivityManager connMgr = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {

			Log.d("Login", "Network Available");

			NameValuePair urlValue = new BasicNameValuePair("url",
					PracifyConstants.resetpwdURL);
			NameValuePair emailValue = new BasicNameValuePair("email_id", email);
			//NameValuePair pwdValue = new BasicNameValuePair("password",
				//	password);

			new AsyncJSONParser(this).execute(urlValue, emailValue);
			//activity.showHTMLError(json.getString("msg"));
		} else {

			Log.e("Login", "No network available");
			activity.showHTMLError("Error! Please check your internet connection");
		}
	}

	@Override
	public void onTaskComplete(JSONObject json) {

		try {
			if (json.getBoolean("resetpwd")) {

				Log.d("ResetPwd", "Valid EmailID.");
				//CommonHelpers.showLongToast(activity, "Please Check Your Mail");
				activity.showHTMLError("Password reset link has been sent to your mail.");
				} else {

				Log.e("ResetPwd", "Error Reseting password. Returning message");

				activity.showHTMLError(json.getString("msg"));
			}
		} catch (JSONException e) {

			e.printStackTrace();
			Log.e("ResetPwd", e.getMessage());
		}
	}
}
