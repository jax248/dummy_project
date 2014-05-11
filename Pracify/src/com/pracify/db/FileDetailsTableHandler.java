package com.pracify.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pracify.db.tableClasses.FileDetails;

public class FileDetailsTableHandler extends DatabaseHandler {

	private static final String LOG_TAG = "FileDetailsTableHandler";

	public FileDetailsTableHandler(Context context) {
		super(context);
	}

	public void addFileDetails(FileDetails fileDetails) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COLUMN_TABLE_FILE_DETAILS_NAME, fileDetails.getName());
		values.put(COLUMN_TABLE_FILE_DETAILS_DESC, fileDetails.getDesc());
		values.put(COLUMN_TABLE_FILE_DETAILS_PATH, fileDetails.getPath());
		values.put(COLUMN_TABLE_FILE_DETAILS_OWNER, fileDetails.getOwner());
		values.put(COLUMN_TABLE_FILE_DETAILS_CREATIONDATE,
				fileDetails.getCreation_date());
		values.put(COLUMN_TABLE_FILE_DETAILS_GROUPID, fileDetails.getGroup());

		// Inserting Row
		db.insert(TABLE_FILE_DETAILS, null, values);
		Log.d("UserDetailsTableHandler", TABLE_USER_DETAILS
				+ " : data inserted");
		Log.d(LOG_TAG,
				"File Name : " + fileDetails.getName() + ", File Path : "
						+ fileDetails.getPath() + ", File Owner : "
						+ fileDetails.getOwner() + ", Group : "
						+ fileDetails.getGroup());
		db.close(); // Closing database connection
	}

	public FileDetails getFileDetails(String file_id) {

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_FILE_DETAILS, new String[] {
				COLUMN_TABLE_FILE_DETAILS_ID, COLUMN_TABLE_FILE_DETAILS_NAME,
				COLUMN_TABLE_FILE_DETAILS_DESC, COLUMN_TABLE_FILE_DETAILS_PATH,
				COLUMN_TABLE_FILE_DETAILS_OWNER,
				COLUMN_TABLE_FILE_DETAILS_CREATIONDATE,
				COLUMN_TABLE_FILE_DETAILS_GROUPID },
				COLUMN_TABLE_FILE_DETAILS_ID + "=?", new String[] { file_id },
				null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();

			FileDetails fileDetails = new FileDetails(cursor.getInt(0),
					cursor.getString(1), cursor.getString(2),
					cursor.getString(3), cursor.getString(4),
					cursor.getString(5), cursor.getString(6));
			db.close();
			Log.d(LOG_TAG, TABLE_FILE_DETAILS + " : Returning File Details");
			Log.d(LOG_TAG, "File ID : " + fileDetails.getId()
					+ ", File Name : " + fileDetails.getName()
					+ ", File Path : " + fileDetails.getPath()
					+ ", File Owner : " + fileDetails.getOwner()
					+ ", Creation Date : " + fileDetails.getCreation_date()
					+ ", Group Date : " + fileDetails.getGroup());

			db.close();
			// return contact
			return fileDetails;
		} else {

			db.close();
			return null;
		}
	}

	public List<FileDetails> getAllFileDetails() {
		List<FileDetails> fileDetailsList = new ArrayList<FileDetails>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_FILE_DETAILS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				FileDetails fileDetails = new FileDetails(cursor.getInt(0),
						cursor.getString(1), cursor.getString(2),
						cursor.getString(3), cursor.getString(4),
						cursor.getString(5), cursor.getString(6));
				fileDetailsList.add(fileDetails);
			} while (cursor.moveToNext());
		}

		db.close();

		// return list
		return fileDetailsList;
	}

	public void deleteAllDetails() {

		SQLiteDatabase db = this.getWritableDatabase();
		String query = "DELETE FROM " + TABLE_FILE_DETAILS;

		db.rawQuery(query, null);

		db.close();
	}

	public int updateFileDetails(FileDetails fileDetails) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COLUMN_TABLE_FILE_DETAILS_NAME, fileDetails.getName());
		values.put(COLUMN_TABLE_FILE_DETAILS_PATH, fileDetails.getPath());
		values.put(COLUMN_TABLE_FILE_DETAILS_OWNER, fileDetails.getOwner());
		values.put(COLUMN_TABLE_FILE_DETAILS_CREATIONDATE,
				fileDetails.getCreation_date());
		values.put(COLUMN_TABLE_FILE_DETAILS_GROUPID, fileDetails.getGroup());

		Log.d(LOG_TAG, TABLE_FILE_DETAILS + " : Updating Record with ID : "
				+ fileDetails.getId());

		// updating row
		return db.update(TABLE_FILE_DETAILS, values,
				COLUMN_TABLE_FILE_DETAILS_ID + " = ?",
				new String[] { fileDetails.getId() });
	}
}
