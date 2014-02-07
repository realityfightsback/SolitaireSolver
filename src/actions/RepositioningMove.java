package actions;

import java.util.ArrayList;
import java.util.Collections;

import obj.Card;
import obj.GameState;
import obj.PlayPosition;

public class RepositioningMove extends Move {
	/**
	 * 
	 * @param cardIntitialPlayAreaColumn
	 * @param cardBeingMoved
	 * @param destinationColumn
	 * @param desiredParentCard
	 *            Can be NULL in cases
	 */
	public RepositioningMove(int cardIntitialPlayAreaColumn,
			Card cardBeingMoved, int destinationColumn, Card desiredParentCard) {
		super(cardIntitialPlayAreaColumn, cardBeingMoved, destinationColumn,
				desiredParentCard);
	}

	@Override
	public GameState execute(GameState initial) {
		GameState g = new GameState(initial, this);

		// Other cases can involve moving many cards
		// Pop them from destination, store temporarily, push to new
		// location.

		ArrayList<Card> temp = new ArrayList<Card>();
		// DrawPile to Play Area Case
		if (cardIntitialPlayAreaColumn == -1) {
			Card c = g.board.drawPile.remove();
			c.isVisible = true;
			g.board.playPositions[destinationColumn].addCardToPile(c);
		} else {// Play Area to Play Area Case
			PlayPosition p = g.board.playPositions[cardIntitialPlayAreaColumn];
			Card c = null;
			do {
				c = p.removeCardFromPile();

				temp.add(c);
			} while (!c.equals(cardBeingMoved));
			// Since we popped off the stack the lowest card is first, want to
			// put on new stack highest card first. So flip it.
			Collections.reverse(temp);

			for (Card z : temp) {
				g.board.playPositions[destinationColumn].addCardToPile(z);
			}
		}
		return g;
	}

}