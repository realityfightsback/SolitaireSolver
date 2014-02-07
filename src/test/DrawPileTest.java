package test;

import static org.junit.Assert.*;

import java.util.HashSet;
import obj.Card;
import obj.Deck;
import obj.DrawPile;
import obj.Rank;
import obj.Suit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DrawPileTest {
	DrawPile drawPile;

	DrawPile smallDrawPile;

	/**
	 * A pile with the top cards being 5C, 8C, JC, AC It descends
	 */
	@Before
	public void setup() {
		Deck deck = new Deck();
		deck.addCard(new Card(Suit.CLUBS, Rank.ACE), 51);
		deck.addCard(new Card(Suit.CLUBS, Rank.KING), 50);
		deck.addCard(new Card(Suit.CLUBS, Rank.QUEEN), 49);
		deck.addCard(new Card(Suit.CLUBS, Rank.JACK), 48);
		deck.addCard(new Card(Suit.CLUBS, Rank.TEN), 47);
		deck.addCard(new Card(Suit.CLUBS, Rank.NINE), 46);
		deck.addCard(new Card(Suit.CLUBS, Rank.EIGHT), 45);
		deck.addCard(new Card(Suit.CLUBS, Rank.SEVEN), 44);
		deck.addCard(new Card(Suit.CLUBS, Rank.SIX), 43);
		deck.addCard(new Card(Suit.CLUBS, Rank.FIVE), 42);
		deck.addCard(new Card(Suit.CLUBS, Rank.FOUR), 41);
		deck.addCard(new Card(Suit.CLUBS, Rank.THREE), 40);

		drawPile = new DrawPile();

		drawPile.initialize(40, deck);

		Deck deck2 = new Deck();

		deck2.addCard(new Card(Suit.CLUBS, Rank.SIX), 51);
		deck2.addCard(new Card(Suit.CLUBS, Rank.FIVE), 50);
		deck2.addCard(new Card(Suit.CLUBS, Rank.FOUR), 49);
		deck2.addCard(new Card(Suit.CLUBS, Rank.THREE), 48);

		smallDrawPile = new DrawPile();

		smallDrawPile.initialize(48, deck2);

	}

	@Test
	public void simpleTest() {

		if (drawPile.hasAccessibleCard) {
			assertHelper(drawPile.peek(), Rank.FIVE);
			assertHelper(drawPile.remove(), Rank.FIVE);

			assertHelper(drawPile.peek(), Rank.FOUR);
			assertHelper(drawPile.remove(), Rank.FOUR);

			assertHelper(drawPile.peek(), Rank.THREE);
			assertHelper(drawPile.remove(), Rank.THREE);

		}

		Assert.assertFalse("Drawpile doesn't reflect lack of accessible cards",
				drawPile.hasAccessibleCard);
	}

	@Test
	public void simpleFlip() {
		// Remove the Five
		drawPile.remove();
		drawPile.flipThree();

		assertHelper(drawPile.remove(), Rank.EIGHT);
		assertHelper(drawPile.remove(), Rank.SEVEN);
		assertHelper(drawPile.remove(), Rank.SIX);
		assertHelper(drawPile.remove(), Rank.FOUR);

	}

	@Test
	public void simpleFlipExpectedFail() {
		// Remove the Five
		drawPile.remove();
		drawPile.flipThree();

		assertHelper(drawPile.remove(), Rank.EIGHT);
		assertHelper(drawPile.remove(), Rank.SEVEN);
		assertHelper(drawPile.remove(), Rank.SIX);

		assertNotEquals(drawPile.remove(), new Card(Suit.CLUBS, Rank.THREE));
	}

	@Test
	public void testLimiting() {
		for (int i = 0; i < 6; i++) {
			smallDrawPile.flipThree();
		}
		Assert.assertFalse("Drawing was not properly cut off",
				smallDrawPile.canHit());
	}

	/**
	 * Properly removes nulls
	 */
	@Test
	public void loopingWorks() {
		smallDrawPile.remove();
		smallDrawPile.flipThree();
		smallDrawPile.flipThree();

		assertHelper(smallDrawPile.remove(), Rank.SIX);
	}

	@Test
	public void remainingCard() {
		smallDrawPile.flipThree();

		assertHelper(smallDrawPile.remove(), Rank.SIX);
	}

	@Test
	public void remainingTopCards() {
		HashSet<Card> set = drawPile.getTopCardsLeft();
		assertEquals(4, set.size());

		assertHelper(set, Rank.ACE);
		assertHelper(set, Rank.JACK);
		assertHelper(set, Rank.EIGHT);
		assertHelper(set, Rank.FIVE);

	}

	/**
	 * Removes cards until no more pop up. Must flip to get more cards.
	 */
	@Test
	public void removeUntilFlipRequiredForCards() {
		drawPile.remove();
		drawPile.remove();
		drawPile.remove();

		assertFalse(drawPile.hasAccessibleCard);

		assertTrue(drawPile.canHit());
		assertFalse(drawPile.isEmpty());
		drawPile.flipThree();

		assertTrue(drawPile.hasAccessibleCard);
		assertNotNull(drawPile.remove());

		assertTrue(drawPile.hasAccessibleCard);
		assertNotNull(drawPile.remove());

		assertTrue(drawPile.hasAccessibleCard);
		assertNotNull(drawPile.remove());

		assertFalse(drawPile.hasAccessibleCard);
	}

	public void assertHelper(Card c, Rank rank) {
		Assert.assertEquals(c, new Card(Suit.CLUBS, rank));

	}

	public void assertHelper(HashSet<Card> set, Rank rank) {
		assertTrue(set.contains(new Card(Suit.CLUBS, rank)));

	}
}
