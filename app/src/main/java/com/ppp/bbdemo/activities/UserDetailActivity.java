package com.ppp.bbdemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ppp.bbdemo.R;
import com.ppp.bbdemo.database.NoteDbWorker;
import com.ppp.bbdemo.model.User;
import com.ppp.bbdemo.utils.Utils;

/**
 * Displays a detail view of a user and allows the creation, editing, and clearing of
 * a note in the notes database for that user.
 */
public class UserDetailActivity extends ActionBarActivity {

	public static final String USER = "user";

	private User user;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);

		setContentView(R.layout.user_detail_activity);

		// Get the user to display
		Intent intent = getIntent();
		user = intent.getParcelableExtra(USER);

		// Set the profile image and all TextViews
		Utils.loadImage((ImageView) findViewById(R.id.profilePic), user.profilePicUrl);
		Utils.safeSetText((TextView) findViewById(R.id.city), Utils.getCityString(user));
		Utils.safeSetText((TextView) findViewById(R.id.state_and_country), Utils.getStateCountryString(this, user));
		Utils.safeSetText((TextView) findViewById(R.id.username), user.userName);
		Utils.safeSetText((TextView) findViewById(R.id.realname), user.realName);
		Utils.safeSetText((TextView) findViewById(R.id.age), user.getAgeString());
		Utils.safeSetText((TextView) findViewById(R.id.height), user.getHeightString(this));
		Utils.safeSetText((TextView) findViewById(R.id.weight), user.getWeightString(this));
		Utils.safeSetText((TextView) findViewById(R.id.body_fat), user.getBodyFatString(this));

		// Set the note
		NoteDbWorker worker = new NoteDbWorker(this);
		String note = worker.getNote(user.userName);
		worker.close();
		Utils.safeSetText((TextView) findViewById(R.id.note_edittext), note);

		// Set click handlers on the buttons
		((Button) findViewById(R.id.clear)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { clearNote(); }
		});
		((Button) findViewById(R.id.save)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { saveNote(); }
		});

	}

	/**
	 * Clears the note edit area text and the note database entry for the user
	 */
	private void clearNote()
	{
		((EditText) findViewById(R.id.note_edittext)).setText("");
		NoteDbWorker worker = new NoteDbWorker(this);
		worker.deleteNote(user.userName);
		worker.close();
		Toast.makeText(this, getString(R.string.note_cleared), Toast.LENGTH_LONG).show();
	}

	/**
	 * Saves the note edit area text to the database for the given user
	 */
	private void saveNote()
	{
		String text = ((EditText) findViewById(R.id.note_edittext)).getText().toString();
		NoteDbWorker worker = new NoteDbWorker(this);
		worker.updateNote(user.userName, text);
		worker.close();
		Toast.makeText(this, getString(R.string.note_saved), Toast.LENGTH_LONG).show();
	}

}
