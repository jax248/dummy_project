package com.pracify.contentprovider;

import android.net.Uri;

import com.pracify.db.FileDetailsTableHandler;

public class FileDetailsContract {

	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.pracify.file_details";
	public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.pracify.file_details";

	// The authority for the sync adapter's content provider
	public static final String AUTHORITY = "com.pracify.contentprovider";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/file_details");

	// File Details Table Columns names
	public static final String FILE_DETAILS_ID = FileDetailsTableHandler.COLUMN_TABLE_FILE_DETAILS_ID;
	public static final String FILE_DETAILS_NAME = FileDetailsTableHandler.COLUMN_TABLE_FILE_DETAILS_NAME;
	public static final String FILE_DETAILS_DESC = FileDetailsTableHandler.COLUMN_TABLE_FILE_DETAILS_DESC;
	public static final String FILE_DETAILS_PATH = FileDetailsTableHandler.COLUMN_TABLE_FILE_DETAILS_PATH;
	public static final String FILE_DETAILS_OWNER = FileDetailsTableHandler.COLUMN_TABLE_FILE_DETAILS_OWNER;
	public static final String FILE_DETAILS_CREATIONDATE = FileDetailsTableHandler.COLUMN_TABLE_FILE_DETAILS_CREATIONDATE;
	public static final String FILE_DETAILS_GROUPID = FileDetailsTableHandler.COLUMN_TABLE_FILE_DETAILS_GROUPID;
}
