package com.pracify.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "pracify";

	// User Details table name
	protected static final String TABLE_USER_DETAILS = "user_details";

	// User Details Table Columns names
	protected static final String COLUMN_TABLE_USER_DETAILS_EMAIL_ID = "email_id";
	protected static final String COLUMN_TABLE_USER_DETAILS_USER_NAME = "user_name";
	protected static final String COLUMN_TABLE_USER_DETAILS_IS_LOGGED_IN = "is_logged_in";

	// File Details table name
	public static final String TABLE_FILE_DETAILS = "file_details";
	// File Details Table Columns names
	public static final String COLUMN_TABLE_FILE_DETAILS_ID = "file_id";
	public static final String COLUMN_TABLE_FILE_DETAILS_NAME = "file_name";
	public static final String COLUMN_TABLE_FILE_DETAILS_DESC = "file_desc";
	public static final String COLUMN_TABLE_FILE_DETAILS_PATH = "file_path";
	public static final String COLUMN_TABLE_FILE_DETAILS_OWNER = "file_owner";
	public static final String COLUMN_TABLE_FILE_DETAILS_CREATIONDATE = "created_on";
	public static final String COLUMN_TABLE_FILE_DETAILS_GROUPID = "group_id";

	// Group Details table name
	protected static final String TABLE_GROUP_DETAILS = "group_details";
	// File Details Table Columns names
	protected static final String COLUMN_TABLE_GROUP_DETAILS_ID = "group_id";
	protected static final String COLUMN_TABLE_GROUP_DETAILS_NAME = "group_name";
	protected static final String COLUMN_TABLE_GROUP_DETAILS_OWNER = "group_owner";
	protected static final String COLUMN_TABLE_GROUP_DETAILS_CREATIONDATE = "created_on";
	protected static final String COLUMN_TABLE_GROUP_DETAILS_MEMBERS = "members";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {

		String sqlQuery = "CREATE TABLE " + TABLE_GROUP_DETAILS + "("
				+ COLUMN_TABLE_GROUP_DETAILS_ID + " TEXT PRIMARY KEY,"
				+ COLUMN_TABLE_GROUP_DETAILS_NAME + " TEXT NOT NULL,"
				+ COLUMN_TABLE_GROUP_DETAILS_OWNER + " TEXT NOT NULL,"
				+ COLUMN_TABLE_GROUP_DETAILS_CREATIONDATE + " TEXT NOT NULL,"
				+ COLUMN_TABLE_GROUP_DETAILS_MEMBERS + " TEXT NOT NULL" + ")";
		db.execSQL(sqlQuery);
		Log.d("DatabaseHandler", TABLE_GROUP_DETAILS + " table Created");

		sqlQuery = "CREATE TABLE " + TABLE_FILE_DETAILS + "("
				+ COLUMN_TABLE_FILE_DETAILS_ID + " TEXT PRIMARY KEY,"
				+ COLUMN_TABLE_FILE_DETAILS_NAME + " TEXT NOT NULL,"
				+ COLUMN_TABLE_FILE_DETAILS_DESC + " TEXT NOT NULL,"
				+ COLUMN_TABLE_FILE_DETAILS_PATH + " TEXT NOT NULL,"
				+ COLUMN_TABLE_FILE_DETAILS_OWNER + " TEXT NOT NULL,"
				+ COLUMN_TABLE_FILE_DETAILS_CREATIONDATE + " TEXT NOT NULL,"
				+ COLUMN_TABLE_FILE_DETAILS_GROUPID + " TEXT NOT NULL" + ")";
		db.execSQL(sqlQuery);
		Log.d("DatabaseHandler", TABLE_FILE_DETAILS + " table Created");

		sqlQuery = "CREATE TABLE " + TABLE_USER_DETAILS + "("
				+ COLUMN_TABLE_USER_DETAILS_EMAIL_ID + " TEXT PRIMARY KEY,"
				+ COLUMN_TABLE_USER_DETAILS_USER_NAME + " TEXT NOT NULL,"
				+ COLUMN_TABLE_USER_DETAILS_IS_LOGGED_IN + " INTEGER NOT NULL"
				+ ")";
		db.execSQL(sqlQuery);
		Log.d("DatabaseHandler", TABLE_USER_DETAILS + " table Created");
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_DETAILS);

		// Create tables again
		onCreate(db);
	}
}
