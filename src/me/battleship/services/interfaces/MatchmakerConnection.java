package me.battleship.services.interfaces;

import org.jivesoftware.smack.XMPPException;

/**
 * The connection to the matchmaker
 * 
 * @author Manuel Vögele
 */
public interface MatchmakerConnection
{
	/**
	 * Adds the client to the queue on the matchmaker
	 * 
	 * @throws XMPPException
	 *            if queuing fails
	 */
	public void queue() throws XMPPException;

	/**
	 * Closes any background processes. The instance will be unstable after that
	 */
	public void cleanup();
}
