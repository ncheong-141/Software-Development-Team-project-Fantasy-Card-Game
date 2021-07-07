package structures.basic;

import java.util.ArrayList;
import structures.basic.Unit;
import structures.basic.abilities.Ability;
import structures.basic.abilities.AbilityToUnitLinkage;
import utils.BasicObjectBuilders;


/**
 * This is the base representation of a Card which is rendered in the player's hand.
 * A card has an id, a name (cardname) and a manacost. A card then has a large and mini
 * version. The mini version is what is rendered at the bottom of the screen. The big
 * version is what is rendered when the player clicks on a card in their hand.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Card implements Comparable<Card> {
	
	private int id;//unique identifier for each card
	
	private String cardname;//name of card
	private int manacost;//mana cost to play associated unit
	
	private MiniCard miniCard;//display element for unselected cards
	private BigCard bigCard;//display element for selected card
	
	private String configFile;
	private ArrayList<Ability> abilityList;
	private Class<?> associatedClass;
	
	public Card() {};
	
	public Card(int id, String cardname, int manacost, MiniCard miniCard, BigCard bigCard) {
		super();
		this.id = id;
		this.cardname = cardname;
		this.manacost = manacost;
		this.miniCard = miniCard;
		this.bigCard = bigCard;
		this.configFile="";
		this.abilityList=new ArrayList<Ability>();
		this.associatedUnitClass = Card.class;
	}
	
	
	//shortcut methods for ability access
	
	//checks whether card targets spell or enemy
	public boolean targetEnemy() {
		boolean result=false;
		for (Ability a: this.abilityList) {
			if (a.targetEnemy()==true){
				result=true;
			}
			else {
				result=false;
			}
		}
		return result;
	}
	
	//checks that monster/spell associated with card has an ability
	public boolean hasAbility(){
		boolean result= false;
			if(this.abilityList!=null) {
				result=true;
			}
		return result;
		}

	//special getter methods to aid with ai logic decisions getting card(if monster) health and attack
	public int getCardHP(){
		return this.getBigCard().getHealth();
	}
	public int getCardAttack() {
		return this.getBigCard().getAttack();
	}
	
	
	//helper method to show where card is playable
	public boolean playableAnywhere() {
		if(hasAbility()) {
			for(Ability a: this.abilityList) {
			if(a.getClass()==A_U_SummonAnywhere.class) {
				return true;
			}
		}
		}else {
			return false;
		}
	}
	
	//method to return integer value of unit ability effect 
	//i.e if ability is +2 damage, the method would return 2
//	public int getAbilityEffect() {
//		int resultOne;
//		for(Ability a: this.abilityList) {
//			a.
//		}
//		return resultOne;
//	}
	
	@Override
	//compares mana cost of two cards for ai logic
	public int compareTo(Card o) {
		if(this.manacost>o.getManacost()) {
			return 1;
		}else if(this.manacost<o.getManacost()){
			return -1;
		}else {
		return 0;
		}
	}
	
	//getters and setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCardname() {
		return cardname;
	}
	public void setCardname(String cardname) {
		this.cardname = cardname;
	}
	public int getManacost() {
		return manacost;
	}
	public void setManacost(int manacost) {
		this.manacost = manacost;
	}
	public MiniCard getMiniCard() {
		return miniCard;
	}
	public void setMiniCard(MiniCard miniCard) {
		this.miniCard = miniCard;
	}
	public BigCard getBigCard() {
		return bigCard;
	}
	public void setBigCard(BigCard bigCard) {
		this.bigCard = bigCard;
	}
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}
	public String getConfigFile() {
		return this.configFile;
	}
	public ArrayList<Ability> getAbilityList() {
		return abilityList;
	}
	public void setAbilityList(ArrayList<Ability> abilityList) {
		this.abilityList = abilityList;
	}
	public Class<?> getAssociatedClass() {
		return associatedUnitClass;
	}
	public void setAssociatedClass(Class<?> associatedUnitClass) {
		this.associatedUnitClass = associatedUnitClass;
	}
}


