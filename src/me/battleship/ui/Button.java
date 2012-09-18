package me.battleship.ui;

import android.graphics.Rect;

/**
 * A graphical button that will be drawn on the screen within the game
 * 
 * @author Manuel Vögele
 */
public class Button
{
	/**
	 * The location of the button
	 */
	private Rect location;

	/**
	 * The resource id of the image
	 */
	private int drawable;

	/**
	 * Initializes a new {@link Button}
	 * 
	 * @param location
	 *           the location of the button
	 * @param drawable
	 *           the resource id of the image
	 */
	public Button(Rect location, int drawable)
	{
		this.location = location;
		this.drawable = drawable;
	}

	/**
	 * Returns the location of the button
	 * 
	 * @return the location of the button
	 */
	public Rect getLocation()
	{
		return location;
	}

	/**
	 * Sets the location of the button
	 * 
	 * @param location
	 *           the location of the button
	 */
	public void setLocation(Rect location)
	{
		this.location = location;
	}

	/**
	 * Returns the resource id of the image
	 * 
	 * @return the resource id of the image
	 */
	public int getDrawable()
	{
		return drawable;
	}

	/**
	 * Sets the resource id of the image
	 * 
	 * @param drawable
	 *           the resource id of the image
	 */
	public void setDrawable(int drawable)
	{
		this.drawable = drawable;
	}

}
