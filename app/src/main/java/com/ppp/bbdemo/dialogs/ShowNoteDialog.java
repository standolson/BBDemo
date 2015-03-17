package com.ppp.bbdemo.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ppp.bbdemo.R;
import com.ppp.bbdemo.database.NoteDbWorker;
import com.ppp.bbdemo.model.User;

/**
 * This class is the dialog displayed to the user when they click the
 * "Note" button a list item from the main activity.
 */
public class ShowNoteDialog extends DialogFragment {

	private static final String USER = "user";

	private View rootView;
	private TextView userNameText;
	private EditText noteText;
	private Button dismissButton;

	private User user;

	public static ShowNoteDialog newInstance(User user)
	{
		ShowNoteDialog dialog = new ShowNoteDialog();
		Bundle args = new Bundle();
		args.putParcelable(USER, user);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);

		setStyle(STYLE_NO_TITLE, R.style.dialogStyle);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		rootView = inflater.inflate(R.layout.show_note_dialog, container, false);

		userNameText = (TextView) rootView.findViewById(R.id.note_username);
		noteText = (EditText) rootView.findViewById(R.id.note_edittext);
		dismissButton = (Button) rootView.findViewById(R.id.dismiss_button);

		user = getArguments().getParcelable(USER);

		// Set the username
		userNameText.setText(user.userName);

		// Get the user's note for display in the EditText
		NoteDbWorker worker = new NoteDbWorker(rootView.getContext());
		String note = worker.getNote(user.userName);
		worker.close();
		noteText.setText(note);

		// Set a click handler on the button to close the dialog
		dismissButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { dismiss(); }
		});

		return rootView;

	}

}
