package events;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import commands.GeneralCommandSets;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Monster;
import structures.basic.Tile;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * somewhere that is not on a card tile or the end-turn button.
 * 
 * { 

 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class OtherClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		// Check if locked, dont not execute anything if so
		if (gameState.userinteractionLocked()) {
			return;
		}
		
		// Lock user interaction during action
		/**===========================**/
		gameState.userinteractionLock();
		/**===========================**/


		/* Entity deselection and board reset */
		gameState.deselectAllEntities();
		GeneralCommandSets.boardVisualReset(out, gameState);
		if(gameState.getTurnOwner() == gameState.getPlayerOne()) {
			GeneralCommandSets.drawCardsInHand(out, gameState, gameState.getTurnOwner().getHand().getCurr(), gameState.getTurnOwner().getHand().getHandList());
		}

		
		/**===========================**/
		gameState.userinteractionUnlock();
		/**===========================**/
		
	}

}


