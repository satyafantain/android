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
	 * Disconnects from the XMPP server
	 */
	public void disconnect();
}
