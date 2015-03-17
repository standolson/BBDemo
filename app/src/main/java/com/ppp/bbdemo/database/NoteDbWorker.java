package com.ppp.bbdemo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

/**
 * This is the worker class for accessing the notes database.  It has methods
 * for creating, deleting, and find notes for given users.
 */
public class NoteDbWorker {

	protected NoteDbHelper dbHelper;

	public NoteDbWorker(Context context)
	{
		dbHelper = new NoteDbHelper(context);
	}

	public void close() { dbHelper.close(); }

	/**
	 * Updates or creates a note for the given userId.  If the note is blank or
	 * empty, deletes the note for the user.
	 * @param userId The userId
	 * @param note The note
	 */
	public void updateNote(String userId, String note)
	{

		if ((userId == null) || TextUtils.isEmpty(userId))
			return;

		// Special case for no or blank note...delete the entry
		if ((note == null) || TextUtils.isEmpty(note))  {
			deleteNote(userId);
			return;
		}

		SQLiteDatabase db = null;
		Cursor c = null;
		ContentValues cv = makeContentValues(userId, note);

		try {

			// Open the database
			db = dbHelper.getWritableDatabase();

			// Query the DB for the given user's record
			c = getUserCursor(db, userId);

			// At least one user exists with the given userId
			if (c != null)  {

				// If we got none back, do an insert
				if (c.getCount() == 0)
					db.insert(NoteDbHelper.TABLE_NAME, null, cv);

				// If we got one back, we need to update it
				else if (c.getCount() == 1)  {
					c.moveToFirst();
					int rowId = c.getInt(c.getColumnIndex(BaseColumns._ID));
					db.update(NoteDbHelper.TABLE_NAME, cv, BaseColumns._ID + " = " + rowId, null);
				}

				// More than one?  Preposterous!
				else
					Log.e(getClass().getSimpleName(), "found " + c.getCount() + " records for userId '" + userId + "'");

			}

		}
		catch (Throwable t) {
			Log.e(getClass().getSimpleName(), "updateNote exception: " + t.getClass().getSimpleName());
			t.printStackTrace();
		}
		finally {
			if (c != null) {
				c.deactivate();
				c.close();
			}
			if (db != null)
				db.close();
		}

	}

	/**
	 * Gets and returns the note for the given userId.
	 * @param userId The userId
	 * @return The note.  Blank string is returned for cases where the userId
	 * does not exist in the database.
	 */
	public String getNote(String userId)
	{

		SQLiteDatabase db = null;
		Cursor c = null;
		String note = "";

		try {

			// Open the database
			db = dbHelper.getReadableDatabase();

			// Query the DB for the given user's record
			c = getUserCursor(db, userId);

			// At least one user exists with the given userId
			if (c != null) {

				// Only works for one returned record
				if (c.getCount() == 1)  {
					c.moveToFirst();
					note = c.getString(c.getColumnIndex(NoteDbHelper.NOTES));
				}

				// More than one?  Preposterous!
				else
					Log.e(getClass().getSimpleName(), "found " + c.getCount() + " records for userId '" + userId + "'");

			}

		}
		catch (Throwable t) {
			Log.e(getClass().getSimpleName(), "updateNote exception: " + t.getClass().getSimpleName());
			t.printStackTrace();
		}
		finally {
			if (c != null) {
				c.deactivate();
				c.close();
			}
			if (db != null)
				db.close();
		}

		return note;

	}

	/**
	 * Deletes the row for the given userId
	 * @param userId The userId
	 */
	public void deleteNote(String userId)
	{

		SQLiteDatabase db = null;

		if ((userId == null) || TextUtils.isEmpty(userId))
			return;

		try {
			db = dbHelper.getWritableDatabase();
			db.delete(NoteDbHelper.TABLE_NAME, NoteDbHelper.USER_ID + " = ?", new String[]{userId});
		}
		catch (Throwable t) {
			Log.e(getClass().getSimpleName(), "deleteNote exception: " + t.getClass().getSimpleName());
			t.printStackTrace();
		}
		finally {
			if (db != null)
				db.close();
		}

	}

	/**
	 * Determines if the given userId has a note in the database
	 * @param userId The userId
	 * @return true if a note exists
	 */
	public boolean hasNote(String userId)
	{

		SQLiteDatabase db = null;
		Cursor c = null;

		if ((userId == null) || TextUtils.isEmpty(userId))
			return false;

		try {
			db = dbHelper.getReadableDatabase();
			c = getUserCursor(db, userId);
			boolean retval = (c != null) ? c.getCount() == 1 : false;
			db.close();
			return retval;
		}
		catch (Throwable t) {
			Log.e(getClass().getSimpleName(), "hasNote exception: " + t.getClass().getSimpleName());
			t.printStackTrace();
		}
		finally {
			if (c != null)  {
				c.deactivate();
				c.close();
			}
			if (db != null)
				db.close();
		}

		return false;

	}

	/**
	 * Queries the database to return a Cursor containing a given user's
	 * record
	 * @param db The open SQLiteDatabase
	 * @param userId The userId
	 * @return A cursor possibly containing the user's record
	 */
	private Cursor getUserCursor(SQLiteDatabase db, String userId)
	{
		return
			db.query(NoteDbHelper.TABLE_NAME,
					null,
					NoteDbHelper.USER_ID + " = ?",
					new String[] { userId },
					null, null, null);
	}

	/**
	 * Loads and returns a ContentValues object with the given userId and
	 * note for use in database operations.
	 * @param userId The userId
	 * @param note The note
	 * @return
	 */
	private ContentValues makeContentValues(String userId, String note)
	{
		ContentValues cv = new ContentValues();
		cv.put(NoteDbHelper.USER_ID, userId);
		cv.put(NoteDbHelper.NOTES, note);
		return cv;
	}

}