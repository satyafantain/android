package me.battleship.services;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.battleship.services.interfaces.MatchmakerConnection;
import me.battleship.services.interfaces.MatchmakerMessageListener;
import me.battleship.services.interfaces.OpponentConnection;
import me.battleship.services.interfaces.OpponentMessageListener;
import me.battleship.services.interfaces.XMPPConnection;
import me.battleship.xmpp.AlreadyConnectedException;
import me.battleship.xmpp.BattleshipPacketExtension;
import me.battleship.xmpp.ExtensionElements;
import me.battleship.xmpp.JID;
import me.battleship.xmpp.MessageUtil;
import me.battleship.xmpp.message.QueueMessage;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
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

	/** The JID of the matchmaker */
	public static final String MATCHMAKER_JID = "matchmaker@battleship.me";

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

	/**
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
		public MatchmakerConnection getMatchmakerConnection(MatchmakerMessageListener listener)
		{
			Chat chat = connection.getChatManager().createChat(MATCHMAKER_JID, null);
			return new MatchmakerConnectionImpl(chat, listener);
		}

		@Override
		public OpponentConnection getOpponentConnection(String opponentJID, OpponentMessageListener listener)
		{
			Chat chat = connection.getChatManager().createChat(opponentJID, null);
			return new OpponentConnectionImpl(chat, listener);
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

		/**
		 * Writes an unparsable message to the log
		 * 
		 * @param message
		 *           the message
		 */
		void logUnparsableMessage(Message message)
		{
			Log.i(LOG_TAG, "Could not parse received message: " + message.toXML());
		}

		/**
		 * The implementation of the {@link OpponentConnection} interface
		 * 
		 * @author Manuel Vögele
		 */
		private class OpponentConnectionImpl implements OpponentConnection, MessageListener
		{
			/** The chat */
			private Chat chat;

			/** The listener for messages from the opponent */
			private OpponentMessageListener listener;

			/**
			 * Instantiates a new {@link OpponentConnectionImpl}
			 * 
			 * @param chat
			 *           the chat
			 * @param listener
			 *           the listener for messages from the matchmaker
			 */
			public OpponentConnectionImpl(Chat chat, OpponentMessageListener listener)
			{
				this.chat = chat;
				this.listener = listener;
				chat.addMessageListener(this);
			}

			@Override
			public void sendDiceRoll(int dice)
			{
				// TODO Auto-generated method stub
			}

			@Override
			public void processMessage(Chat arg0, Message arg1)
			{
				// TODO Auto-generated method stub
			}
		}

		/**
		 * The implementation of the {@link MatchmakerConnection} interface
		 * 
		 * @author Manuel Vögele
		 */
		private class MatchmakerConnectionImpl extends TimerTask implements MatchmakerConnection, MessageListener
		{
			/** The chat */
			private Chat chat;

			/** The listener for messages from the opponent */
			private MatchmakerMessageListener listener;

			/** The queue id */
			private String queueId;

			/** A timer */
			private Timer timer;

			/**
			 * Instantiates a new {@link MatchmakerConnectionImpl}
			 * 
			 * @param chat
			 *           the chat
			 * @param listener
			 *           the listener for messages from the matchmaker
			 */
			public MatchmakerConnectionImpl(Chat chat, MatchmakerMessageListener listener)
			{
				this.chat = chat;
				chat.addMessageListener(this);
				this.listener = listener;
				this.timer = new Timer(true);
			}

			@Override
			public void queue() throws XMPPException
			{
				Log.i(LOG_TAG, "Queuing at matchmaker");
				if (queueId != null)
				{
					Log.i(LOG_TAG, "Allready queued. Queuing aborted.");
					return;
				}
				chat.sendMessage(new QueueMessage());
			}

			@Override
			public void cleanup()
			{
				chat.removeMessageListener(this);
				timer.cancel();
			}

			@Override
			public void processMessage(@SuppressWarnings("hiding") Chat chat, Message message)
			{
				BattleshipPacketExtension root = MessageUtil.getPacketExtension(message, ExtensionElements.BATTLESHIP);
				BattleshipPacketExtension queuing = root.getSubElement(ExtensionElements.QUEUEING);
				Map<String, String> attributes = queuing.getAttributes();
				String action = attributes.get("action");
				if (action == null)
				{
					logUnparsableMessage(message);
					return;
				}
				if (action.equals("success"))
				{
					queueId = attributes.get("id");
					if (Log.isLoggable(LOG_TAG, Log.INFO))
						Log.i(LOG_TAG, "Received queue id " + queueId);
					timer.scheduleAtFixedRate(this, 15000, 15000);
					return;
				}
				if (action.equals("ping"))
				{
					Log.i(LOG_TAG, "Received ping from Matchmaker");
					return;
				}
				if (action.equals("assign"))
				{
					timer.cancel();
					String opponentJID = attributes.get("jid");
					String matchId = attributes.get("mid");
					if (Log.isLoggable(LOG_TAG, Log.INFO))
						Log.i(LOG_TAG, "Assigned to: " + opponentJID + " mid: " + matchId);
					try
					{
						chat.sendMessage(new QueueMessage(opponentJID, matchId));
					}
					catch (XMPPException e)
					{
						Log.e(LOG_TAG, "Error while confirming assignment", e);
					}
					chat.removeMessageListener(this);
					queueId = null;
					listener.onOpponentAssigned(opponentJID, matchId);
					return;
				}
				logUnparsableMessage(message);
			}

			@Override
			public void run()
			{
				Log.i(LOG_TAG, "Sending ping to Matchmaker");
				try
				{
					chat.sendMessage(new QueueMessage(queueId));
				}
				catch (XMPPException e)
				{
					Log.e(LOG_TAG, "Error while sending ping to Matchmaker", e);
				}
			}
		}
	}
}