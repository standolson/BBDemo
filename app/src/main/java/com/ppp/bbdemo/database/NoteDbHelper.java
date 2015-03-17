package com.ppp.bbdemo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * This is the SQLiteOpenHelper for the notes database.
 */
public class NoteDbHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "notes.db";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_NAME = "user_notes";
	public static final String USER_ID = "userId";
	public static final String NOTES = "notes";

	private static final String CREATE_DATABASE =
		"CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
			"(" +
				BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				USER_ID + " TEXT, " +
				NOTES + " TEXT" +
			");";

	public NoteDbHelper(final Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public final void onCreate(final SQLiteDatabase db)
	{
		try {
			Log.e(getClass().getSimpleName(), CREATE_DATABASE);
			db.execSQL(CREATE_DATABASE);
		}
		catch (Throwable t) {
			Log.e(getClass().getSimpleName(), "unable to create database '" + DATABASE_NAME + "': " + t.getClass().getSimpleName());
			t.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion)
	{
		Log.e(getClass().getSimpleName(), "shouldn't get to onUpgrade");
	}

}
