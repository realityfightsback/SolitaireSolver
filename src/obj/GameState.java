package obj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import actions.DrawPileHit;
import actions.Move;
import actions.MoveComparator;
import actions.MoveOrigin;
import actions.RepositioningMove;
import actions.ScoringMove;
import util.DeepCopy;
import util.StatePrinter;

public class GameState {
	// Debugging options
	private static boolean drawSolutionOn = false;
	private static boolean consoleOutputOn = false;

	public long thisGameStateNumber = -1;

	GameState parentState = null;
	Move moveAssociatedWithThisState = null;

	// Parent HashSet. Holds all states that have been looked at. All GameStates
	// share this. Should be static?
	HashSet<GameState> investigatedGameStates = null;

	public Board board;
	public Deck deck;
	// All available moves from this state
	public ArrayList<Move> possibleMoves = new ArrayList<Move>();
	// The possible move from the List of possible Moves that we are currently
	// investigating
	int currentlyInvestigatedMove = 0;

	int numberOfKingsExposed = 0;

	// State associated with all GameStates of the currently investigated Game
	private static long totalGameStates = 0;
	private static boolean terminateProcessing = false;
	public static boolean solutionWasFound = false;
	public static GameSolution gameSolution = null;

	// We only need one. We will have many GameStates. Saves a bit of memory.
	public static MoveComparator moveComparator = new MoveComparator();

	/**
	 * Start a new game using a prepared deck for testing scenarios.
	 * 
	 * @param d
	 */
	public GameState(Deck d) {
		setNumericIds();

		this.deck = d;
		this.board = new Board(d);

		investigatedGameStates = new HashSet<GameState>();
	}

	/**
	 * Start a new game with a shuffled deck.
	 */
	public GameState() {
		setNumericIds();

		this.deck = new Deck();
		this.board = new Board(deck);

		investigatedGameStates = new HashSet<GameState>();
	}

	/**
	 * For debugging purposes. Allows injection of known game board.
	 * 
	 * @param b
	 */
	public GameState(Board b) {
		setNumericIds();

		this.board = b;

		investigatedGameStates = new HashSet<GameState>();
	}

	/**
	 * Creates a deep copy of the GameState. Will be used to advance a move to
	 * the next GameState
	 * 
	 * @param g
	 */
	public GameState(GameState g, Move m) {
		setNumericIds();

		this.board = (Board) DeepCopy.copy(g.board);
		this.parentState = g;
		this.moveAssociatedWithThisState = m;

		investigatedGameStates = g.investigatedGameStates;
	}

	public void setNumericIds() {
		totalGameStates++;
		thisGameStateNumber = totalGameStates;

	}

	public void performGameLogic() {
		// Should be unnecessary
		if (terminateProcessing) {
			// We have a solution. Stop looking.
			return;
		}

		if (isASolutionFound()) {
			// Should only be called once.
			System.out.println("Solution Found:");

			ArrayList<String> solution = new ArrayList<String>();

			solution.add(moveAssociatedWithThisState.toString());

			GameState parentHolder = parentState;

			while (parentHolder.moveAssociatedWithThisState != null) {

				solution.add(parentHolder.moveAssociatedWithThisState
						.toString());

				parentHolder = parentHolder.parentState;
			}

			Collections.reverse(solution);

			for (String string : solution) {
				System.out.println(string);
			}

			terminateProcessing = true;
			solutionWasFound = true;
			return;
		}

		if (investigatedGameStates.add(this)) {
			// This GameState has not been checked already

			drawState();// Debugging

			findValidMoves();

			printState();// Debugging

			Collections.sort(possibleMoves, moveComparator);

			for (Move m : possibleMoves) {
				currentlyInvestigatedMove++;

				m.associateWithState(currentlyInvestigatedMove, this);

				printInvestigatingNodeAndMove(m);// Debugging

				GameState g = m.execute(this);

				g.performGameLogic();

				if (terminateProcessing) {
					// We have a solution. Stop looking.
					return;
				}

				// Returned from investigating a branch. Display current state.
				// Debugging
				printReturnStatement();
				drawState();

			}

		} else {
			System.out.println("-----Non-unique GameState found.");
			return;
		}

		System.out.println(String.format(
				">>>Exhausted possible moves of GameState %d",
				this.thisGameStateNumber));
		// Once we've exhausted all moves from a state, the ArrayList is just
		// eating memory
		possibleMoves.clear();
	}

