package me.battleship.activities;

import me.battleship.R;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.game);
		// TODO Bind game service
	}	
	
	@Override
	protected void onStart()
	{
		super.onStart();
		GameView gameView = (GameView) findViewById(R.id.game);
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
