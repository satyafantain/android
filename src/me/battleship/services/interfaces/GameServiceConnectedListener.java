package me.battleship.services.interfaces;
/**
 * A listener called when the game service has connected
 * 
 * @author Manuel Vögele
 */
public interface GameServiceConnectedListener
{
	/**
	 * Called when the game service has connected
	 */
	public abstract void onGameServiceConnected();
}