	/**
	 * Determines depth from initial state.
	 * 
	 * Initial state has no parent. All others do. By searching up the chain
	 * until we find a null we get the depth.
	 * 
	 * @return int Distance from Initial State
	 */

	private int determineStateDepth() {
		int i = 0;
		GameState parent = this.parentState;
		while (parent != null) {
			i++;
			parent = parent.parentState;
		}

		return i;
	}

	/**
	 * If the Play Positions and Draw Pile have been exhausted we have a
	 * solution
	 * 
	 * @return boolean
	 */
	private boolean isASolutionFound() {
		for (int i = 0; i < board.playPositions.length; i++) {
			if (board.playPositions[i].isEmpty() == false) {
				return false;
			}
		}
		// Demonstrates King problem
		// if (board.drawPile.hasAccessibleCard == true) {
		// return false;
		// }
		//
		// if (board.drawPile.canHit()) {
		// return false;
		// }

		if (board.drawPile.isEmpty() == false) {
			return false;
		}

		return true;

	}

	/**
	 * 
	 * Valid Moves: 1) We can move Card A onto Card B if card A is one rank
	 * lower and a different color. Example: Card A is a Jack of Diamonds, Card
	 * B is a Queen of Spades. 1a) If Card A has other cards on top of it we
	 * must move those cards as well.
	 * 
	 * 2) If the Scoring Position has a card that is ONE RANK LESS of the SAME
	 * SUIT Example: 2 of Clubs can go on the Ace of Clubs 2a) Aces can always
	 * move into Scoring Position
	 * 
	 * 3) Kings can go in any Play Position that has no cards.
	 * 
	 * 4) Can hit the draw pile (if we haven't gone through 3 times). Normal
	 * rules apply to the top card of the pile
	 * 
	 * 
	 */

	private void findValidMoves() {

		// PlayPosition Possibilities
		for (int playColumn = 0; playColumn < board.playPositions.length; playColumn++) {
			PlayPosition playPosition = board.playPositions[playColumn];
			// Can optimize later for weird cases where moves can expose
			// scorable cards. For now just checking top Card

			if (playPosition.isEmpty() == false) {
				// Can we score?
				addScoringMove(playColumn, playPosition.showTopCard(), false);

				List<Card> playPositionCards = playPosition
						.getListOfVisibleCards();

				// How about moving within the PlayPositions?
				for (int i = 0; i < playPositionCards.size(); i++) {
					addPlayPositionToPlayPositionMove(playColumn,
							playPositionCards.get(i));
				}
			}
		}

		// DrawPile Possibilities

		// Check drawPile top card for moves
		if (board.drawPile.hasAccessibleCard) {
			addScoringMove(DrawPile.drawPileColumnNumber,
					board.drawPile.peek(), true);

			addDrawPileToPlayPositionMove(board.drawPile.peek());
		}

		// There are times when using cards we've scored can help us get more
		// cards. Either by allowing us to expose play position piles or pull
		// off the drawpile.

		addScoringPositionToPlayPositionMove();

		if (board.drawPile.canHit()) {
			if (board.drawPile.makesSenseToHitDrawPile(board.playPositions,
					this.board)) {
				possibleMoves.add(new DrawPileHit());
			}
		}
	}

