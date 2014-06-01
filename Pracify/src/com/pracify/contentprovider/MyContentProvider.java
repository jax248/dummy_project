package com.pracify.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.pracify.db.FileDetailsTableHandler;

/*
 * Define an implementation of ContentProvider that stubs out
 * all methods
 */
public class MyContentProvider extends ContentProvider {

	public static final UriMatcher URI_MATCHER = buildUriMatcher();
	public static final String PATH = "file_details";
	public static final int PATH_TOKEN = 100;
	public static final String PATH_FOR_ID = "file_details/*";
	public static final int PATH_FOR_ID_TOKEN = 200;

	// Uri Matcher for the content provider
	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = FileDetailsContract.AUTHORITY;
		matcher.addURI(authority, PATH, PATH_TOKEN);
		matcher.addURI(authority, PATH_FOR_ID, PATH_FOR_ID_TOKEN);
		return matcher;
	}

	private FileDetailsTableHandler dbHelper;

	@Override
	public boolean onCreate() {

		Context ctx = getContext();
		dbHelper = new FileDetailsTableHandler(ctx);
		return true;
	}

	@Override
	public String getType(Uri uri) {

		final int match = URI_MATCHER.match(uri);
		switch (match) {
		case PATH_TOKEN:
			return FileDetailsContract.CONTENT_TYPE_DIR;
		case PATH_FOR_ID_TOKEN:
			return FileDetailsContract.CONTENT_ITEM_TYPE;
		default:
			throw new UnsupportedOperationException("URI " + uri
					+ " is not supported.");
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		final int match = URI_MATCHER.match(uri);
		switch (match) {
		// retrieve tv shows list
		case PATH_TOKEN: {
			SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
			builder.setTables(FileDetailsTableHandler.TABLE_FILE_DETAILS);
			return builder.query(db, projection, selection, selectionArgs,
					null, null, sortOrder);
		}
		case PATH_FOR_ID_TOKEN: {
			int fileID = (int) ContentUris.parseId(uri);
			SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
			builder.setTables(FileDetailsTableHandler.TABLE_FILE_DETAILS);
			builder.appendWhere(FileDetailsTableHandler.COLUMN_TABLE_FILE_DETAILS_ID
					+ "=" + fileID);
			return builder.query(db, projection, selection, selectionArgs,
					null, null, sortOrder);
		}
		default:
			return null;
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
        int token = URI_MATCHER.match(uri);
        switch (token) {
            case PATH_TOKEN: {
                long id = db.insert(FileDetailsTableHandler.TABLE_FILE_DETAILS, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return FileDetailsContract.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            default: {
                throw new UnsupportedOperationException("URI: " + uri + " not supported.");
            }
        }
	}

	/*
	 * delete() always returns "no rows affected" (0)
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	/*
	 * update() always returns "no rows affected" (0)
	 */
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}
}
