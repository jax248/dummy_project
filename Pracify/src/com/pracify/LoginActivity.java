package com.pracify;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.pracify.db.UserDetailsTableHandler;
import com.pracify.js.Login;
import com.pracify.util.MyAppWebViewClient;

public class LoginActivity extends ActionBarActivity {

	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		UserDetailsTableHandler userDetailsTableHandler = new UserDetailsTableHandler(
				this);
		Login loginJS = new Login(this);

		if (userDetailsTableHandler.isUserLoggedIn()) {

			Intent intent = new Intent(this, HomeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}

		mWebView = (WebView) findViewById(R.id.activity_main_webview);

		mWebView.addJavascriptInterface(loginJS, "Login");

		mWebView.loadUrl("file:///android_asset/login.html");

		// Enable Javascript
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);

		// Force links and redirects to open in the WebView instead of in a
		// browser
		mWebView.setWebViewClient(new MyAppWebViewClient());
	}

	public void showHTMLError(String msg) {

		String javaScript = "javascript:showErrorMsg('" + msg + "')";

		Log.d("LoginActivity", javaScript);
		mWebView.loadUrl(javaScript);
	}

	// Detect when the back button is pressed
	@Override
	public void onBackPressed() {
		if (mWebView.canGoBack()) {
			mWebView.goBack();
		} else {
			// Let the system handle the back button
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
