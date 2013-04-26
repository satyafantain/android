package me.battleship.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.battleship.Playground;
import me.battleship.Ship;
import me.battleship.ShipType;
import me.battleship.services.interfaces.GameServiceConnection;
import android.app.Service;
import android.content.Intent;
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
	private boolean isRunning = false;

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
