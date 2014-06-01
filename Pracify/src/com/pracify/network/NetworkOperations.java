package com.pracify.network;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

import com.pracify.db.tableClasses.FileDetails;
import com.pracify.util.PracifyConstants;

public class NetworkOperations {

	private JSONParser jsonParser;

	// Testing in localhost using wamp or xampp
	// use http://10.0.2.2/ to connect to your localhost ie http://localhost/
	private static String loginURL = PracifyConstants.loginURL;
	private static String registerURL = PracifyConstants.registerURL;

	// constructor
	public NetworkOperations() {
		jsonParser = new JSONParser();
	}

	/**
	 * function get file list
	 * 
	 * @param url
	 * */
	public void downloadFile(String fileUrl, String filePath) {

		int count;
		try {
			URL url = new URL(fileUrl);
			URLConnection con = url.openConnection();
			con.connect();

			// downlod the file
			InputStream input = new BufferedInputStream(url.openStream());
			OutputStream output = new FileOutputStream(filePath);

			byte data[] = new byte[1024];

			while ((count = input.read(data)) != -1) {
				output.write(data, 0, count);
			}

			output.flush();
			output.close();
			input.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	/**
	 * function get file list
	 * 
	 * @param email
	 * @param password
	 * */
	public void uploadFile(FileDetails fileDetails) {
		// Building Parameters
		Log.d("NetworkOperations", "uploadFile");

		Bundle params = new Bundle();
		params.putString("file_id", fileDetails.getId());
		params.putString("file_name", fileDetails.getName());
		params.putString("file_desc", fileDetails.getDesc());
		params.putString("file_owner", fileDetails.getOwner());
		params.putString("file_group", fileDetails.getGroup());
		params.putString("file_creation_date", fileDetails.getCreation_date());

		try {

			File sourceFile = new File(fileDetails.getPath());
			String finalURL = PracifyConstants.fileUploadURL + "?"
					+ encodeUrl(params);
			URL url = new URL(finalURL);

			Log.d("NetworkOperations",
					"uploadFile : finalURL = " + url.toString());

			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(
					CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

			HttpPost httppost = new HttpPost(url.toString());

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			FileBody cbFile = new FileBody(sourceFile);
			builder.addPart("userfile", cbFile);

			httppost.setEntity(builder.build());
			Log.d("NetworkOperations",
					"Executing request " + httppost.getRequestLine());
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();

			Log.d("NetworkOperations",
					"Response Status Line : " + response.getStatusLine());
			if (resEntity != null) {

				Log.d("NetworkOperations",
						"ResEntity : " + EntityUtils.toString(resEntity));
				resEntity.consumeContent();
			}

			httpclient.getConnectionManager().shutdown();

		} catch (Exception ex) {

			Log.e("NetworkOperations -> uploadFile",
					"Exception : " + ex.getMessage(), ex);
		}
	}

	public String encodeUrl(Bundle parameters) {

		try {
			if (parameters == null) {
				return "";
			}
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (String key : parameters.keySet()) {
				if (first)
					first = false;
				else
					sb.append("&");
				Log.d(key + " = ", "" + parameters.getString(key));
				sb.append(URLEncoder.encode(key, Charset.defaultCharset()
						.name())
						+ "="
						+ URLEncoder.encode(parameters.getString(key), Charset
								.defaultCharset().name()));
			}

			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * function get file list
	 * 
	 * @param email
	 * @param password
	 * */
	public JSONObject getFileList(String email) {
		// Building Parameters
		Log.d("NetworkOperations", "getFileList Email : " + email);
		NameValuePair nameValuePair = new BasicNameValuePair("email_id", email);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(nameValuePair);

		JSONObject result = jsonParser.getJSONFromUrl(
				PracifyConstants.fileListURL, params);

		Log.d("NetworkOperations", "Returning JSON : " + result);
		return result;
	}

	/**
	 * function get file list
	 * 
	 * @param email
	 * @param password
	 * */
	public JSONObject getFileDetails(String email, String fileID) {
		// Building Parameters
		Log.d("NetworkOperations", "getFileDetails Email : " + email
				+ " & FileID : " + fileID);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email_id", email));
		params.add(new BasicNameValuePair("file_id", fileID));

		JSONObject result = jsonParser.getJSONFromUrl(
				PracifyConstants.fileDownloadURL, params);

		Log.d("NetworkOperations", "Returning JSON : " + result);
		return result;
	}

	/**
	 * function make Login Request
	 * 
	 * @param email
	 * @param password
	 * */
	public JSONObject loginUser(String email, String password) {
		// Building Parameters
		Log.d("NetworkOperations", "Email : " + email + " and Password : "
				+ password);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email_id", email));
		params.add(new BasicNameValuePair("password", password));
		JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);

		Log.d("NetworkOperations", "Returning JSON : " + json);
		return json;
	}

	/**
	 * function make Register Request
	 * 
	 * @param name
	 * @param email
	 * @param password
	 * */
	public JSONObject registerUser(String name, String email, String password) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_name", name));
		params.add(new BasicNameValuePair("email_id", email));
		params.add(new BasicNameValuePair("password", password));

		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
		// return json
		return json;
	}
}
