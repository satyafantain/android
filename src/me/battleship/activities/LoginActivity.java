package me.battleship.activities;

import java.net.UnknownHostException;

import me.battleship.R;
import me.battleship.services.XMPPConnectionService;
import me.battleship.services.interfaces.XMPPConnection;
import me.battleship.xmpp.AlreadyConnectedException;
import me.battleship.xmpp.JID;
import me.battleship.xmpp.JID.JIDFormatException;

import org.jivesoftware.smack.XMPPException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ViewAnimator;

/**
 * The activity for logging in
 * 
 * @author Manuel Vögele
 */
public class LoginActivity extends Activity implements OnClickListener, ServiceConnection
{
	/** The log tag **/
	public final static String LOG_TAG = LoginActivity.class.getSimpleName();

	/** The task used for connecting **/
	private ConnectTask connectTask;

	/** The connection to the XMPP server **/
	XMPPConnection connection;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		findViewById(R.id.login_button).setOnClickListener(this);
		findViewById(R.id.abort_button).setOnClickListener(this);
		Intent intent = new Intent(this, XMPPConnectionService.class);
		bindService(intent, this, BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		unbindService(this);
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.abort_button)
		{
			connectTask.cancel(true);
			return;
		}
		connectTask = new ConnectTask();
		connectTask.execute(v);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service)
	{
		Log.i(LOG_TAG, "XMPPConnectionService connected");
		connection = (XMPPConnection) service;
		if (connection.isConnected())
		{
			Intent intent = new Intent(this, MatchSelectionActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name)
	{
		Log.i(LOG_TAG, "XMPPConnectionService disconnected");
		connection = null;
	}

	/**
	 * A task for connecting to a XMPP server
	 * 
	 * @author Manuel Vögele
	 */
	private class ConnectTask extends AsyncTask<View, Void, String>
	{
		/** The jabber id **/
		String jid;

		/** The password **/
		String password;

		/** The port **/
		int port;

		/**
		 * Initializes a new {@link ConnectTask}
		 */
		public ConnectTask()
		{
			// Nothing to do
		}

		@Override
		protected void onPreExecute()
		{
			jid = ((TextView) findViewById(R.id.jabber_id)).getText().toString();
			password = ((TextView) findViewById(R.id.password)).getText().toString();
			port = Integer.parseInt(((TextView) findViewById(R.id.port)).getText().toString());
			ViewAnimator animator = (ViewAnimator) findViewById(R.id.login_buttons_animator);
			animator.showNext();
		}

		@Override
		protected String doInBackground(View... params)
		{
			ConnectThread connectThread = new ConnectThread();
			connectThread.start();
			while (true)
			{
				try
				{
					Thread.sleep(200);
				}
				catch (InterruptedException e)
				{
					// Nothing to do
				}
				if (!connectThread.isAlive())
				{
					return connectThread.getResult();
				}
				if (isCancelled())
				{
					connection.disconnect();
					return null;
				}
			}
		}

		@Override
		protected void onPostExecute(String result)
		{
			ViewAnimator animator = (ViewAnimator) findViewById(R.id.login_buttons_animator);
			animator.showNext();
			if (result == null)
			{
				Intent intent = new Intent(LoginActivity.this, MatchSelectionActivity.class);
				startActivity(intent);
				return;
			}
			Builder builder = new AlertDialog.Builder(LoginActivity.this);
			builder.setMessage(result);
			builder.setCancelable(true);
			builder.setNeutralButton(R.string.ok, null);
			builder.show();
		}

		@Override
		protected void onCancelled()
		{
			ViewAnimator animator = (ViewAnimator) findViewById(R.id.login_buttons_animator);
			animator.showNext();
		}

		/**
		 * The thread handling the connection
		 *
		 * @author Manuel Vögele
		 */
		private class ConnectThread extends Thread
		{
			/**
			 * The result
			 */
			private String result = null;
		
			/**
			 * Initializes a new {@link ConnectThread}
			 */
			public ConnectThread()
			{
				// Nothing to do
			}
			
			@Override
			public void run()
			{
				try
				{
					connection.connect(new JID(jid), password, port);
					result = null;
					return;
				}
				catch (JIDFormatException e)
				{
					Log.i(LOG_TAG, "Could not connect to the XMPP server", e);
					result = getString(R.string.invalid_jid);
					return;
				}
				catch (XMPPException e)
				{
					if (e instanceof AlreadyConnectedException)
					{
						Log.e(LOG_TAG, "Could not connect to the XMPP server", e);
						result = getString(R.string.already_connected);
						return;
					}
					Throwable cause = e.getCause();
					if (cause instanceof UnknownHostException)
					{
						Log.e(LOG_TAG, "Could not connect to the XMPP server", e);
						result = getString(R.string.could_not_connect);
						return;
					}
					Log.e(LOG_TAG, "Could not connect to the XMPP server", e);
					result = getString(R.string.error_while_connecting);
					return;
				}
			}
			
			/**
			 * Returns the result
			 * @return the result
			 */
			public String getResult()
			{
				return result;
			}
		}
	}
}