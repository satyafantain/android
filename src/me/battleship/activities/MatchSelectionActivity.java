package me.battleship.activities;

import me.battleship.R;
import me.battleship.services.GameService;
import me.battleship.services.XMPPConnectionService;
import me.battleship.services.interfaces.XMPPConnection;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * An activity to select which opponent you want to battle 
 *
 * @author Manuel Vögele
 */
public class MatchSelectionActivity extends Activity implements OnClickListener, ServiceConnection, android.content.DialogInterface.OnClickListener
{

	/** The log tag **/
	public final static String LOG_TAG = MatchSelectionActivity.class.getSimpleName();

	/** The connection to the XMPP server **/
	XMPPConnection connection;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.match_selection);
		findViewById(R.id.logout_button).setOnClickListener(this);
		findViewById(R.id.matchmaker_connect).setOnClickListener(this);
		// TODO OnClick for direct connect
		Intent intent = new Intent(this, XMPPConnectionService.class);
		bindService(intent, this, BIND_AUTO_CREATE);
		if (GameService.isRunning())
		{
			intent = new Intent(this, GameActivity.class);
			startActivity(intent);
		}
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		unbindService(this);
	}
	
	@Override
	public void onBackPressed()
	{
		Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage(R.string.really_logout);
		dialog.setCancelable(true);
		dialog.setPositiveButton(R.string.yes, this);
		dialog.setNegativeButton(R.string.no, null);
		dialog.show();
	}
	
	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.matchmaker_connect)
		{
			// TODO Implement connecting via matchmaker
			Intent intent = new Intent(this, GameActivity.class);
			startActivity(intent);
		}
		else if (v.getId() == R.id.logout_button)
		{
			connection.disconnect();
			finish();
		}
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		connection.disconnect();
		finish();
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service)
	{
		Log.i(LOG_TAG, "XMPPConnectionService connected");
		connection = (XMPPConnection) service;
	}

	@Override
	public void onServiceDisconnected(ComponentName name)
	{
		Log.i(LOG_TAG, "XMPPConnectionService disconnected");
		connection = null;
	}
}
