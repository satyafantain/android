package me.battleship;

import android.graphics.Rect;
import android.util.Log;

/**
 * A ship
 * 
 * @author manuel
 */
public class Ship
{
	/** The tag for the logger */
	public static final String LOG_TAG = "Ship";

	/** The type of the ship */
	private ShipType type;

	/** The size of the ship */
	private int size;

	/** The id of the drawable of the ship */
	private int drawable;

	/** The x position of the ship */
	private int x;

	/** The y position of the ship */
	private int y;

	/** The orientation of the ship */
	private Orientation orientation;

	/**
	 * The full position of the ship
	 */
	private Rect pos;

	/** An array containing the field that was destroyed */
	private boolean fieldDestroyed[];

	/**
	 * Constructs a new ship
	 * 
	 * @param type
	 *           The type of the ship
	 * @param x
	 *           The x position of the ship
	 * @param y
	 *           The y position of the ship
	 * @param orientation
	 *           the orientation of the ship
	 */
	public Ship(ShipType type, int x, int y, Orientation orientation)
	{
		if (type == null)
		{
			throw new NullPointerException("type may not be null");
		}
		this.type = type;
		this.x = x;
		this.y = y;
		this.orientation = orientation;
		this.size = getSizeForType(type);
		this.pos = getRectForPos(x, y, size, orientation);
		switch (type)
		{
			case AIRCRAFT_CARRIER:
				drawable = R.drawable.aircraftcarrier;
			break;
			case BATTLESHIP:
				drawable = R.drawable.battleship;
			break;
			case SUBMARINE:
				drawable = R.drawable.submarine;
			break;
			case DESTROYER:
				drawable = R.drawable.destroyer;
			break;
		}
		fieldDestroyed = new boolean[size];
		for (int i = 0;i < size;i++)
		{
			fieldDestroyed[i] = false;
		}
	}

	/**
	 * Initializes a new {@link Ship} making a copy of an exsisting ship
	 * 
	 * @param ship
	 *           the ship to make a copy of
	 */
	public Ship(Ship ship)
	{
		this.type = ship.type;
		this.x = ship.x;
		this.y = ship.y;
		this.orientation = ship.orientation;
		this.pos = ship.pos;
		this.size = ship.size;
		this.drawable = ship.drawable;
		this.fieldDestroyed = ship.fieldDestroyed.clone();
	}

	/**
	 * Returns the size for the specified ship type
	 * 
	 * @param type
	 *           the type of the ship
	 * @return the size
	 */
	public static int getSizeForType(ShipType type)
	{
		switch (type)
		{
			case AIRCRAFT_CARRIER:
				return 5;
			case BATTLESHIP:
				return 4;
			case SUBMARINE:
				return 3;
			case DESTROYER:
				return 2;
			default:
				Log.wtf(LOG_TAG, "Unrecognized value " + type + " in getSizeForType(ShipType)");
				return -1;
		}
	}

	/**
	 * Returns the {@link Rect} for the specified position
	 * 
	 * @param x
	 *           the x position
	 * @param y
	 *           the y position
	 * @param size
	 *           the ships size
	 * @param orientation
	 *           the orientation
	 * @return the <code>Rect</code> for the specified position
	 */
	public static Rect getRectForPos(int x, int y, int size, Orientation orientation)
	{
		int right, bottom;
		if (orientation == Orientation.VERTICAL)
		{
			right = x;
			bottom = y + size - 1;
		}
		else
		{
			right = x + size - 1;
			bottom = y;
		}
		return new Rect(x, y, right, bottom);
	}

	/**
	 * Returns the ship type for the specified size
	 * 
	 * @param size
	 *           the size
	 * @return the ship type
	 * @throws IllegalArgumentException
	 *            if there is no ship type with the specified size
	 */
	public static ShipType getTypeForSize(int size) throws IllegalArgumentException
	{
		switch (size)
		{
			case 5:
				return ShipType.AIRCRAFT_CARRIER;
			case 4:
				return ShipType.BATTLESHIP;
			case 3:
				return ShipType.SUBMARINE;
			case 2:
				return ShipType.DESTROYER;
			default:
				throw new IllegalArgumentException("unrecognized value " + size + " in getTypeForSize(int)");
		}
	}

	/**
	 * Returns the type of the ship
	 * 
	 * @return the type of the ship
	 */
	public ShipType getType()
	{
		return type;
	}

	/**
	 * Returns the size of the ship
	 * 
	 * @return the size of the ship
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 * Returns the id of the drawable of the ship
	 * 
	 * @return the id of the drawable of the ship
	 */
	public int getDrawable()
	{
		return drawable;
	}

	/**
	 * Returns the x pos of the ship
	 * 
	 * @return the x pos of the ship
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Returns the y pos of the ship
	 * 
	 * @return the y pos of the ship
	 */
	public int getY()
	{
		return y;
	}

	/**
	 * Sets the position of the ship
	 * 
	 * @param x
	 *           the x position
	 * @param y
	 *           the y position
	 */
	public void setPos(int x, int y)
	{
		this.x = x;
		this.y = y;
		this.pos = getRectForPos(x, y, size, orientation);
	}

	/**
	 * Returns the orientation of the ship
	 * 
	 * @return the orientation of the ship
	 */
	public Orientation getOrientation()
	{
		return orientation;
	}

	/**
	 * Sets the orientation of the ship
	 * 
	 * @param orientation
	 *           the orientation
	 */
	public void setOrientation(Orientation orientation)
	{
		this.orientation = orientation;
		this.pos = getRectForPos(x, y, size, orientation);
	}

	/**
	 * Returns the pos of the ship
	 * 
	 * @return the pos
	 */
	public Rect getRect()
	{
		return pos;
	}

	/**
	 * Destroys the field at the specified position
	 * 
	 * @param xpos
	 *           the x position
	 * @param ypos
	 *           the y position
	 * @throws IllegalArgumentException
	 *            if the field is not a field of the ship
	 */
	public void destroyField(int xpos, int ypos) throws IllegalArgumentException
	{
		for (int i = 0;i < size;i++)
		{
			int iX, iY;
			if (orientation == Orientation.HORIZONTAL)
			{
				iX = x + i;
				iY = y;
			}
			else
			{
				iX = x;
				iY = y + i;
			}
			if (iX == xpos && iY == ypos)
			{
				fieldDestroyed[i] = true;
				return;
			}
		}
		throw new IllegalArgumentException("Position " + xpos + "," + ypos + " is not a position of this ship (" + x + "," + y + "," + orientation + ")");
	}

	/**
	 * Returns if all fields of this ship are destroyed
	 * 
	 * @return if all fields of this ship are destroyed
	 */
	public boolean areAllFieldsDestroyed()
	{
		for (boolean destroyed : fieldDestroyed)
		{
			if (destroyed == false)
			{
				return false;
			}
		}
		return true;
	}
}
