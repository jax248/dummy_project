package com.pracify.util;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyAppWebViewClient extends WebViewClient {

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {

		if (Uri.parse(url).getHost().length() == 0) {
			return false;
		}

		if (Uri.parse(url).getHost().endsWith(PracifyConstants.basServer)) {
			return false;
		}

		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		view.getContext().startActivity(intent);
		return true;
	}
}