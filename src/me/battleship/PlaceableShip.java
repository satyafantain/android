package me.battleship;

/**
 * A ship which still has to be placed
 * 
 * @author Manuel Vögele
 */
public class PlaceableShip extends Ship
{
	/** The x position where the ship will be placed if its not on the playground */
	private int startX;

	/** The y position where the ship will be placed if its not on the playground */
	private int startY;
	
	/** The x position on which the ship was before the last movement */
	private int lastX;

	/** The y position on which the ship was before the last movement */
	private int lastY;

	/** The orientation which the ship has if its not on the playground */
	private Orientation startOrientation;

	/** The x position where the ship will be drawn */
	private int drawX;

	/** The y position where the ship will be drawn */
	private int drawY;

	/** Indicates whether the ship is on the playground */
	private boolean onPlayground;

	/**
	 * Initializes a new {@link PlaceableShip} using an existing ship
	 * 
	 * @param ship
	 *           the ship to use as base
	 */
	public PlaceableShip(Ship ship)
	{
		super(ship);
		onPlayground = ship.getX() >= 0;
		lastX = ship.getX();
		lastY = ship.getY();
	}

	/**
	 * Sets the position where the ship will be placed if its not on the
	 * playground
	 * 
	 * @param x
	 *           the x position
	 * @param y
	 *           the y position
	 */
	public void setStartPos(int x, int y)
	{
		startX = x;
		startY = y;
	}

	/**
	 * Returns the x position where the ship will be placed if its not on the
	 * playground
	 * 
	 * @return the x position
	 */
	public int getStartX()
	{
		return startX;
	}

	/**
	 * Returns the y position where the ship will be placed if its not on the
	 * playground
	 * 
	 * @return the y position
	 */
	public int getStartY()
	{
		return startY;
	}

	@Override
	public void setPos(int x, int y)
	{
		lastX = getX();
		lastY = getY();
		super.setPos(x, y);
	}

	/**
	 * The x position the ship had before the last movement
	 * 
	 * @return the last x position
	 */
	public int getLastX()
	{
		return lastX;
	}

	/**
	 * The y position the ship had before the last movement
	 * 
	 * @return the last y position
	 */
	public int getLastY()
	{
		return lastY;
	}

	/**
	 * Sets the orientation the ship has if its not on the playground
	 * 
	 * @param orientation
	 *           the orientation
	 */
	public void setStartOrientation(Orientation orientation)
	{
		startOrientation = orientation;
	}

	/**
	 * Returns the orientation the ship has if its not on the playground
	 * 
	 * @return the orientation
	 */
	public Orientation getStartOrientation()
	{
		return startOrientation;
	}

	/**
	 * Sets the position where the ship will be drawn
	 * 
	 * @param x
	 *           the x position
	 * @param y
	 *           the y position
	 */
	public void setDrawPos(int x, int y)
	{
		drawX = x;
		drawY = y;
	}

	/**
	 * Returns the x position where the ship will be drawn
	 * 
	 * @return the x position
	 */
	public int getDrawX()
	{
		return drawX;
	}

	/**
	 * Returns the y position where the ship will be drawn
	 * 
	 * @return the y position
	 */
	public int getDrawY()
	{
		return drawY;
	}

	/**
	 * Sets whether the ship is on the playground
	 * 
	 * @param onPlayground
	 *           <code>true</code> if the ship is on the playground
	 */
	public void setOnPlayground(boolean onPlayground)
	{
		this.onPlayground = onPlayground;
		if (!onPlayground)
		{
			setPos(-1, -1);
		}
	}

	/**
	 * Returns whether the ship is on the polayground
	 * 
	 * @return <code>true</code> if the ship is on the playground
	 */
	public boolean isOnPlayground()
	{
		return onPlayground;
	}

}
