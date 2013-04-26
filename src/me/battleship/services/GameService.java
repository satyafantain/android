package me.battleship.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.battleship.Orientation;
import me.battleship.Playground;
import me.battleship.Ship;
import me.battleship.ShipType;
import me.battleship.services.interfaces.GameServiceConnection;
import me.battleship.utils.RectUtils;
import android.app.Service;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;

/**
 * The service taking care of the game logic
 * 
 * @author Manuel Vögele
 */
public class GameService extends Service
{
	/** Indicates whether the service is running */
	private static boolean isRunning = false;

	/** A set containing the own ships */
	List<Ship> ownShips;

	/** A set containing the opponents ships */
	List<Ship> opponentShips;

	/** The own playground */
	Playground ownPlayground;

	/** The enemies playground */
	Playground opponentPlayground;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if (isRunning)
			return START_NOT_STICKY;
		ownShips = new ArrayList<Ship>(
		          Arrays.asList(new Ship(ShipType.AIRCRAFT_CARRIER, -1, -1, null),
		                        new Ship(ShipType.BATTLESHIP, -1, -1, null),
		                        new Ship(ShipType.SUBMARINE, -1, -1, null),
		                        new Ship(ShipType.SUBMARINE, -1, -1, null),
		                        new Ship(ShipType.DESTROYER, -1, -1, null)));
		opponentShips = new ArrayList<Ship>(ownShips.size());
		ownPlayground = new Playground();
		opponentPlayground = new Playground();
		isRunning = true;
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		if (!isRunning)
			throw new IllegalStateException("The service has to be started before binding to it");
		return new GameServiceBinder();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		isRunning = false;
	}

	/**
	 * Returns whether this service is running
	 * 
	 * @return <code>true</code> if the servic is running
	 */
	public static boolean isRunning()
	{
		return isRunning;
	}

	/**
	 * The binder for the {@link GameService}
	 * 
	 * @author Manuel Vögele
	 */
	private class GameServiceBinder extends Binder implements GameServiceConnection
	{
		// TODO Restrict writing access on returned data
		/**
		 * Initializes a new {@link GameServiceBinder}
		 */
		public GameServiceBinder()
		{
			// Nothing to do
		}

		@Override
		public List<Ship> getOwnShips()
		{
			return ownShips;
		}

		@Override
		public List<Ship> getOpponentShips()
		{
			return opponentShips;
		}

		@Override
		public Playground getOwnPlayground()
		{
			return ownPlayground;
		}

		@Override
		public Playground getOpponentPlayground()
		{
			return opponentPlayground;
		}
		
		@Override
		public boolean areAllShipsPlaced(Collection<Ship> ships)
		{
			for (Ship ship : ships)
			{
				Rect rect = ship.getRect();
				if (rect.left < 0 || rect.top < 0 || rect.right >= Playground.SIZE || rect.bottom >= Playground.SIZE)
				{
					return false;
				}
			}
			return true;
		}

		@Override
		public Set<Point> getInvalidFields(Collection<Ship> ships)
		{
			Set<Point> fields = new HashSet<Point>();
			for (Ship ship : ships)
			{
				if (ship.getX() < 0 || ship.getY() < 0)
					continue;
				for (Ship ship2 : ships)
				{
					if (ship == ship2)
						continue;
					if (ship2.getX() < 0 || ship2.getY() < 0)
						continue;
					Rect rect = RectUtils.getIntersection(ship.getRect(), ship2.getRect());
					if (rect != null)
					{
						if (ship.getOrientation() == Orientation.HORIZONTAL)
						{
							for (int x = rect.left;x <= rect.right;x++)
							{
								fields.add(new Point(x, rect.top));
							}
						}
						else
						{
							for (int y = rect.top;y <= rect.bottom;y++)
							{
								fields.add(new Point(rect.left, y));
							}
						}
					}
				}
			}
			return fields;
		}

		@Override
		public boolean confirmShips()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isInPlacementPhase()
		{
			// TODO Auto-generated method stub
			return true;
		}
	}
}
