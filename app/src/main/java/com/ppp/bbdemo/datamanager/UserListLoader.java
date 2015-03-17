package com.ppp.bbdemo.datamanager;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ppp.bbdemo.model.User;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * This is the asynchonous Loader class for reading from the JSON service.  It can
 * take optional arguments to indicate the page number to load, and whether to sort
 * by username or age.
 *
 * This uses Square's OkHttp library for network operations.
 */
public class UserListLoader extends AsyncTaskLoader<List<User>> {

	private static final String BASE_URL = "http://107.170.231.93/member";
	private static final int DEFAULT_PAGE_SIZE = 10;

	public static final String PAGE_NUMBER = "pageNumber";
	public static final String SORT_BY_USERNAME = "sortByUserName";
	public static final String SORT_BY_AGE = "sortByAge";

	private int pageNumber = 0;
	private boolean sortByUsername = false;
	private boolean sortByAge = false;

	public UserListLoader(Context context, Bundle args)
	{

		super(context);

		// First part of a workaround to cause instantiation of this Loader to
		// perform a load.
		//
		// See: http://stackoverflow.com/a/19481256
		onContentChanged();

		// Get any arguments to this Loader
		if (args != null) {
			pageNumber = args.getInt(PAGE_NUMBER, 0);
			sortByUsername = args.getBoolean(SORT_BY_USERNAME);
			sortByAge = args.getBoolean(SORT_BY_AGE);
		}

	}

	@Override
	public List<User> loadInBackground()
	{

		// Generate the URL based on parameters at instantiation
		StringBuilder sb = new StringBuilder();
		String url = String.format("%s?limit=%d&skip=%d", BASE_URL, DEFAULT_PAGE_SIZE, pageNumber * DEFAULT_PAGE_SIZE);
		sb.append(url);
		if (sortByUsername)
			sb.append("&sort=userName");
		else if (sortByAge)
			sb.append("&sort=birthday%20DESC");

		// Build the request
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(sb.toString()).build();

		try {

			// Execute the request to get a response
			Response response = client.newCall(request).execute();

			// Get the response as a stream for GSON processing
			InputStream stream = response.body().byteStream();
			InputStreamReader reader = new InputStreamReader(stream);

			// Process the JSON
			GsonBuilder builder = new GsonBuilder();
			builder.setDateFormat(User.SHORT_DATE_FORMAT);
			Gson gson = builder.create();
			List<User> users = Arrays.asList(gson.fromJson(reader, User[].class));

			reader.close();

			return users;

		}
		catch (IOException e) {
			Log.e(getClass().getSimpleName(), "loadInBackground exception: " + e.getClass().getSimpleName());
			return null;
		}

	}

	@Override
	public void deliverResult(List<User> users)
	{
		super.deliverResult(users);
	}

	@Override
	public void onCanceled(List<User> users) {}

	@Override
	protected void onStartLoading()
	{
		// Second half of the workaround to cause the instantiation
		// of this Loader to perform the initial load
		if (takeContentChanged())
			forceLoad();
	}

	@Override
	protected void onStopLoading() {}

	@Override
	protected void onReset() {}

}
