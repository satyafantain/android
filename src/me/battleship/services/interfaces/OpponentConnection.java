package me.battleship.services.interfaces;

/**
 * The connection to the opponent
 * 
 * @author Manuel Vögele
 */
public interface OpponentConnection
{
	/**
	 * Send a dice roll to the opponent
	 * 
	 * @param dice
	 *           the dice roll
	 */
	public void sendDiceRoll(int dice);
}
