package com.pracify;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.pracify.authenticator.AccountAuthenticator;
import com.pracify.db.UserDetailsTableHandler;
import com.pracify.db.tableClasses.UserDetails;
import com.pracify.js.Login;
import com.pracify.js.Register;
import com.pracify.js.ResetPwd;
import com.pracify.util.MyAppWebViewClient;

public class LoginActivity extends AccountAuthenticatorActivity {

	private WebView mWebView;

	private static final String TAG = "LoginActivity";

	public static final String PARAM_AUTHTOKEN_TYPE = "pracify.auth.token";
	public static final String PARAM_CREATE = "create";

	public static final int REQ_CODE_CREATE = 1;

	public static final int REQ_CODE_UPDATE = 2;

	public static final String EXTRA_REQUEST_CODE = "req.code";

	public static final int RESP_CODE_SUCCESS = 0;

	public static final int RESP_CODE_ERROR = 1;

	public static final int RESP_CODE_CANCEL = 2;

	public static final String PARAM_USERNAME = "user_name";
	public static final String PARAM_CONFIRMCREDENTIALS = "confirm_credentials";

	private AccountManager mAccountManager;
	private String mAuthtoken;
	private String mAuthtokenType;

	/**
	 * If set we are just checking that the user knows their credentials; this
	 * doesn't cause the user's password to be changed on the device.
	 */
	private Boolean mConfirmCredentials = false;

	/** Was the original caller asking for an entirely new account? */
	protected boolean mRequestNewAccount = false;

	private String mUsername, mPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		mAccountManager = AccountManager.get(this);

		final Intent intent = getIntent();
		mUsername = intent.getStringExtra(PARAM_USERNAME);
		mAuthtokenType = intent.getStringExtra(PARAM_AUTHTOKEN_TYPE);
		mRequestNewAccount = mUsername == null;
		mConfirmCredentials = intent.getBooleanExtra(PARAM_CONFIRMCREDENTIALS,
				false);

		Login loginJS = new Login(this);
		Register registerJS = new Register(this);
		ResetPwd resetJS = new ResetPwd(this);

		mWebView = (WebView) findViewById(R.id.activity_main_webview);

		mWebView.addJavascriptInterface(loginJS, "Login");
		mWebView.addJavascriptInterface(registerJS, "Register");
		mWebView.addJavascriptInterface(resetJS, "ResetPwd");
		mWebView.loadUrl("file:///android_asset/login.html");

		// Enable Javascript
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);

		// Force links and redirects to open in the WebView instead of in a
		// browser
		mWebView.setWebViewClient(new MyAppWebViewClient());
	}

	public void loginTask(JSONObject json, String email_id, String password) {

		boolean result = false;
		try {
			if (json != null) {
			if (json.getBoolean("login")) {

				mUsername = email_id;
				mPassword = password;

				UserDetailsTableHandler userDetailsTableHandler = new UserDetailsTableHandler(
						this);

				UserDetails userDetails = new UserDetails(
						json.getString("email_id"),
						json.getString("user_name"), 1);

				userDetailsTableHandler.deleteAllDetails();
				userDetailsTableHandler.addUserDetails(userDetails);

				result = true;
			} else {

				Log.e("Login", "Error login. Returning message");

				showHTMLError(json.getString("msg"));
			}
			}else{
				Log.e("Login", "Error login. JSON Object is NULL");

				showHTMLError("Some Error occurred! Please try again later....");
			}
		} catch (JSONException e) {

			e.printStackTrace();
			Log.e("Login", e.getMessage());
		}

		onAuthenticationResult(result);
	}

	/**
	 * Called when the authentication process completes
	 */
	public void onAuthenticationResult(boolean result) {
		Log.i(TAG, "onAuthenticationResult(" + result + ")");
		if (result) {
			if (!mConfirmCredentials) {
				finishLogin();
			} else {
				finishConfirmCredentials(true);
			}
		}
	}

	/**
	 * Called when response is received from the server for confirm credentials
	 * request. See onAuthenticationResult(). Sets the
	 * AccountAuthenticatorResult which is sent back to the caller.
	 * 
	 * @param the
	 *            confirmCredentials result.
	 */
	protected void finishConfirmCredentials(boolean result) {
		Log.i(TAG, "finishConfirmCredentials()");
		final Account account = new Account(mUsername,
				AccountAuthenticator.ACCOUNT_TYPE);
		mAccountManager.setPassword(account, mPassword);
		final Intent intent = new Intent();
		intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, result);
		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * 
	 * Called when response is received from the server for authentication
	 * request. See onAuthenticationResult(). Sets the
	 * AccountAuthenticatorResult which is sent back to the caller. Also sets
	 * the authToken in AccountManager for this account.
	 * 
	 * @param the
	 *            confirmCredentials result.
	 */

	protected void finishLogin() {
		Log.i(TAG, "finishLogin()");
		final Account account = new Account(mUsername,
				AccountAuthenticator.ACCOUNT_TYPE);

		if (mRequestNewAccount) {
			Log.i(TAG, "Adding new Account");
			mAccountManager.addAccountExplicitly(account, mPassword, null);
			// Set contacts sync for this account.
			ContentResolver.setSyncAutomatically(account,
					ContactsContract.AUTHORITY, true);
		} else {
			mAccountManager.setPassword(account, mPassword);
		}
		Intent intent = new Intent();
		mAuthtoken = mPassword;
		intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mUsername);
		intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
		if (mAuthtokenType != null
				&& mAuthtokenType.equals(PARAM_AUTHTOKEN_TYPE)) {
			intent.putExtra(AccountManager.KEY_AUTHTOKEN, mAuthtoken);
		}
		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);

		Log.d(TAG, "Starting Home Activity");
		intent = new Intent(this, HomeActivity.class);
		startActivity(intent);

		finish();
	}

	public void showHTMLError(String msg) {

		String javaScript = "javascript:showErrorMsg('" + msg + "')";

		Log.d("LoginActivity", javaScript);
		mWebView.loadUrl(javaScript);
	}

	public void registerSuccess() {

		Toast.makeText(getApplicationContext(),
				"Registered! Please check your mail to proceed.",
				Toast.LENGTH_LONG).show();

		Log.d("LoginActivity", "Going back to login");
		mWebView.loadUrl("file:///android_asset/login.html");
	}
}
