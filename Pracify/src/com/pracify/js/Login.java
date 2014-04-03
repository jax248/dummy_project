package com.pracify.js;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.pracify.HomeActivity;
import com.pracify.network.NetworkOperations;

public class Login {

	private Activity activity;

	public Login(Activity activity) {
		this.activity = activity;
	}

	/**
	 * function make Login Request
	 * 
	 * @param email
	 * @param password
	 * */
	@JavascriptInterface
	public String performLogin(String email, String password) {

		Log.d("Login", "Performing Login");

		NetworkOperations networkOperations = new NetworkOperations();

		JSONObject json = networkOperations.loginUser(email, password);

		try {

			if (json.getBoolean("login")) {

				Log.d("Login", "Valid login. Starting new activity");

				Intent intent = new Intent(activity, HomeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				activity.startActivity(intent);
				activity.finish();

				Log.d("Login", "Started new activity");

				return null;
			} else {

				Log.d("Login", "Error login. Returning message");

				return json.getString("msg");
			}
		} catch (Exception e) {

			Log.e("Login", e.getMessage());
			return "Error parsing server response!";
		}
	}
}
