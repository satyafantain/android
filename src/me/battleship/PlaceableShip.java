package me.battleship;

/**
 * A ship which still has to be placed
 * 
 * @author Manuel Vögele
 */
public class PlaceableShip extends Ship
{
	/** The x position where the ship will be placed if its not on the playground **/
	private int startX;

	/** The y position where the ship will be placed if its not on the playground **/
	private int startY;
	
	/** The orientation which the ship has if its not on the playground */
	private Orientation startOrientation;

	/** The x position where the ship will be drawn **/
	private int drawX;

	/** The y position where the ship will be drawn **/
	private int drawY;

	/** Indicates whether the ship is on the playground **/
	private boolean onPlayground;

	/**
	 * Initializes a new {@link PlaceableShip}
	 * 
	 * @param type
	 *           the type of the ship
	 */
	public PlaceableShip(Ship ship)
	{
		super(ship);
		onPlayground = ship.getOrientation() != null;
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
