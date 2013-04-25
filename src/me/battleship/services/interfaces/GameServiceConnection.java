package me.battleship.services.interfaces;

import java.util.List;

import me.battleship.Playground;
import me.battleship.Ship;

/**
 * The connection to the game service
 * 
 * @author Manuel Vögele
 */
public interface GameServiceConnection
{
	/**
	 * Returns the players ships
	 * 
	 * @return the players ships
	 */
	public List<Ship> getOwnShips();

	/**
	 * Returns the opponents ships
	 * 
	 * @return the opponents ships
	 */
	public List<Ship> getOpponentShips();

	/**
	 * Returns the own playground
	 * 
	 * @return the own playground
	 */
	public Playground getOwnPlayground();

	/**
	 * Returns the opponents playground
	 * 
	 * @return the opponents playground
	 */
	public Playground getOpponentPlayground();

	/**
	 * Confirms the current placement of the ships. Validates if all ships are
	 * placed correctly.
	 * 
	 * @return <code>true</code> if the ships are placed correctly and the ships
	 *         were successfully confirmed.
	 */
	public boolean confirmShips();

	/**
	 * Returns whether the game is in the placement phase
	 * 
	 * @return <code>true</code> if the game is in the placement phase
	 */
	public boolean isInPlacementPhase();
}