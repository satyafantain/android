package me.battleship.services.interfaces;

import me.battleship.xmpp.JID;

import org.jivesoftware.smack.XMPPException;

/**
 * A connection to a XMPP server
 * 
 * @author Manuel Vögele
 */
public interface XMPPConnection
{
	/**
	 * Establishes a connection to the XMPP server
	 * 
	 * @param jid
	 *           the jabber id
	 * @param password
	 *           the passwort
	 * @param port
	 *           the port
	 * 
	 * @throws XMPPException
	 *            if connecting to the XMPP server fails
	 */
	public void connect(JID jid, String password, int port) throws XMPPException;

	/**
	 * Returns whether a connection is established
	 * 
	 * @return <code>true</code> if a connection is established
	 */
	public boolean isConnected();

	/**
	 * Returns the connection to the matchmaker
	 * 
	 * @param listener
	 *           a listener for messages from the matchmaker
	 * @return the connection to the matchmaker
	 */
	public MatchmakerConnection getMatchmakerConnection(MatchmakerMessageListener listener);

	/**
	 * Returns the opponent connection for the specified JID
	 * 
	 * @param opponentJID
	 *           the JID of the opponent
	 * @param listener
	 *           a listener for messages from the opponent
	 * @return the connection to the opponent
	 */
	public OpponentConnection getOpponentConnection(String opponentJID, OpponentMessageListener listener);

	/**
	 * Disconnects from the XMPP server
	 */
	public void disconnect();
}
