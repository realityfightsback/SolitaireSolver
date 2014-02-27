package test;

import obj.Card;
import obj.Deck;
import obj.Rank;
import obj.Suit;

import org.junit.Assert;
import org.junit.Test;

public class DeckTest {
	/**
	 * Tests that the deck is properly generated (52 cards, 4 of each rank, 13
	 * of each suit)
	 */
	@Test
	public void testDeck() {
		Deck d = new Deck();
		
		testlogic(d);
	}

	public static void testlogic(Deck d) {
		Assert.assertEquals(d.cards.length, 52);

		for (Rank rank : Rank.values()) {
			int totalFound = 0;
			for (int i = 0; i < d.cards.length; i++) {
				if (d.getCard(i).rank.equals(rank))
					totalFound++;
			}
			Assert.assertEquals("Not enough" + rank + " found", 4, totalFound);

		}

		for (Suit suit : Suit.values()) {
			int totalFound = 0;
			for (Card c : d.cards) {
				if (c.suit.equals(suit)) {
					totalFound++;
				}
			}
			Assert.assertEquals("Not enough" + suit + " found", 13, totalFound);

		}		
	}
	


}
