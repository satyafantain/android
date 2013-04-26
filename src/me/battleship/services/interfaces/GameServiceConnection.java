package me.battleship.services.interfaces;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import me.battleship.Playground;
import me.battleship.Ship;
import android.graphics.Point;

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
	 * Returns whether all ships are placed on the playground
	 * 
	 * @param ships
	 *           the ships
	 * @return <code>true</code> is all ships are placed on the playground
	 */
	public boolean areAllShipsPlaced(Collection<Ship> ships);

	/**
	 * Returns the fields on which the specified ships overlap
	 * 
	 * @param ships
	 *           the ships
	 * @return the fields on which the specified ships overlap
	 */
	public Set<Point> getInvalidFields(Collection<Ship> ships);

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