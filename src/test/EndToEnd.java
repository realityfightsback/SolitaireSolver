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

	// @Test
	public void noMoves() {

		Board b = new Board(
				new Deck(
						"C:\\Users\\User\\Desktop\\Dev\\workspaces\\JEE\\SolitaireSolver\\NoMoves"));
		GameState g = new GameState(b);

		g.performGameLogic();

		Assert.assertEquals(
				"Did not take the expect number of game state investigations",
				1, GameState.getStateNumber());

		Assert.assertFalse(GameState.solutionWasFound);
	}

	@Test
	public void simpleBoard() {

		Board b = new Board(
				new Deck(
						"C:\\Users\\User\\Desktop\\Dev\\workspaces\\JEE\\SolitaireSolver\\EasyGame2"));
		GameState g = new GameState(b);

		GameState.setDrawSolutionOn();

		g.performGameLogic();

		Assert.assertEquals(
				"Did not take the expect number of game state investigations",
				60, GameState.getStateNumber());

		Assert.assertTrue(GameState.solutionWasFound);
	}

	// @Test
	public void simpleBoardNoDrawPile() {

		Board b = new Board(
				new Deck(
						"C:\\Users\\User\\Desktop\\Dev\\workspaces\\JEE\\SolitaireSolver\\EasyGame1 (No DrawPile)"));
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

	// @Test
	public void simpleBoardOnlyDrawPile() {

		Board b = new Board(
				new Deck(
						"C:\\Users\\User\\Desktop\\Dev\\workspaces\\JEE\\SolitaireSolver\\EasyGame3 (Only DrawPile)"));

		GameState g = new GameState(b);

		GameState.setDrawSolutionOn();

		g.performGameLogic();

		Assert.assertEquals(
				"Did not take the expect number of game state investigations",
				18, GameState.getStateNumber());

		Assert.assertTrue(GameState.solutionWasFound);
	}

}
