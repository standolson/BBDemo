package com.ppp.bbdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ppp.bbdemo.R;
import com.ppp.bbdemo.model.User;

import java.util.List;

/**
 * This is the RecyclerView adapter for the user list.  It allows for paged loading
 * of items from the JSON service by extending the size of the list by one item until
 * the JSON service tells us it has no more items.  The view holder will show a list
 * item with a ProgressBar in those cases until the next page is loaded.
 */
public class UserListAdapter extends RecyclerView.Adapter<UserHolder> {

	private final List<User> userList;
	private final LayoutInflater layoutInflater;

	private boolean doneLoading = false;

	public UserListAdapter(Context context, List<User> users)
	{
		userList = users;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public UserHolder onCreateViewHolder(ViewGroup parent, int position)
	{
		View view = layoutInflater.inflate(R.layout.user_list_item, parent, false);
		return new UserHolder(view);
	}

	@Override
	public void onBindViewHolder(UserHolder holder, int position)
	{
		User user = position < userList.size() ? userList.get(position) : null;
		holder.bind(user, position);
	}

	@Override
	public int getItemCount()
	{
		// Add one to the size of the list if we aren't done loading.
		// We do this so we can show a spinner in the list while the
		// next chunk is loaded.
		return (userList != null) ? userList.size() + (doneLoading ? 0 : 1) : 0;
	}

	/**
	 * Tells this adapter that we are done loading and it should not allow
	 * a ProgressBar to be shown as a list item to indicate another page is
	 * loading.
	 *
	 * If we are indicating that we are done loading, makes sure we notify
	 * everyone that our data set has changed so any ProgressBar currently
	 * being shown will be removed.
	 *
	 * @param b true if done loading, false otherwise
	 */
	public void isDoneLoading(boolean b)
	{
		doneLoading = b;
		if (doneLoading)
			notifyDataSetChanged();
	}

	public boolean doneLoading() { return doneLoading; }

}