	// In order for this to happen a scoring card must be able to be placed
	// on the board. Also, another playPosition card, which if we could move
	// it would be a wise move (exposes a card), or we can place a drawpile
	// card.
	private void addScoringPositionToPlayPositionMove() {
		for (int scoringPositionIndex = 0; scoringPositionIndex < board.scoringPositions.length; scoringPositionIndex++) {
			// Get each scoring position's top card
			Card scoredCard = board.scoringPositions[scoringPositionIndex]
					.showTopCard();
			if (scoredCard == null) {
				continue;
			}
			for (int playPositionIndex = 0; playPositionIndex < board.playPositions.length; playPositionIndex++) {
				// Get each playPosition's top card
				PlayPosition playPosition = board.playPositions[playPositionIndex];

				if (playPosition.isEmpty()) {
					continue;
				}
				Card playPositionTopCard = playPosition.showTopCard();

				// Can we place the score card onto a Play Position?
				if (scoredCard.matchesPlayPosition(playPositionTopCard)) {
					// valid move, but does it make sense?
					doesItMakeSenseToMoveAScoredCard(scoredCard,
							playPositionIndex, playPositionTopCard);
				}

			}
		}
	}

//	@formatter:off
/**
 * There are 3 cases where this makes sense:
 * 1) If the scoredCard lets us remove a DrawPile card
 * 2) If the scoredCard lets us move a PlayPosition card that exposes another card or leaves space for a king
 * 3) The scoredCard is covering a card that fits 1 of the 2 above criteria. This could go deep... Not currently implemented.
 * // TODO We may need to pull a 5 of Hearts, to pull the 4 of hearts, to finally get 3 of clubs off the drawpile. Not currently thinking of that though
 *
 * @param scoredCard - Card currently in the scoring position, that we are considering moving back to the Play Position
 * @param playPositionIndex - Column we are considering moving scoredCard to
 * @param playPositionTopCard - ??? The card the scoredCard will be placed on??? 
 * 
 */
	//@formatter:on
	private void doesItMakeSenseToMoveAScoredCard(Card scoredCard,
			int playPositionIndex, Card playPositionTopCard) {

		boolean makesSense = false;
		Card drawPileTopCard = board.drawPile.peek();

		if (drawPileTopCard == null) {
			return;
		}

		if (drawPileTopCard.matchesPlayPosition(scoredCard)) {
			makesSense = true;
		} else {
			// Look at all the playPositions, except where the scoredCard is
			// being considered for movement
			for (int i = 0; i < board.playPositions.length; i++) {
				if (i == playPositionIndex) {
					continue;
				}
				List<Card> playPositionCards = board.playPositions[i]
						.getListOfVisibleCards();
				// If the scoredCard's movement allows a playPosition card to be
				// moved in a valid, "wise" way, we want to move the card
				for (Card card : playPositionCards) {
					if (card.matchesPlayPosition(scoredCard)) {
						if (isWiseMove(card, i)) {
							makesSense = true;
						}
					}
				}
			}
		}

		if (makesSense) {
			possibleMoves.add(new RepositioningMove(
					MoveOrigin.SCORING_POSITION, -1, scoredCard,
					playPositionIndex, playPositionTopCard));
		}
	}

	/**
	 * Takes a card at a particular position and determines if this card could
	 * be moved anywhere else on the board. Adds to possible moves if it can.
	 * 
	 * @param cardsOriginalColumn
	 * @param underConsideration
	 */
	private void addPlayPositionToPlayPositionMove(int cardsOriginalColumn,
			Card underConsideration) {

		int boardSize = board.playPositions.length;

		for (int i = 0; i < boardSize; i++) {
			if (i == cardsOriginalColumn) { // No point in checking card's own
											// column
				continue;
			}
			PlayPosition playPosition = board.playPositions[i];

			if (playPosition.isEmpty() == false) {
				// There is a card(s) in this position
				Card boardTopCard = playPosition.showTopCard();

				if (underConsideration.matchesPlayPosition(boardTopCard)) {

					if (isWiseMove(underConsideration, cardsOriginalColumn)) {
						possibleMoves.add(new RepositioningMove(
								MoveOrigin.PLAY_POSITION, cardsOriginalColumn,
								underConsideration, i, boardTopCard));
					}

				}
			} else { // The board position is empty. King can move there
				if (underConsideration.rank.equals(Rank.KING)) {
					if (isWiseMove(underConsideration, cardsOriginalColumn))
						possibleMoves.add(new RepositioningMove(
								MoveOrigin.PLAY_POSITION, cardsOriginalColumn,
								underConsideration, i, null));
				}
			}
		}

	}

//	@formatter:off
	/**
	 * Used for considering moves from the Play Position and Scoring Position to the Play Position 
	 * 
	 * Ok, so its a valid move. Is it a move that advances us toward our goal?
	 * 
	 * For cards in the Playing Field there are a few beneficial moves:
	 * 1) Reveal a card 
	 * 2) Shift a card so that the exposed can score
	 * 3) Open a spot up for a king to move 
	 * //TODO We miss out in a few rare cases. Imagine a board with 2 exposed 7s. One 7 
	 * has nothing below it. THe other has cards. There are no exposed kings. 
	 * Currently the move of the 7 with nothing under it is never taken
	 * 
	 * @param underConsideration
	 * @param cardsOriginalColumn
	 * @return boolean
	 */
//	@formatter:on

