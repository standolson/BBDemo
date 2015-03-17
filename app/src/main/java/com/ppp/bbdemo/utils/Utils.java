package com.ppp.bbdemo.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ppp.bbdemo.R;
import com.ppp.bbdemo.model.User;
import com.squareup.picasso.Picasso;

/**
 * Helper methods.
 */
public class Utils {

	private Utils() {}

	/**
	 * Loads the given URL into the supplied ImageView
	 * @param iv The ImageView
	 * @param url The URL to load
	 */
	public static void loadImage(ImageView iv, String url)
	{
		// Use the Square Picasso library to load the image in the
		// background and cache it for us
		Picasso.with(iv.getContext()).load(url).into(iv);
	}

	/**
	 * Safely sets the given TextView with a possibly null String value.
	 * If null, we show a blank String.
	 * @param tv The TextView
	 * @param text The String to show
	 */
	public static void safeSetText(TextView tv, String text)
	{
		if (TextUtils.isEmpty(text))
			tv.setText("");
		else
			tv.setText(text);
	}

	/**
	 * Returns a user readable version of the user's city.  Returns
	 * blank for "N/A" or null values.
	 * @param user The User object
	 * @return A readable version of the city
	 */
	public static String getCityString(User user)
	{
		if (TextUtils.isEmpty(user.city) || "N/A".equals(user.city))
			return "";
		return user.city;
	}

	/**
	 * Returns a user readable version of the user's state and country.
	 * Shows blank for "N/A" or null values.  If the state is not user
	 * readable, returns only the country and vice-versa.  If neither
	 * is user readable, returns a blank string.
	 * @param user The User object
	 * @return A readable version of the city
	 */
	public static String getStateCountryString(Context context, User user)
	{
		if (TextUtils.isEmpty(user.state) || "N/A".equals(user.state))  {
			if (TextUtils.isEmpty(user.country) || "N/A".equals(user.country))
				return "";
			else
				return user.country;
		}
		else if (TextUtils.isEmpty(user.country) || "N/A".equals(user.country))
			return user.state;
		else
			return String.format(context.getString(R.string.state_country_format), user.state, user.country);
	}

}
