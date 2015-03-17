package com.ppp.bbdemo.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * This class is the item decorator for the user list.  It places a 2 pixel
 * high black divider line between each item in the list.
 */
public class UserListDecoration extends RecyclerView.ItemDecoration {

	private static final int DIVIDER_HEIGHT = 2;

	public UserListDecoration(Context context) {}

	@Override
	public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

		// Only on VERTICAL layouts
		if (getOrientation(parent) == LinearLayoutManager.HORIZONTAL)
			return;

		final int left = parent.getPaddingLeft();
		final int right = parent.getWidth() - parent.getPaddingRight();

		// This iterates over only the visible views
		LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
		int first = layoutManager.findFirstVisibleItemPosition();
		int last = layoutManager.findLastVisibleItemPosition();
		for (int i = Math.max(first, 1); i <= last; i += 1) {

			// Get the ViewHolder for this position so that we can get
			// its parent view
			final UserHolder holder = (UserHolder)parent.findViewHolderForPosition(i);
			if (holder == null)
				continue;

			final View child = holder.parent;
			final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child.getLayoutParams();
			final Resources resources = parent.getContext().getResources();

			// Place the divider along the top of the child
			final int top = child.getTop() - params.topMargin - DIVIDER_HEIGHT;
			final int bottom = child.getTop();

			// Paint the divider
			Paint paint = new Paint();
			paint.setColor(resources.getColor(android.R.color.black));
			c.drawRect(left, top, right, bottom, paint);

		}

	}

	private int getOrientation(RecyclerView parent)
	{
		if (parent.getLayoutManager() instanceof LinearLayoutManager)
			return ((LinearLayoutManager)parent.getLayoutManager()).getOrientation();
		else
			throw new IllegalStateException("DividerItemDecoration can only be used with a LinearLayoutManager.");
	}

}
