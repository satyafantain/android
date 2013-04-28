package me.battleship.services.interfaces;

/**
 * A listener for messages from the matchmaker
 * 
 * @author Manuel Vögele
 */
public interface MatchmakerMessageListener
{
	/**
	 * Called when an opponent has been assigned
	 * 
	 * @param opponentJID
	 *           the jid of the opponent
	 * @param matchId
	 *           the id of the match
	 */
	public void onOpponentAssigned(String opponentJID, String matchId);
}
