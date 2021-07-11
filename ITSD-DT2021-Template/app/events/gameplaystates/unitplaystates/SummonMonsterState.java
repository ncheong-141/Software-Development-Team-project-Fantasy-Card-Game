package events.gameplaystates.unitplaystates;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import commands.GeneralCommandSets;
import events.gameplaystates.GameplayContext;
import events.gameplaystates.tileplaystates.ITilePlayStates;
import structures.GameState;
import structures.basic.abilities.*;
import structures.basic.Avatar;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.HumanPlayer;
import structures.basic.Monster;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class SummonMonsterState implements IUnitPlayStates {

	/*** State attributes ***/
	private Tile 				targetTile;
	private ArrayList <Tile> 	summonRange;
	
	
	/*** State constructor ***/
	/* 
	 * Changed constructor to input current and target tiles to decouple Unit states from TileClicked
	 * Previously Unit states had tilex, tiley be used from context which were variables recieved from TileClicked. 
	 * Decoupling required to use unit States from the ComputerPlayer. */
	
	public SummonMonsterState(Tile targetTile) {
		this.targetTile = targetTile;
		this.summonRange = null;
	}
	
	
	/*** State method ***/
	
	public void execute(GameplayContext context) {
		
		System.out.println("In SummonMonsterSubState.");
		

		/**===========================================**/
		context.getGameStateRef().userinteractionLock();
		/**===========================================**/

		
		
		/** Build summon range **/
		// Use adjusted range is monster has special skill on summoe
		if (context.getGameStateRef().useAdjustedMonsterActRange()) {
			
			// Abilities with adjusted act range populate this container with adjusted range
			summonRange = context.getGameStateRef().getTileAdjustedRangeContainer();
		}
		else {
			summonRange = context.getGameStateRef().getBoard().allSummonableTiles(context.getGameStateRef().getTurnOwner());
		}
		
		// Target tile within summon range && sufficient player mana check
		if(tileInSummonRange() && sufficientMana(context.getGameStateRef().getTurnOwner(), context.getLoadedCard())) {
			
			// Execute summon method
			summonMonster(context, context.out, context.getLoadedCard().getConfigFile(), context.getLoadedCard(), this.targetTile);
			
			// Update board counter for num Monsters
			context.getGameStateRef().getBoard().updateUnitCount(1);

			/** Delete card from Hand + update visual **/
			
			// Index variables
			int cardIndexInHand = context.getGameStateRef().getTurnOwner().getHand().getSelCarPos(); 
			int oldHandSize =  context.getGameStateRef().getTurnOwner().getHand().getHandList().size(); 	// How many UI cards to delete

			// Remove card
			context.getGameStateRef().getTurnOwner().getHand().removeCard(cardIndexInHand);
			
			/** Reset entity selection and board **/  
			// Deselect after action finished
			context.deselectAllAfterActionPerformed();
			
			// Only update Hand for Human player
			if (context.getGameStateRef().getTurnOwner() instanceof HumanPlayer) {
				GeneralCommandSets.drawCardsInHand(context.out, context.getGameStateRef(), oldHandSize, context.getGameStateRef().getTurnOwner().getHand().getHandList());
			}
		
			// Reset board visual (highlighted tiles)
			GeneralCommandSets.boardVisualReset(context.out, context.getGameStateRef());
		}
		
		else {
		// Verbose console messages for debugging, simplify for submission
			
			if(!tileInSummonRange()) {
				System.out.println("Tile is not in summon range.");
			} else if(!(sufficientMana(context.getGameStateRef().getTurnOwner(), context.getLoadedCard()))) {
				System.out.println("Insufficient mana to summon this monster.");
				BasicCommands.addPlayer1Notification(context.out, "Get some mana!!", 1);
				GeneralCommandSets.boardVisualReset(context.out, context.getGameStateRef());
			} else {
				System.out.println("Can't summon Monster, please try again.");
			}
			
		}
		
		// Short thread sleep before unlock to allow UI to update
		
		/**===========================================**/
		context.getGameStateRef().userinteractionUnlock();
		/**===========================================**/
	}

	
	public void summonMonster(GameplayContext context, ActorRef out, String u_configFile, Card statsRef, Tile summonTile) {
		
		// Mana cost
		BasicCommands.addPlayer1Notification(out, "Player mana cost", 2);
		context.getGameStateRef().getTurnOwner().loseMana(statsRef.getManacost());
		
		if(context.getGameStateRef().getTurnOwner() == context.getGameStateRef().getPlayerOne()) {
			BasicCommands.setPlayer1Mana(out, context.getGameStateRef().getTurnOwner());
			GeneralCommandSets.threadSleep();
		} else {
			BasicCommands.setPlayer2Mana(out, context.getGameStateRef().getTurnOwner());
			GeneralCommandSets.threadSleep();
		}
		
		// Summon the Monster (instantiate)
		BasicCommands.addPlayer1Notification(out, "drawUnit", 2);
		
		// Need some code about retrieving StaticConfFiles matching card from Deck here
		Monster summonedMonster = (Monster) BasicObjectBuilders.loadMonsterUnit(u_configFile,statsRef,context.getGameStateRef().getTurnOwner(),Monster.class);		
		summonedMonster.setPositionByTile(context.getGameStateRef().getBoard().getTile(summonTile.getTilex(),summonTile.getTiley()));
		summonedMonster.setOwner(context.getGameStateRef().getTurnOwner());
		GeneralCommandSets.threadSleep(); 
		
		// Add unit to tile ON BOARD
		BasicCommands.addPlayer1Notification(out, "Monster added to tile", 2);
		summonTile.addUnit(summonedMonster);
		GeneralCommandSets.threadSleep();
		
		// Summon animation
		EffectAnimation summonEf = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
		BasicCommands.playEffectAnimation(out, summonEf, summonTile);
		
		// Drawing summoned monster with stats on the board
		GeneralCommandSets.drawUnitWithStats(out, summonedMonster, summonTile);
		GeneralCommandSets.threadSleep();
		
		// Check for on-summon triggers: permanent changes to Monster statistics, abilities activated by summoning
		checkForSummonTriggers(summonedMonster, context);
		
	}
	
	
	
	// Checking for triggered abilities: construction-related, summon-method related
	private void checkForSummonTriggers(Monster summonedMonster, GameplayContext context) {
		
		if(summonedMonster.hasAbility()) {	
			checkForConstruction(summonedMonster, context);
			checkForSummon(summonedMonster, context);	
		}	
		
	}
	
	// Abilities activated directly after object construction to permanently modify the Unit
	private void checkForConstruction(Monster summonedMonster, GameplayContext context) {
		for(Ability a : summonedMonster.getMonsterAbility()) {
			if(a.getCallID() == Call_IDs.construction) {
				a.execute(summonedMonster, context.getGameStateRef());
			}
		}
	}
	
	// Abilities activated by the act of summoning in the game logic
	private void checkForSummon(Monster summonedMonster, GameplayContext context) {
		for(Ability a : summonedMonster.getMonsterAbility()) {
			if(a.getCallID() == Call_IDs.onSummon) {
				System.out.println("Ability:" + a);
				
				// Target logic
				if (a.getTargetType() == Avatar.class) {
					
					if (a.targetEnemy() == false) {						
						// Buff abilities
						if(a instanceof A_U_HealAvatarHPIfSummoned) {
							BasicCommands.playUnitAnimation(context.out, summonedMonster, UnitAnimationType.channel);
							BasicCommands.playEffectAnimation(context.out, BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff), context.getGameStateRef().getHumanAvatar().getPosition().getTile(context.getGameStateRef().getBoard()));
							try {Thread.sleep(1300);} catch (InterruptedException e) {e.printStackTrace();}	
							a.execute(context.getGameStateRef().getHumanAvatar(), context.getGameStateRef());
							GeneralCommandSets.drawUnitWithStats(context.out, summonedMonster, targetTile);
						}

					}
					else {
						a.execute(context.getGameStateRef().getComputerAvatar(), context.getGameStateRef());
					}
				}
				else if (a.getTargetType() == Monster.class) {
						
					if (a.targetEnemy() == false) {
						// Assume can only target itself or avatars
						a.execute(summonedMonster, context.getGameStateRef());
					} else {
						// Not acting on enemy monsters atm? 
					}		
				} else {
					
					// Draw cards on summon
					a.execute(null, context.getGameStateRef());
					
					// Redraw hand for Player
					int i = 0;	
					for(Card c : context.getGameStateRef().getTurnOwner().getHand().getHandList()) { // get list of cards from Hand from Player
						BasicCommands.drawCard(context.out, c, i, 0);
						i++;
						GeneralCommandSets.threadSleep(); 
					}
					
				}
			}
		}
	}
	
	/*	Helper methods	*/
	
	// Returns true if the clicked targetTile is within a Player's summon range
	private boolean tileInSummonRange() {
		if(summonRange.contains(targetTile)) {	return true;	}
		return false;
	}
	
	// Returns true if Player has sufficient mana to cover the card's playing cost
	private boolean sufficientMana(Player p, Card mon) {
		System.out.println("Turn owner has: " + p.getMana() + " mana");
		if(p.getMana() - mon.getManacost() >= 0) {	return true;	}
		return false;
	}
	
}


