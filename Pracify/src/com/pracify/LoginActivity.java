package com.pracify;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.pracify.db.UserDetailsTableHandler;
import com.pracify.js.Login;
import com.pracify.js.Register;
import com.pracify.js.ResetPwd;
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
		Register registerJS = new Register(this);
		ResetPwd resetJS = new ResetPwd(this);
		if (userDetailsTableHandler.isUserLoggedIn()) {

			Intent intent = new Intent(this, HomeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}

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
