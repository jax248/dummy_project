package com.pracify;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.pracify.js.RecordAudio;
import com.pracify.util.MyAppWebViewClient;

public class HomeActivity extends ActionBarActivity {

	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);

		mWebView = (WebView) findViewById(R.id.activity_home_webview);

		mWebView.loadUrl("file:///android_asset/record.html");

		RecordAudio recordAudioJS = new RecordAudio(this);
		mWebView.addJavascriptInterface(recordAudioJS, "RA");

		// Enable Javascript
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);

		// Force links and redirects to open in the WebView instead of in a
		// browser
		mWebView.setWebViewClient(new MyAppWebViewClient());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
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
