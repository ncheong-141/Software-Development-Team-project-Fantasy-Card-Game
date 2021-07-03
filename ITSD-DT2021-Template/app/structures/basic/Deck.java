package structures.basic;

import java.util.ArrayList;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class Deck{//class used to create and manage player and ai decks
	private ArrayList<Card> deck;// array of card objects that comprise the deck
	private ArrayList<Unit> unitDeck;//array of unit objects to complement deck
	
	public Deck() { //constructor for deck
		this.deck = new ArrayList<Card>();
		this.unitDeck = new ArrayList<Unit>();
	}
	
	public void deckOne() {// creates an instance of the human player deck
		Card card;
		Unit unit;
		String[] cardList= {// list of cards in player deck
				StaticConfFiles.c_azure_herald,
				StaticConfFiles.c_azurite_lion,
				StaticConfFiles.c_comodo_charger,
				StaticConfFiles.c_fire_spitter,
				StaticConfFiles.c_hailstone_golem,
				StaticConfFiles.c_ironcliff_guardian,
				StaticConfFiles.c_pureblade_enforcer,
				StaticConfFiles.c_silverguard_knight,
				StaticConfFiles.c_sundrop_elixir,
				StaticConfFiles.c_truestrike};
		String[] unitList= {// list of units in player deck
				StaticConfFiles.u_azure_herald,
				StaticConfFiles.u_azurite_lion,
				StaticConfFiles.u_comodo_charger,
				StaticConfFiles.u_fire_spitter,
				StaticConfFiles.u_hailstone_golem,
				StaticConfFiles.u_ironcliff_guardian,
				StaticConfFiles.u_pureblade_enforcer,
				StaticConfFiles.u_silverguard_knight};
		
		
		for (int i=0; i<=9; i++) {// cycles through the list and creates two instances of each card
			card = BasicObjectBuilders.loadCard(cardList[i], i+1, Card.class);
			deck.add(card);
			card = BasicObjectBuilders.loadCard(cardList[i], i+11, Card.class);
			deck.add(card);
			}
		for(int j=0; j<=7; j++) {//cycles through unit list and creates two of each unit
			//unit= BasicObjectBuilders.loadMonsterUnit(unitList[j], j+1, Unit.class);
			//unitDeck.add(unit);
			//unit= BasicObjectBuilders.loadMonsterUnit(unitList[j], j+11, Unit.class);
			//unitDeck.add(unit);
		}
		}
		
	public void deckTwo() {// creates AI player deck
		Card card;
		Unit unit;
		String[] cardList= {//list of cards in AI player deck
				StaticConfFiles.c_blaze_hound,
				StaticConfFiles.c_bloodshard_golem,
				StaticConfFiles.c_hailstone_golem,
				StaticConfFiles.c_planar_scout,
				StaticConfFiles.c_pyromancer,
				StaticConfFiles.c_windshrike,
				StaticConfFiles.c_serpenti,
				StaticConfFiles.c_rock_pulveriser,
				StaticConfFiles.c_entropic_decay,
				StaticConfFiles.c_staff_of_ykir};
		String[] unitList= {//list of units in AI player deck
				StaticConfFiles.u_blaze_hound,
				StaticConfFiles.u_bloodshard_golem,
				StaticConfFiles.u_hailstone_golem,
				StaticConfFiles.u_planar_scout,
				StaticConfFiles.u_pyromancer,
				StaticConfFiles.u_windshrike,
				StaticConfFiles.u_serpenti,
				StaticConfFiles.u_rock_pulveriser};
		
		for (int i=0; i<=9; i++) {//cycles through card list twice and creates two of each card and adds to deck
			card = BasicObjectBuilders.loadCard(cardList[i], i+1, Card.class);
			card.setConfigFile(cardList[i]);
			deck.add(card);
			card = BasicObjectBuilders.loadCard(cardList[i], i+11, Card.class);
			card.setConfigFile(cardList[i]);
			deck.add(card);
			}
		for(int j=0; j<=7; j++) {//cycles through unit list and creates two of each unit
			//unit= BasicObjectBuilders.loadMonsterUnit(unitList[j], j+1, Unit.class);
			//unitDeck.add(unit);
			//unit= BasicObjectBuilders.loadMonsterUnit(unitList[j], j+11, Unit.class);
			//unitDeck.add(unit);
		}
		}
		
	public void delCard(int i){// removes card from the deck and corresponding unit
		deck.remove(i);
		unitDeck.remove(i);
	}
	
	//getters and setters
	public void setDeck(ArrayList<Card> deck) {
		this.deck = deck;
	}
	public ArrayList<Card> getDeck() {
		return deck;
	}

	public ArrayList<Unit> getUnitDeck() {
		return unitDeck;
	}
	public void setUnitDeck(ArrayList<Unit> unitDeck) {
		this.unitDeck = unitDeck;
	}

	//method to test deck creation in eclipse
	public void pod(Deck deck) {
		ArrayList<Card> check= new ArrayList<Card>();
		check=deck.getDeck();
		for(int i=0; i<check.size();i++) {
			System.out.print(check.get(i).getCardname());
		}
	}
	
	public void shuffleDeck() {
		//write in sprint 4
		//connect to gameState for both decks to shuffle before game commences
		//shuffle deck, reorder unitDeck to match shuffled deck?
	}
}
	
