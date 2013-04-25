package me.battleship.activities;

import me.battleship.R;
import me.battleship.gameui.DefaultGameUI;
import me.battleship.gameui.GameUI;
import me.battleship.services.GameService;
import me.battleship.services.interfaces.GameServiceConnectedListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.FrameLayout;

/**
 * The game activity
 * 
 * @author Manuel Vögele
 */
public class GameActivity extends Activity implements GameServiceConnectedListener
{

	/** The view for drawing the game **/
	private GameUI ui;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.game);
		Intent intent = new Intent(this, GameService.class);
		startService(intent);
		// TODO Dynamic allocation of the game ui
		ui = new DefaultGameUI(this, this);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		ui.onStart();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		ui.onStop();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ui.onDestroy();
	}

	@Override
	public void onGameServiceConnected()
	{
		FrameLayout gameFrame = (FrameLayout) findViewById(R.id.game_frame);
		gameFrame.addView(ui.getView());
	}
}
