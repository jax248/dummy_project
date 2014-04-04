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
import com.pracify.network.AsyncJSONParser;
import com.pracify.network.AsyncTaskCompleteListener;
import com.pracify.util.PracifyConstants;

public class Login implements AsyncTaskCompleteListener<JSONObject> {

	private LoginActivity activity;

	public Login(LoginActivity activity) {
		this.activity = activity;
	}

	/**
	 * function make Login Request
	 * 
	 * @param email
	 * @param password
	 * */
	@JavascriptInterface
	public void performLogin(String email, String password) {

		Log.d("Login", "Performing Login");

		ConnectivityManager connMgr = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {

			Log.d("Login", "Network Available");

			NameValuePair urlValue = new BasicNameValuePair("url",
					PracifyConstants.loginURL);
			NameValuePair emailValue = new BasicNameValuePair("email_id", email);
			NameValuePair pwdValue = new BasicNameValuePair("password",
					password);

			new AsyncJSONParser(this).execute(urlValue, emailValue, pwdValue);
		} else {

			Log.e("Login", "No network available");
			activity.showHTMLError("Error! Please check your internet connection");
		}
	}

	@Override
	public void onTaskComplete(JSONObject json) {

		try {
			if (json.getBoolean("login")) {

				Log.d("Login", "Valid login. Starting new activity");

				Intent intent = new Intent(activity, HomeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				activity.startActivity(intent);
				activity.finish();

				Log.d("Login", "Started new activity");
			} else {

				Log.d("Login", "Error login. Returning message");

				activity.showHTMLError(json.getString("msg"));
			}
		} catch (JSONException e) {

			e.printStackTrace();
			Log.e("Login", e.getMessage());
		}
	}
}
