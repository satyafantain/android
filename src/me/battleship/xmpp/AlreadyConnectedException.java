package me.battleship.xmpp;

import org.jivesoftware.smack.XMPPException;

/**
 * A exception thrown when trying to establish a already opened connection
 * 
 * @author Manuel Vögele
 */
public class AlreadyConnectedException extends XMPPException
{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -1514848974921968503L;

	/**
	 * Initializes a new {@link AlreadyConnectedException}
	 */
	public AlreadyConnectedException()
	{
		// TODO Auto-generated constructor stub
	}
}
