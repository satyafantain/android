package me.battleship;

/**
 * This is where game takes place
 * 
 * @author manuel
 */
public class Playground
{
	/**
	 * The size of the playground. The playground is assumed to be square so this
	 * is used for the x and y size.
	 */
	public static final int SIZE = 10;

	/** Stores all fields of this playground. */
	private PlaygroundField[][] fields;

	/**
	 * Constructs a new playground
	 */
	public Playground()
	{
		fields = new PlaygroundField[SIZE][SIZE];
		for (int y = 0;y < SIZE;y++)
		{
			for (int x = 0;x < SIZE;x++)
			{
				fields[x][y] = new PlaygroundField();
			}
		}
	}

	/**
	 * Returns a field of the playground
	 * 
	 * @param x
	 *           the x position of the field
	 * @param y
	 *           the y position of the field
	 * @return the field at the specified position
	 */
	public PlaygroundField getField(int x, int y)
	{
		return fields[x][y];
	}

	/**
	 * Validates if a ship with the specified orientation can be placed on field
	 * orientation
	 * 
	 * @param x
	 *           the x position
	 * @param y
	 *           the y position
	 * @param orientation
	 *           the orientation of the ship
	 * @param type
	 *           the type of the ship
	 * @return if the position is valid
	 */
	public boolean validateShipPos(int x, int y, Orientation orientation, ShipType type)
	{
		if (x < 0)
			return false;
		if (y < 0)
			return false;
		int size = Ship.getSizeForType(type);
		if (orientation == Orientation.HORIZONTAL && x + size - 1 >= SIZE)
			return false;
		if (orientation == Orientation.VERTICAL && y + size - 1 >= SIZE)
			return false;
		for (int i = 0;i < size;i++)
		{
			if (orientation == Orientation.HORIZONTAL && fields[x + i][y].getShip() != null)
				return false;
			if (orientation == Orientation.VERTICAL && fields[x][y + i].getShip() != null)
				return false;
		}
		return true;
	}

	/**
	 * Returns whether the specified coordinate is on the field or not
	 * 
	 * @param x
	 *           the x pos
	 * @param y
	 *           the y pos
	 * @return if the specified position is on the playground
	 */
	public static boolean isPosOnPlaygroud(int x, int y)
	{
		if (x < 0)
			return false;
		if (y < 0)
			return false;
		if (x >= SIZE)
			return false;
		if (y >= SIZE)
			return false;
		return true;
	}
}