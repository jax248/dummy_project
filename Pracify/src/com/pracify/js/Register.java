package com.pracify.js;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.pracify.LoginActivity;
import com.pracify.network.AsyncJSONParser;
import com.pracify.network.AsyncTaskCompleteListener;
import com.pracify.util.PracifyConstants;

public class Register implements AsyncTaskCompleteListener<JSONObject> {

	private LoginActivity activity;

	public Register(LoginActivity activity) {
		this.activity = activity;
	}

	/**
	 * function make Register User
	 * 
	 * @param email
	 * @param password
	 * */
	@JavascriptInterface
	public void registerUser(String email, String password, String user_name) {

		Log.d("Register", "Register user");

		ConnectivityManager connMgr = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {

			Log.d("Register", "Network Available");

			NameValuePair urlValue = new BasicNameValuePair("url",
					PracifyConstants.registerURL);
			NameValuePair userNameValue = new BasicNameValuePair("user_name",
					user_name);
			NameValuePair emailValue = new BasicNameValuePair("email_id", email);
			NameValuePair pwdValue = new BasicNameValuePair("password",
					password);

			new AsyncJSONParser(this).execute(urlValue, userNameValue,
					emailValue, pwdValue);
		} else {

			Log.e("Register", "No network available");
			activity.showHTMLError("Error! Please check your internet connection");
		}
	}

	@Override
	public void onTaskComplete(JSONObject json) {

		try {
			if (json.getBoolean("register")) {

				Log.d("Register",
						"Registered. Show alert and take to Login page");
				activity.registerSuccess();
			} else {

				Log.d("Register", "Error. Returning message");

				activity.showHTMLError(json.getString("msg"));
			}
		} catch (JSONException e) {

			e.printStackTrace();
			Log.e("Register", e.getMessage());
		}
	}
}
