package me.battleship.utils;

import android.graphics.Rect;

/**
 * Utilities for working with {@link Rect Rects}
 * 
 * @author Manuel Vögele
 */
public class RectUtils
{
	/**
	 * Returns the intersection between r1 and r2
	 * 
	 * @param r1
	 *           the first rectangle
	 * @param r2
	 *           the second rectangle
	 * @return the intersection between r1 and r2. <code>null</code> if the
	 *         rectangles do not intersect.
	 */
	public static Rect getIntersection(Rect r1, Rect r2)
	{
		Rect result = new Rect();
		result.left = Math.max(r1.left, r2.left);
		result.top = Math.max(r1.top, r2.top);
		result.right = Math.min(r1.right, r2.right);
		result.bottom = Math.min(r1.bottom, r2.bottom);
		if (result.left > result.right || result.top > result.bottom)
		{
			return null;
		}
		return result;
	}
}