	private boolean isWiseMove(Card underConsideration, int cardsOriginalColumn) {
		Card parent = underConsideration.coversThisCard;

		if (parent == null) {
			if (underConsideration.rank.equals(Rank.KING)) {
				// No point in moving Kings around if its not exposing a
				// card.
				return false;
			} else {
				// If there is a King that can be moved from the drawpile to the
				// Play Position, that's a good thing
				Card tempCard = board.drawPile.peek();
				if (tempCard != null && tempCard.rank.equals(Rank.KING)) {
					return true;
				}

				for (int i = 0; i < board.playPositions.length; i++) {
					if (i == cardsOriginalColumn) {
						continue;
					}

					PlayPosition playPosition = board.playPositions[i];

					for (Card c : playPosition.getListOfVisibleCards()) {
						if (c.rank.equals(Rank.KING)) {
							if (c.coversThisCard != null) {
								// Also, if an exposed King has a Parent (card
								// it covers), that card must be flipped over.
								// So moving that King is a good thing
								return true;
							}
						}
					}
				}

			}
			return false;
		}
		// There's value in exposing cards and scoring them
		return (parent.isVisible == false || parent.canScore(board));
	}

	public void addDrawPileToPlayPositionMove(Card underConsideration) {

		int boardSize = board.playPositions.length;

		for (int i = 0; i < boardSize; i++) {

			if (board.playPositions[i].isEmpty() == false) {

				Card boardTopCard = board.playPositions[i].showTopCard();

				if (underConsideration.matchesPlayPosition(boardTopCard)) {
					possibleMoves.add(new RepositioningMove(
							MoveOrigin.DRAW_PILE,
							DrawPile.drawPileColumnNumber, underConsideration,
							i, boardTopCard));
				}
			} else {
				if (underConsideration.rank.equals(Rank.KING)) {
					possibleMoves.add(new RepositioningMove(
							MoveOrigin.DRAW_PILE,
							DrawPile.drawPileColumnNumber, underConsideration,
							i, null));
				}
			}
		}

	}

	/**
	 * If this card can score, we add that move to the possible moves.
	 * 
	 * @param cardsColumn
	 * @param card
	 * @param isDrawPile
	 *            boolean Is the card from the DrawPile?
	 * @param addIfFound
	 */
	public void addScoringMove(int cardsColumn, Card card, boolean isDrawPile) {

		if (card.canScore(board)) {
			if (isDrawPile) {
				possibleMoves.add(new ScoringMove(
						getSuitsScoringPositionIndex(card), card));
			} else {
				possibleMoves.add(new ScoringMove(MoveOrigin.PLAY_POSITION,
						cardsColumn, card, getSuitsScoringPositionIndex(card)));
			}
		}
	}

	/**
	 * 
	 * @param card
	 * @return Index associated with the Suit's scoring area
	 */

	private int getSuitsScoringPositionIndex(Card card) {

		for (int i = 0; i < board.scoringPositions.length; i++) {
			if (board.scoringPositions[i].suit.equals(card.suit)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.board == null) ? 0 : this.board.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameState other = (GameState) obj;
		if (this.board == null) {
			if (other.board != null)
				return false;
		} else if (!this.board.equals(other.board))
			return false;
		return true;
	}

	public static long getStateNumber() {
		return totalGameStates;
	}

	public static void isDrawSolutionOn() {
		GameState.drawSolutionOn = true;
	}

	public static boolean isConsoleOutputOn() {
		return consoleOutputOn;
	}

	public static void setConsoleOutputOn(boolean consoleOutputIsOn) {
		GameState.consoleOutputOn = consoleOutputIsOn;
	}

	public static void setDrawSolutionOn() {
		GameState.drawSolutionOn = true;
	}

	private void printReturnStatement() {
		System.out
				.println(String
						.format(">>>Returned to node %d. We've finished %d of %d potiential moves and are %d Levels deep",
								this.thisGameStateNumber,
								this.currentlyInvestigatedMove,
								this.possibleMoves.size(),
								determineStateDepth()));
	}

	private void printInvestigatingNodeAndMove(Move m) {
		System.out.println(String.format("Performing Node %d's Move %d of %d",
				thisGameStateNumber, currentlyInvestigatedMove,
				possibleMoves.size()));

		m.printMove();
	}

	private void printState() {
		System.out
				.println(String
						.format(">>>Now in node %d. Which has %d potiential moves and is %d Levels deep",
								this.thisGameStateNumber,
								this.possibleMoves.size(),
								determineStateDepth()));
	}

	private void drawState() {
		if (drawSolutionOn) {
			StatePrinter.getInstance().setGameState(this);
		}
	}

	/**
	 * Seperate runs should call this. WORK INTO CONSTRUCTOR???
	 */
	public static void resetStaticState() {
		totalGameStates = 0;
		solutionWasFound = false;
		terminateProcessing = false;
		gameSolution = null;
	}

}
