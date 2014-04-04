package com.pracify.network;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.os.AsyncTask;

public class AsyncJSONParser extends AsyncTask<NameValuePair, Void, JSONObject> {

	private AsyncTaskCompleteListener<JSONObject> callback;

	public AsyncJSONParser(AsyncTaskCompleteListener<JSONObject> cb) {
		this.callback = cb;
	}

	protected void onPostExecute(JSONObject result) {
		callback.onTaskComplete(result);
	}

	@Override
	protected JSONObject doInBackground(NameValuePair... arg0) {

		NameValuePair pair;
		String url = null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		for (int i = 0; i < arg0.length; i++) {

			pair = arg0[i];
			if (pair.getName().equalsIgnoreCase("url")) {
				url = pair.getValue();
			} else {
				params.add(pair);
			}
		}

		if (null != url) {
			JSONParser jsonParser = new JSONParser();
			// getting JSON Object
			JSONObject json = jsonParser.getJSONFromUrl(url, params);
			// return json
			return json;
		} else {
			return null;
		}
	}
}
