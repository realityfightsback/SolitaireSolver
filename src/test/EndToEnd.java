package test;

import obj.Board;
import obj.Deck;
import obj.GameState;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EndToEnd {

	@Before
	public void setUp() {
		GameState.resetStaticState();
	}

	/**
	 * No possible moves from the given state. We expect this to realize there
	 * are no states worth investigating (no flips that would be beneficial,
	 * nothing available) and finish processing.
	 */
	@Test
	public void noMoves() {
		loader("NoMoves", 1, false, false);
	}

	@Test
	public void simpleBoard() {
		loader("EasyGame2", 60, true, true);
	}

	/**
	 * A simple GameState with no DrawPile (only stacks on the board). The board
	 * can all be scored.
	 */
	@Test
	public void simpleBoardNoDrawPile() {

		Board b = new Board(new Deck("EasyGame1 (No DrawPile)"));
		b.drawPile.hasAccessibleCard = false;
		b.drawPile.setCanHit(false);

		GameState g = new GameState(b);

		GameState.setDrawSolutionOn();

		g.performGameLogic();

		Assert.assertEquals(
				"Did not take the expect number of game state investigations",
				29, GameState.getStateNumber());

		Assert.assertTrue(GameState.solutionWasFound);
	}

	/**
	 * A simple GameState with only the DrawPile (nothing on the board). The
	 * DrawPile is set up so that all the cards can be scored.
	 */
	@Test
	public void simpleBoardOnlyDrawPile() {
		loader("EasyGame3 (Only DrawPile)", 18, true, true);

	}

	/**
	 * A Game I played and was able to beat, transcribed from Windows (painful)
	 */
	@Test
	public void nonTrivialCase() {
		loader("NonTrivial", -1, false, true);
	}

	/**
	 * This one takes a lot more states, look at, maybe some optimizations in
	 * order.
	 */
	@Test
	public void nonTrivialCase2() {
		loader("NonTrivial2", -1, false, true);
	}

	private void loader(String fileName, int statesExpected,
			boolean checkPerformance, boolean solutionExpected) {
		Board b = new Board(new Deck(fileName));

		GameState g = new GameState(b);

		GameState.setDrawSolutionOn();

		g.performGameLogic();

		if (checkPerformance) {
			Assert.assertEquals(
					"Did not take the expect number of game state investigations",
					statesExpected, GameState.getStateNumber());
		}
		if (solutionExpected) {
			Assert.assertTrue(GameState.solutionWasFound);
		} else {
			Assert.assertFalse(GameState.solutionWasFound);
		}
	}
}
