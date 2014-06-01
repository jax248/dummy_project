package com.pracify.datasync;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.pracify.LoginActivity;
import com.pracify.R;
import com.pracify.contentprovider.FileDetailsContract;
import com.pracify.db.tableClasses.FileDetails;
import com.pracify.network.NetworkOperations;
import com.pracify.util.CommonHelpers;
import com.pracify.util.PracifyConstants;

/**
 * Handle the transfer of data between a server and an app, using the Android
 * sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

	// Global variables
	// Define a variable to contain a content resolver instance
	private final ContentResolver mContentResolver;
	private final AccountManager mAccountManager;

	private Context context;

	/**
	 * Set up the sync adapter
	 */
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);

		this.context = context;
		/*
		 * If your app uses a content resolver, get an instance of it from the
		 * incoming Context
		 */
		mContentResolver = context.getContentResolver();
		mAccountManager = AccountManager.get(context);
	}

	/**
	 * Set up the sync adapter. This form of the constructor maintains
	 * compatibility with Android 3.0 and later platform versions
	 */
	public SyncAdapter(Context context, boolean autoInitialize,
			boolean allowParallelSyncs) {
		super(context, autoInitialize, allowParallelSyncs);
		/*
		 * If your app uses a content resolver, get an instance of it from the
		 * incoming Context
		 */
		mContentResolver = context.getContentResolver();
		mAccountManager = AccountManager.get(context);
	}

	/*
	 * Specify the code you want to run in the sync adapter. The entire sync
	 * adapter runs in a background thread, so you don't have to set up your own
	 * background processing.
	 */
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {

		int mId = 1;

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_action_refresh)
				.setContentTitle("Pracify").setContentText("Sync Started..");

		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(mId, mBuilder.build());
		/*
		 * Put the data transfer code here.
		 */
		Log.d("SyncAdapter -> onPerformSync", "Performing Sync for account["
				+ account.name + "]");

		try {
			String authToken = mAccountManager.blockingGetAuthToken(account,
					LoginActivity.PARAM_AUTHTOKEN_TYPE, true);

			if (authToken != null && !authToken.isEmpty()) {

				Log.d("SyncAdapter -> onPerformSync",
						"Get File List From Server");

				mBuilder = new NotificationCompat.Builder(context)
						.setSmallIcon(R.drawable.ic_action_refresh)
						.setContentTitle("Pracify")
						.setContentText("Getting File list from Server!");
				mNotificationManager.notify(mId, mBuilder.build());
				List<String> serverFileList = getFileListFromServer(account.name);

				mBuilder = new NotificationCompat.Builder(context)
						.setSmallIcon(R.drawable.ic_action_refresh)
						.setContentTitle("Pracify")
						.setContentText("Getting Local File list!");
				mNotificationManager.notify(mId, mBuilder.build());

				Log.d("SyncAdapter -> onPerformSync", "Get Local Files");
				List<String> localFileList = new ArrayList<String>();
				Cursor curFileList = provider
						.query(FileDetailsContract.CONTENT_URI, null, null,
								null, null);
				if (curFileList != null) {
					while (curFileList.moveToNext()) {
						String fileID = curFileList.getString(0);
						Log.d("SyncAdapter -> onPerformSync", "fileID : "
								+ fileID);
						localFileList.add(fileID);
					}
					curFileList.close();
				}
				Log.d("SyncAdapter -> onPerformSync", "Local File Count : "
						+ localFileList.size());

				Log.d("SyncAdapter -> onPerformSync",
						"Fetching Files to be downloaded");
				List<String> filesToBeDownloaded = getFilesToBeDownloaded(
						localFileList, serverFileList);

				mBuilder = new NotificationCompat.Builder(context)
						.setSmallIcon(R.drawable.ic_action_refresh)
						.setContentTitle("Pracify")
						.setContentText(
								"Downloading " + filesToBeDownloaded.size()
										+ " file(s) from Server....")
						.setProgress(0, 0, true);
				mNotificationManager.notify(mId, mBuilder.build());

				Log.d("SyncAdapter -> onPerformSync",
						"Downloading Files from Server");
				downloadFilesFromServer(filesToBeDownloaded, provider,
						account.name);

				Log.d("SyncAdapter -> onPerformSync",
						"Fetching Files to be uploaded");
				List<String> filesToBeUploaded = getFilesToBeUploaded(
						localFileList, serverFileList);

				mBuilder = new NotificationCompat.Builder(context)
						.setSmallIcon(R.drawable.ic_action_refresh)
						.setContentTitle("Pracify")
						.setContentText(
								"Uploading " + filesToBeUploaded.size()
										+ " files to Server....")
						.setProgress(0, 0, true);
				mNotificationManager.notify(mId, mBuilder.build());

				Log.d("SyncAdapter -> onPerformSync",
						"Uploading Files from Server");
				uploadFilesToServer(filesToBeUploaded, provider);

				mBuilder = new NotificationCompat.Builder(context)
						.setSmallIcon(R.drawable.ic_action_refresh)
						.setContentTitle("Pracify")
						.setContentText("Sync Completed!!");
				mNotificationManager.notify(mId, mBuilder.build());

				Log.d("SyncAdapter -> onPerformSync", "Sync Completed");

			} else {

				mBuilder = new NotificationCompat.Builder(context)
						.setSmallIcon(R.drawable.ic_action_refresh)
						.setContentTitle("Pracify")
						.setContentText("Authentication failure...");
				mNotificationManager.notify(mId, mBuilder.build());
				Log.e("SyncAdapter -> onPerformSync",
						"Authentication failure...");
			}
		} catch (Exception e) {

			mBuilder = new NotificationCompat.Builder(context)
					.setSmallIcon(R.drawable.ic_action_refresh)
					.setContentTitle("Pracify")
					.setContentText(
							"Error on performing Sync : " + e.getMessage());
			mNotificationManager.notify(mId, mBuilder.build());

			Log.e("SyncAdapter -> onPerformSync", e.getMessage());
			e.printStackTrace();
		}

		mNotificationManager.cancelAll();

	}

	private void uploadFilesToServer(List<String> filesToBeUploaded,
			ContentProviderClient provider) {

		Cursor curFileList;
		List<FileDetails> fileDetailsList = new ArrayList<FileDetails>();
		try {
			curFileList = provider.query(FileDetailsContract.CONTENT_URI, null,
					null, null, null);
			if (curFileList != null) {
				while (curFileList.moveToNext()) {
					String fileID = curFileList.getString(0);
					Log.d("SyncAdapter -> uploadFilesToServer", "fileID : "
							+ fileID);
					if (filesToBeUploaded.contains(fileID)) {

						String fileName = curFileList.getString(1);
						Log.d("SyncAdapter -> uploadFilesToServer",
								"fileName : " + fileName);

						String fileDesc = curFileList.getString(2);
						Log.d("SyncAdapter -> uploadFilesToServer",
								"fileDesc : " + fileDesc);

						String filePath = curFileList.getString(3);
						Log.d("SyncAdapter -> uploadFilesToServer",
								"filePath : " + filePath);

						String fileOwner = curFileList.getString(4);
						Log.d("SyncAdapter -> uploadFilesToServer",
								"fileOwner : " + fileOwner);

						String fileCreationDate = curFileList.getString(5);
						Log.d("SyncAdapter -> uploadFilesToServer",
								"fileCreationDate : " + fileCreationDate);

						String fileGroupID = curFileList.getString(6);
						Log.d("SyncAdapter -> uploadFilesToServer",
								"fileGroupID : " + fileGroupID);

						FileDetails fileDetails = new FileDetails(
								Integer.parseInt(fileID), fileName, fileDesc,
								filePath, fileOwner, fileCreationDate,
								fileGroupID);

						fileDetailsList.add(fileDetails);
					} else {

						Log.d("SyncAdapter -> uploadFilesToServer",
								"Not uploading file with fileID : " + fileID);
					}
				}
				curFileList.close();
			}

			NetworkOperations networkOperations = new NetworkOperations();

			Log.d("SyncAdapter -> uploadFilesToServer", "Uploading Files");
			for (FileDetails fileDetails : fileDetailsList) {

				networkOperations.uploadFile(fileDetails);
			}

			Log.d("SyncAdapter -> uploadFilesToServer", "Done Uploading Files");
		} catch (RemoteException e) {

			e.printStackTrace();
		}
	}

	private void downloadFilesFromServer(List<String> filesToBeDownloaded,
			ContentProviderClient provider, String emailID) {

		Log.d("SyncAdapter -> downloadFilesToServer", "Start Downloading Files");
		NetworkOperations networkOperations = new NetworkOperations();

		for (String fileID : filesToBeDownloaded) {

			JSONObject result = networkOperations.getFileDetails(emailID,
					fileID);

			try {
				String serverFileID = result.getString("file_id");
				String serverFileName = result.getString("file_name");
				String serverFileDesc = result.getString("file_desc");
				String serverFileOwner = result.getString("file_owner");
				String serverFileGroup = result.getString("file_group");
				String serverFileCreationDate = result
						.getString("file_creation_date");
				String serverFilePath = result.getString("file_path");

				String newPath = CommonHelpers.getOutputDir(context) + "/"
						+ serverFileName + PracifyConstants.musicFileExtension;

				networkOperations.downloadFile(serverFilePath, newPath);
				Log.d("SyncAdapter -> downloadFilesToServer",
						"FIle Downloaded to : " + newPath);

				if (serverFileDesc == null || serverFileDesc.isEmpty())
					serverFileDesc = "None";

				ContentValues values = new ContentValues();
				values.put(FileDetailsContract.FILE_DETAILS_ID, serverFileID);
				values.put(FileDetailsContract.FILE_DETAILS_NAME,
						serverFileName);
				values.put(FileDetailsContract.FILE_DETAILS_DESC,
						serverFileDesc);
				values.put(FileDetailsContract.FILE_DETAILS_OWNER,
						serverFileOwner);
				values.put(FileDetailsContract.FILE_DETAILS_GROUPID,
						serverFileGroup);
				values.put(FileDetailsContract.FILE_DETAILS_CREATIONDATE,
						serverFileCreationDate);
				values.put(FileDetailsContract.FILE_DETAILS_PATH, newPath);

				provider.insert(FileDetailsContract.CONTENT_URI, values);

				Log.d("SyncAdapter -> downloadFilesToServer",
						"Details Added to database...");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Log.d("SyncAdapter -> downloadFilesToServer", "Done Downloading Files");
	}

	private List<String> getFilesToBeDownloaded(List<String> localFileList,
			List<String> serverFileList) {
		List<String> tempFileList = new ArrayList<String>();

		for (String fileID : serverFileList) {

			if (!localFileList.contains(fileID))
				tempFileList.add(fileID);
		}
		return tempFileList;
	}

	private List<String> getFilesToBeUploaded(List<String> localFileList,
			List<String> serverFileList) {

		List<String> tempFileList = new ArrayList<String>();

		for (String fileID : localFileList) {

			if (!serverFileList.contains(fileID))
				tempFileList.add(fileID);
		}
		return tempFileList;
	}

	private List<String> getFileListFromServer(String email_id) {

		NetworkOperations networkOperations = new NetworkOperations();

		JSONObject result = networkOperations.getFileList(email_id);

		int fileCount = -1;

		try {
			fileCount = result.getInt("filesCounts");
		} catch (Exception e) {

			Log.e("SyncAdapter -> getFileListFromServer",
					"Error parsing JSON. " + e.getMessage());
			e.printStackTrace();
			return null;
		}

		Log.d("SyncAdapter -> getFileListFromServer", "Server fileCount : "
				+ fileCount);

		List<String> serverFileList = new ArrayList<String>();

		if (fileCount != -1) {

			for (int i = 0; i < fileCount; i++) {
				try {
					String fileID = result.getString("file_" + i);
					Log.d("SyncAdapter -> getFileListFromServer", "fileID " + i
							+ " : " + fileID);
					serverFileList.add(fileID);
				} catch (JSONException e) {

					Log.e("SyncAdapter -> getFileListFromServer",
							"Error getting file at  " + i + ". Exception : "
									+ e.getMessage());
					e.printStackTrace();
				}

			}
		}
		return serverFileList;
	}
}
