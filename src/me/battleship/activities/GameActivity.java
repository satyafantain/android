package me.battleship.activities;

import java.util.Arrays;
import java.util.List;

import me.battleship.PlaceableShip;
import me.battleship.Playground;
import me.battleship.R;
import me.battleship.Ship;
import me.battleship.ShipType;
import me.battleship.view.GameView;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * The game activity
 * 
 * @author Manuel Vögele
 */
public class GameActivity extends Activity
{
	/** The thread used for drawing **/
	private Thread drawThread;

	/** The view for drawing the game **/
	private GameView gameView;

	/** The own playground **/
	private Playground ownPlayground;

	/** The opponents playground **/
	private Playground opponentPlayground;

	/** The own ships **/
	private List<Ship> ships;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.game);
		gameView = (GameView) findViewById(R.id.game);
		ownPlayground = new Playground();
		opponentPlayground = new Playground();
		ships = Arrays.asList(new Ship[] {
				new PlaceableShip(ShipType.AIRCRAFT_CARRIER),
				new PlaceableShip(ShipType.BATTLESHIP),
				new PlaceableShip(ShipType.SUBMARINE),
				new PlaceableShip(ShipType.SUBMARINE),
				new PlaceableShip(ShipType.DESTROYER)
		});
		gameView.initialize(ownPlayground, opponentPlayground, ships);
		// TODO Bind game service
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		drawThread = new Thread(gameView);
		drawThread.start();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		drawThread.interrupt();
		drawThread = null;
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		// TODO Unbind game service
	}
}
