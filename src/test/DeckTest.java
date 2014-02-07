package test;

import obj.Deck;
import obj.Rank;

import org.junit.Assert;
import org.junit.Test;

public class DeckTest {

	@Test
	public void testDeck() {
		Deck d = new Deck();
		Assert.assertEquals(d.cards.length, 52);

		for (Rank rank : Rank.values()) {
			int totalFound = 0;
			for (int i = 0; i < d.cards.length; i++) {
				if (d.getCard(i).rank.equals(rank))
					totalFound++;
			}
			Assert.assertEquals("Not enough" + rank + " found", 4, totalFound);

		}

	}

}
