package com.pracify.js;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.pracify.LoginActivity;
import com.pracify.network.JSONParser;
import com.pracify.util.PracifyConstants;

public class Login {

	private LoginActivity activity;
	String email_id, password;

	private static HttpClient mHttpClient;

	private static final String TAG = "NetworkUtilities";
	public static final String PARAM_USERNAME = "email_id";
	public static final String PARAM_PASSWORD = "password";
	public static final int REGISTRATION_TIMEOUT = 30 * 1000; // ms

	public Login(LoginActivity activity) {
		this.activity = activity;
	}

	/**
	 * Configures the httpClient to connect to the URL provided.
	 */
	public static void maybeCreateHttpClient() {
		if (mHttpClient == null) {
			mHttpClient = new DefaultHttpClient();
			final HttpParams params = mHttpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params,
					REGISTRATION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, REGISTRATION_TIMEOUT);
			ConnManagerParams.setTimeout(params, REGISTRATION_TIMEOUT);
		}
	}

	/**
	 * function make Login Request
	 * 
	 * @param email
	 * @param password
	 * */
	@JavascriptInterface
	public void performLogin(String email, String password) {

		email_id = email;
		this.password = password;

		Log.d("Login", "Performing Login");

		ConnectivityManager connMgr = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {

			Log.d("Login", "Network Available");

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			NameValuePair emailValue = new BasicNameValuePair(PARAM_USERNAME,
					email);
			NameValuePair pwdValue = new BasicNameValuePair(PARAM_PASSWORD,
					password);

			params.add(emailValue);
			params.add(pwdValue);

			JSONParser jsonParser = new JSONParser();

			JSONObject json = jsonParser.getJSONFromUrl(
					PracifyConstants.loginURL, params);

			activity.loginTask(json, email_id, password);

			// new AsyncJSONParser(this).execute(urlValue, emailValue,
			// pwdValue);
		} else {

			Log.e("Login", "No network available");
			activity.showHTMLError("Error! Please check your internet connection");
		}
	}

	public static boolean validateUser(String email, String pwd) {

		Log.d("Login", "Verify Login");

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair emailValue = new BasicNameValuePair(PARAM_USERNAME, email);
		NameValuePair pwdValue = new BasicNameValuePair(PARAM_PASSWORD, pwd);

		params.add(emailValue);
		params.add(pwdValue);

		JSONParser jsonParser = new JSONParser();

		JSONObject json = jsonParser.getJSONFromUrl(PracifyConstants.loginURL,
				params);

		try {
			if (json.getBoolean("login")) {
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
}
