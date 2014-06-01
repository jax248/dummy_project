package com.pracify.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.pracify.db.tableClasses.UserDetails;

public class UserDetailsTableHandler extends DatabaseHandler {

	public UserDetailsTableHandler(Context context) {
		super(context);
	}

	@JavascriptInterface
	public void addUserDetails(UserDetails userDetails) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COLUMN_TABLE_USER_DETAILS_EMAIL_ID,
				userDetails.getEmail_id()); // Email ID
		values.put(COLUMN_TABLE_USER_DETAILS_USER_NAME,
				userDetails.getUser_name()); // User Name
		values.put(COLUMN_TABLE_USER_DETAILS_IS_LOGGED_IN,
				userDetails.getIs_logged_in()); // Is Logged in

		// Inserting Row
		db.insert(TABLE_USER_DETAILS, null, values);
		Log.d("UserDetailsTableHandler", TABLE_USER_DETAILS
				+ " : data inserted");
		db.close(); // Closing database connection
	}

	@JavascriptInterface
	public UserDetails getUserDetails(String email_id) {

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_USER_DETAILS, new String[] {
				COLUMN_TABLE_USER_DETAILS_EMAIL_ID,
				COLUMN_TABLE_USER_DETAILS_USER_NAME,
				COLUMN_TABLE_USER_DETAILS_IS_LOGGED_IN },
				COLUMN_TABLE_USER_DETAILS_EMAIL_ID + "=?",
				new String[] { email_id }, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();

			UserDetails userDetails = new UserDetails(cursor.getString(0),
					cursor.getString(1), Integer.parseInt(cursor.getString(2)));
			db.close();
			Log.d("UserDetailsTableHandler", TABLE_USER_DETAILS
					+ " : Returning User Details");
			Log.d("UserDetailsTableHandler",
					"Email ID : " + userDetails.getEmail_id()
							+ ", User Name : " + userDetails.getUser_name()
							+ ", isLoggedIn : " + userDetails.getIs_logged_in());

			db.close();
			// return contact
			return userDetails;
		} else {

			db.close();
			return null;
		}
	}

	@JavascriptInterface
	public List<UserDetails> getAllUserDetails() {
		List<UserDetails> userDetailsList = new ArrayList<UserDetails>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_USER_DETAILS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				UserDetails userDetails = new UserDetails(cursor.getString(0),
						cursor.getString(1), Integer.parseInt(cursor
								.getString(2)));
				// Adding contact to list
				userDetailsList.add(userDetails);
			} while (cursor.moveToNext());
		}

		db.close();

		// return contact list
		return userDetailsList;
	}

	public String getUserID() {

		List<UserDetails> userDetailsList = getAllUserDetails();

		if (userDetailsList.size() == 1) {

			return userDetailsList.get(0).getEmail_id();
		} else {

			deleteAllDetails();

			return null;
		}
	}

	@JavascriptInterface
	public boolean isUserLoggedIn() {

		List<UserDetails> userDetailsList = getAllUserDetails();

		if (userDetailsList.size() == 1) {

			if (userDetailsList.get(0).getIs_logged_in() == 1) {
				return true;
			} else {
				return false;
			}
		} else {

			deleteAllDetails();

			return false;
		}
	}

	@JavascriptInterface
	public void deleteAllDetails() {

		SQLiteDatabase db = this.getWritableDatabase();
		String query = "DELETE FROM " + TABLE_USER_DETAILS;

		db.rawQuery(query, null);

		db.close();
	}

	@JavascriptInterface
	public int disableLogin(String email_id) {
		SQLiteDatabase db = this.getWritableDatabase();

		UserDetails userDetails = getUserDetails(email_id);

		ContentValues values = new ContentValues();
		values.put(COLUMN_TABLE_USER_DETAILS_EMAIL_ID,
				userDetails.getEmail_id()); // Email ID
		values.put(COLUMN_TABLE_USER_DETAILS_USER_NAME,
				userDetails.getUser_name()); // User Name
		values.put(COLUMN_TABLE_USER_DETAILS_IS_LOGGED_IN, 0); // Is Logged in

		// updating row
		return db.update(TABLE_USER_DETAILS, values,
				COLUMN_TABLE_USER_DETAILS_EMAIL_ID + " = ?",
				new String[] { userDetails.getEmail_id() });
	}

	@JavascriptInterface
	public int enableLogin(String email_id) {
		SQLiteDatabase db = this.getWritableDatabase();

		UserDetails userDetails = getUserDetails(email_id);

		ContentValues values = new ContentValues();
		values.put(COLUMN_TABLE_USER_DETAILS_EMAIL_ID,
				userDetails.getEmail_id()); // Email ID
		values.put(COLUMN_TABLE_USER_DETAILS_USER_NAME,
				userDetails.getUser_name()); // User Name
		values.put(COLUMN_TABLE_USER_DETAILS_IS_LOGGED_IN, 1); // Is Logged in

		// updating row
		return db.update(TABLE_USER_DETAILS, values,
				COLUMN_TABLE_USER_DETAILS_EMAIL_ID + " = ?",
				new String[] { userDetails.getEmail_id() });
	}
}
