package me.battleship.gameui;

import me.battleship.services.GameService;
import me.battleship.services.interfaces.GameServiceConnectedListener;
import me.battleship.services.interfaces.GameServiceConnection;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

/**
 * A parent class for all UIs for the game
 * 
 * @author Manuel Vögele
 */
public abstract class GameUI implements ServiceConnection, GameServiceConnectedListener
{	
	/** The log tag */
	public static final String LOG_TAG = GameUI.class.getSimpleName();

	/** The connection to the game service */
	protected GameServiceConnection gameService;
	
	/** The listener called when the game service has connected */
	private GameServiceConnectedListener listener;

	/** The context */
	private Context context;

	@SuppressWarnings("javadoc")
	public GameUI(Context context, GameServiceConnectedListener listener)
	{
		this.context = context;
		this.listener = listener;
		Intent intent = new Intent(context, GameService.class);
		context.bindService(intent, this, 0);
	}

	/**
	 * Called when the UI is being destroyed
	 */
	public void onDestroy()
	{
		getContext().unbindService(this);
	}

	/**
	 * Called when the UI is brought to the foreground
	 */
	public void onStart()
	{
		// Nothing to do
	}

	/**
	 * Called when the UI is brought to the background
	 */
	public void onStop()
	{
		// Nothing to do 
	}

	/**
	 * Returns the context
	 * 
	 * @return the context
	 */
	public Context getContext()
	{
		return context;
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service)
	{
		Log.i(LOG_TAG, "GameService connected");
		gameService = (GameServiceConnection) service;
		onGameServiceConnected();
		listener.onGameServiceConnected();
	}

	@Override
	public void onServiceDisconnected(ComponentName name)
	{
		Log.i(LOG_TAG, "GameService disconnected");
		gameService = null;
	}

	/**
	 * Returns the view to which the game will be rendered
	 * 
	 * @return the view
	 */
	public abstract View getView();
}
