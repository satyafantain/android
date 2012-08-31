package me.battleship.services;

import me.battleship.services.interfaces.XMPPConnection;
import me.battleship.xmpp.AlreadyConnectedException;
import me.battleship.xmpp.JID;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Presence.Type;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * A service handling a connection to a XMPP server
 * 
 * @author Manuel Vögele
 */
public class XMPPConnectionService extends Service
{
	/** The log tag */
	public static final String LOG_TAG = XMPPConnectionService.class.getSimpleName();

	/** The default resource */
	public static final String DEFAULT_RESOURCE = "battleshipme";

	/** The connection to the XMPPP server */
	private XMPPConnection xmppConnection;

	@Override
	public IBinder onBind(Intent intent)
	{
		XMPPConnectionBinder binder = new XMPPConnectionBinder();
		xmppConnection = binder;
		return binder;
	}

	@Override
	public void onDestroy()
	{
		xmppConnection.disconnect();
	}

	/**onDestroy
	 * A binder for the {@link XMPPConnectionService}
	 * 
	 * @author Manuel Vögele
	 */
	private class XMPPConnectionBinder extends Binder implements XMPPConnection
	{
		/** The connection to the XMPP server **/
		private org.jivesoftware.smack.XMPPConnection connection;

		/**
		 * Initializes a new {@link XMPPConnectionBinder}
		 */
		public XMPPConnectionBinder()
		{
			// Nothing to do
		}

		@Override
		public void connect(JID jid, String password, int port) throws XMPPException
		{
			if (isConnected())
				throw new AlreadyConnectedException();
			try
			{
				String domain = jid.getDomain();
				connection = new org.jivesoftware.smack.XMPPConnection(new ConnectionConfiguration(domain, port));
				Log.i(LOG_TAG, "Connecting to " + domain + ":" + port);
				connection.connect();
				String resource = jid.getResource();
				if (resource == null)
				{
					Log.d(LOG_TAG, "No resource specified. Using default resource " + DEFAULT_RESOURCE);
					resource = DEFAULT_RESOURCE;
				}
				String node = jid.getNode();
				if (Log.isLoggable(LOG_TAG, Log.DEBUG))
				{
					Log.d(LOG_TAG, "Logging in.\nNode: " + node + "\nDomain: " + domain + "\nResource: " + resource);
				}
				connection.login(node, password, resource);
				connection.sendPacket(new Presence(Type.available, "ready", -128, Mode.available));
			}
			catch (XMPPException e)
			{
				disconnect();
				throw e;
			}
			catch (NullPointerException e)
			{
				Log.i(LOG_TAG, "Null Pointer Exception while connecting. Was the connection process aborted?", e);
			}
		}

		@Override
		public boolean isConnected()
		{
			return connection != null;
		}

		@Override
		public void disconnect()
		{
			final org.jivesoftware.smack.XMPPConnection con = connection;
			if (isConnected())
			{
				new Thread()
				{
					@Override
					public void run()
					{
						con.disconnect();
					}
				}.start();
				connection = null;
			}
		}
	}
}