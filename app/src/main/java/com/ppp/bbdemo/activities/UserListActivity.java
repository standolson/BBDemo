package com.ppp.bbdemo.activities;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.ppp.bbdemo.R;
import com.ppp.bbdemo.adapter.UserListAdapter;
import com.ppp.bbdemo.adapter.UserListDecoration;
import com.ppp.bbdemo.database.NoteDbWorker;
import com.ppp.bbdemo.datamanager.UserListLoader;
import com.ppp.bbdemo.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the startup activity of the application.
 *
 * It displays a list of users obtained from a JSON web service, allows display of this list by
 * either JSON order, age, or by username.  Clicking on the "Note" button will display any stored
 * notes.  Clicking on any other part of a list item will display that user's details and allow
 * creation of a note.
 *
 * Users are obtained from the JSON service through an asynchornous Loader.
 */
public class UserListActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<List<User>> {

	private static final int LOADER_ID = 0;

	private static final String USER_LIST = "userList";
	private static final String DONE_LOADING = "doneLoading";

	private RecyclerView recycler;
	private LinearLayoutManager layoutManager;
	private ProgressBar spinner;
	private UserListAdapter adapter;

	private ArrayList<User> userList = null;
	private int pageNumber = 0;
	private boolean sortByUsername = false;
	private boolean sortByAge = false;

	private NoteDbWorker dbWorker;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_list_activity);

		// Recover or initialize state
		boolean doneLoading = false;
		if (savedInstanceState != null)  {
			pageNumber = savedInstanceState.getInt(UserListLoader.PAGE_NUMBER);
			sortByUsername = savedInstanceState.getBoolean(UserListLoader.SORT_BY_USERNAME);
			sortByAge = savedInstanceState.getBoolean(UserListLoader.SORT_BY_AGE);
			userList = savedInstanceState.getParcelableArrayList(USER_LIST);
			doneLoading = savedInstanceState.getBoolean(DONE_LOADING);
		}
		else {
			pageNumber = 0;
			userList = new ArrayList<User>();
		}

		// Setup the RecyclerView and its layout manager
		recycler = (RecyclerView) findViewById(R.id.user_list);
		layoutManager = new LinearLayoutManager(this);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		recycler.setLayoutManager(layoutManager);
		recycler.addItemDecoration(new UserListDecoration(this));

		// Setup the UserListAdapter and attach it to the RecyclerView
		adapter = new UserListAdapter(this, userList);
		recycler.setAdapter(adapter);
		adapter.isDoneLoading(doneLoading);

		// Other view elements
		spinner = (ProgressBar) findViewById(R.id.spinner);

		// If we are on the first page, load it.  While it is loading,
		// show a centered ProgressBar until it is loaded.
		if ((pageNumber == 0) && userList.isEmpty())  {
			showSpinner();
			getLoaderManager().initLoader(LOADER_ID, getLoaderArgs(), this);
		}
		else
			hideSpinner();

	}

	@Override
	public void onResume()
	{

		super.onResume();

		// Always cause the adapter to update itself as the UserDetailActivity
		// could have changed a user's note causing us to hide/show the "Note"
		// button
		adapter.notifyDataSetChanged();

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		savedInstanceState.putInt(UserListLoader.PAGE_NUMBER, pageNumber);
		savedInstanceState.putBoolean(UserListLoader.SORT_BY_USERNAME, sortByUsername);
		savedInstanceState.putBoolean(UserListLoader.SORT_BY_AGE, sortByAge);
		savedInstanceState.putParcelableArrayList(USER_LIST, userList);
		savedInstanceState.putBoolean(DONE_LOADING, adapter.doneLoading());
	}

	@Override
	public Loader<List<User>> onCreateLoader(int id, Bundle args)
	{

		// Only works with our Loader
		if (id != LOADER_ID)
			return null;

		return new UserListLoader(this, args);

	}

	@Override
	public void onLoadFinished(Loader<List<User>> loader, List<User> users)
	{

		// If no users were returned, we're done loading.  Indicate this
		// to the adapter.
		if ((users == null) || (users.size() == 0))  {
			adapter.isDoneLoading(true);
			return;
		}

		// Add the new users to the set we already have and cause the
		// adapter to update itself
		userList.addAll(users);
		adapter.notifyDataSetChanged();

		if (pageNumber == 0)
			hideSpinner();

	}

	@Override
	public void onLoaderReset(Loader<List<User>> users) {}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		int id = item.getItemId();

		// Sort by user
		if (id == R.id.sort_user_name)  {
			sortByUsername = true;
			sortByAge = false;
			resetList();
			return true;
		}

		// Sort by age
		if (id == R.id.sort_age)  {
			sortByUsername = false;
			sortByAge = true;
			resetList();
			return true;
		}

		return super.onOptionsItemSelected(item);

	}

	/**
	 * Hides the centered ProgressBar shown during the load of the
	 * first page of data.
	 */
	private void hideSpinner()
	{
		recycler.setVisibility(View.VISIBLE);
		spinner.setVisibility(View.GONE);
	}

	/**
	 * Shows the center ProgressBar during the load of the first page
	 * of data.
	 */
	private void showSpinner()
	{
		recycler.setVisibility(View.GONE);
		spinner.setVisibility(View.VISIBLE);
	}

	private UserListLoader getLoader()
	{
		Loader<List<User>> loader = getLoaderManager().getLoader(LOADER_ID);
		return (UserListLoader) loader;
	}

	/**
	 * Gets a Bundle containing the arguments required to execute
	 * a query on the UserListLoader.
	 * @return The Bundle of arguments
	 */
	private Bundle getLoaderArgs()
	{
		Bundle args = new Bundle();
		args.putInt(UserListLoader.PAGE_NUMBER, pageNumber);
		args.putBoolean(UserListLoader.SORT_BY_AGE, sortByAge);
		args.putBoolean(UserListLoader.SORT_BY_USERNAME, sortByUsername);
		return args;
	}

	/**
	 * Loads the next page of data from the UserListLoader.  Called by
	 * the RecyclerView.ViewHolder when it shows a list item containing
	 * a ProgressBar to indicate a new page is loading.
	 */
	public void loadNextPage()
	{
		pageNumber += 1;
		getLoaderManager().restartLoader(LOADER_ID, getLoaderArgs(), this);
	}

	/**
	 * Resets the list and causes the first page of results to be fetched
	 * and displayed.  This is commonly used for when the user has changed
	 * the sort-by parameter as in those cases we need to reset the list
	 * and requery the backend.
	 */
	private void resetList()
	{

		// Reset to page zero, empty the current list of users, and reset
		// the adapter so it will load more than one page
		pageNumber = 0;
		userList.clear();
		adapter.isDoneLoading(false);

		// Show the waiting spinner and start loading using the new
		// parameters
		showSpinner();
		getLoaderManager().restartLoader(LOADER_ID, getLoaderArgs(), this);

	}

}
