package com.ppp.bbdemo.adapter;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ppp.bbdemo.R;
import com.ppp.bbdemo.activities.UserDetailActivity;
import com.ppp.bbdemo.activities.UserListActivity;
import com.ppp.bbdemo.database.NoteDbWorker;
import com.ppp.bbdemo.dialogs.ShowNoteDialog;
import com.ppp.bbdemo.model.User;
import com.ppp.bbdemo.utils.Utils;

/**
 * This is the view holder class for the RecyclerView that displays a list of users.
 */
public class UserHolder extends RecyclerView.ViewHolder {

	public ViewGroup parent;
	public ImageView image;
	public TextView name;
	public TextView age;
	public TextView city;
	public TextView stateAndCountry;
	public Button noteButton;

	private boolean hasNote = false;

	public UserHolder(View view)
	{
		super(view);
		parent = (ViewGroup) view;
		image = (ImageView) view.findViewById(R.id.profilePic);
		name = (TextView) view.findViewById(R.id.name);
		age = (TextView) view.findViewById(R.id.age);
		city = (TextView) view.findViewById(R.id.city);
		stateAndCountry = (TextView) view.findViewById(R.id.state_and_country);
		noteButton = (Button) view.findViewById(R.id.note_button);
	}

	public void bind(User user, int position)
	{

		// Show the appropriate view in the list item.  If we ran off the
		// current end of the list, the input User object will be null so
		// for that item, we display a spinner instead of user information.
		parent.findViewById(R.id.item_layout).setVisibility(user == null ? View.GONE : View.VISIBLE);
		parent.findViewById(R.id.spinner).setVisibility(user == null ? View.VISIBLE : View.GONE);

		// If we don't have a user, the user has scrolled and we need to
		// instigate a load of the next page of users.
		if (user == null)  {
			((UserListActivity) parent.getContext()).loadNextPage();
			return;
		}

		// Determine if the user has a note so we can show the "Note" button
		NoteDbWorker worker = new NoteDbWorker(parent.getContext());
		hasNote = worker.hasNote(user.userName);
		worker.close();

		// Setup click handlers on the parent view
		setupClickHandlers(user);

		// Load the profile picture
		Utils.loadImage(image, user.profilePicUrl);

		// Populate the visible user data into the view
		Utils.safeSetText(name, user.userName);
		Utils.safeSetText(age, user.getAgeString());
		Utils.safeSetText(city, Utils.getCityString(user));
		Utils.safeSetText(stateAndCountry, Utils.getStateCountryString(parent.getContext(), user));

	}

	/**
	 * Set the click handlers for the given parent view
	 */
	private void setupClickHandlers(final User user)
	{

		// Put a click handler on the parent view that will show
		// the detail view of the user
		parent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(parent.getContext(), UserDetailActivity.class);
				intent.putExtra(UserDetailActivity.USER, user);
				((ActionBarActivity) parent.getContext()).startActivity(intent);
			}
		});

		// Put a click handler on the "Note" button that will
		// open the note dialog
		noteButton.setVisibility(hasNote ? View.VISIBLE : View.GONE);
		if (hasNote)
			noteButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ShowNoteDialog dialog = ShowNoteDialog.newInstance(user);
					dialog.show(((ActionBarActivity) parent.getContext()).getSupportFragmentManager(), "tag");
				}
			});

	}

}
