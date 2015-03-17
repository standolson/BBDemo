package com.ppp.bbdemo.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.ppp.bbdemo.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * This is the model class for a user in the system.  The JSON data is
 * deserialized into these objects via GSON.  Helper methods are available
 * for getting the properly formatted versions of certain members.  The class
 * is Parcelable to allow for passing one of these objects around the system.
 */
public class User implements Parcelable {

	// NOTE: We're only using the short date format for two reasons.  First, we
	// have no reason to deserialize the created/updated fields.  Secondly, we
	// have no reason to show a full timestamp at this point.
	public static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";
	private static final SimpleDateFormat shortFormat = new SimpleDateFormat(SHORT_DATE_FORMAT);

	// JSON
	public String realName;
	public String userName;
	public String city;
	public String state;
	public String country;
	public String profilePicUrl;
	public double bodyfat;
	public long userId;
	public double height;
	public double weight;
	public Date birthday;
	public int id;

	public User() {}

	/**
	 * Gets the user's age in years.
	 * @return The user's age or zero if the user has no birthday.
	 */
	public int getAge()
	{
		if (birthday != null) {
			long birth = birthday.getTime();
			long now = (new Date()).getTime();
			long days = TimeUnit.MILLISECONDS.toDays(now - birth);
			return (int) (days / 365);
		}
		return 0;
	}

	/**
	 * Get the user's age in years as a string.
	 * @return The age or a zero length string if the user has no birthday
	 */
	public String getAgeString()
	{
		return (birthday != null) ? Integer.toString(getAge()) : "";
	}

	/**
	 * Get the user's height as a string
	 * @param context A context for getting the format string
	 * @return The height, formatted using a localized height format
	 */
	public String getHeightString(Context context)
	{
		String format = context.getString(R.string.height_format);
		int feet = (int)height / 12;
		int inches = (int)height % 12;
		return String.format(format, feet, inches);
	}

	/**
	 * Get the user's weight as a string
	 * @param context A context for getting the format string
	 * @return The weight, formatted using a localized weight format
	 */
	public String getWeightString(Context context)
	{
		return context.getString(R.string.weight_format, weight);
	}

	/**
	 * Get the user's body fat as a string
	 * @param context A context for getting the format string
	 * @return The body fat percentage, formatted using a localized body fat format
	 */
	public String getBodyFatString(Context context)
	{
		return context.getString(R.string.body_fat_format, bodyfat);
	}

	//
	// Parcelable
	//

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>()
	{
		@Override
		public User createFromParcel(Parcel in) { return new User(in); }
		@Override
		public User[] newArray(int size) { return new User[size]; }
	};

	@Override
	public int describeContents() { return 0; }

	public User(Parcel in)
	{

		realName = in.readString();
		userName = in.readString();
		city = in.readString();
		state = in.readString();
		country = in.readString();
		profilePicUrl = in.readString();
		bodyfat = in.readDouble();
		userId = in.readLong();
		height = in.readDouble();
		weight = in.readDouble();
		id = in.readInt();

		// Parse all of the dates and if there are errors,
		// revert to using the time now.
		try {
			birthday = shortFormat.parse(in.readString());
		}
		catch (Exception e) {
			birthday = new Date();
		}

	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		safeParcelString(dest, realName);
		safeParcelString(dest, userName);
		safeParcelString(dest, city);
		safeParcelString(dest, state);
		safeParcelString(dest, country);
		safeParcelString(dest, profilePicUrl);
		dest.writeDouble(bodyfat);
		dest.writeLong(userId);
		dest.writeDouble(height);
		dest.writeDouble(weight);
		dest.writeInt(id);
		safeParcelDate(dest, birthday);
	}

	/**
	 * Safely writes a possibly null String to the given Parcel
	 * @param dest Parcel to write to
	 * @param str String to write
	 */
	private void safeParcelString(Parcel dest, String str)
	{
		dest.writeString(str != null ? str : "");
	}

	/**
	 * Safely writes a possibly null Date to the given Parcel
	 * @param dest Parcel to write to
	 * @param date Date to write
	 */
	private void safeParcelDate(Parcel dest, Date date)
	{
		dest.writeString(date != null ? shortFormat.format(date) : "");
	}

}
