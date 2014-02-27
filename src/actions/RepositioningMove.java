package actions;

import java.util.ArrayList;
import java.util.Collections;

import obj.Card;
import obj.GameState;
import obj.PlayPosition;
import obj.ScoringPosition;

public class RepositioningMove extends Move {

	/**
	 * 
	 * @param moveOrigin
	 * @param cardIntitialPlayAreaColumn
	 *            - Only applies to PlayPostion originating moves. Makes no
	 *            sense for DrawPile or ScoringPosition, which pass in -1
	 * @param cardBeingMoved
	 * @param destinationColumn
	 * @param desiredParentCard
	 *            - Can be NULL in cases
	 */
	public RepositioningMove(MoveOrigin moveOrigin,
			int cardIntitialPlayAreaColumn, Card cardBeingMoved,
			int destinationColumn, Card desiredParentCard) {
		super(moveOrigin, cardIntitialPlayAreaColumn, cardBeingMoved,
				destinationColumn, desiredParentCard);
	}

	@Override
	public GameState execute(GameState initial) {
		GameState g = new GameState(initial, this);

		ArrayList<Card> temp = new ArrayList<Card>();
		Card c = null;

		switch (moveOrigin) {
		case DRAW_PILE:
			c = g.board.drawPile.remove();
			c.isVisible = true;
			g.board.playPositions[destinationColumn].addCardToPile(c);
			break;
		case PLAY_POSITION:
			PlayPosition p = g.board.playPositions[cardIntitialPlayAreaColumn];

			// May can involve moving many cards
			// Pop them from source, store temporarily, push to new
			// destination.
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
			break;
		case SCORING_POSITION:

			ScoringPosition scoringPosition = cardBeingMoved
					.getSuitsScoringPosition(g.board);

			c = scoringPosition.showTopCard();
			if (c == null || !(c.equals(cardBeingMoved))) {
				throw new RuntimeException(
						"Major issue. Scoring position removal is off.");
			}

			c = scoringPosition.removeCardFromPile();

			g.board.playPositions[destinationColumn].addCardToPile(c);

			break;
		default:
			break;
		}

		return g;
	}

	@Override
	public String toString() {
		String desiredParentName = "empty";
		if (desiredParentCard != null) {
			desiredParentName = desiredParentCard.toString();
		}
		switch (moveOrigin) {
		case DRAW_PILE:
			return "Moving DrawPile {" + cardBeingMoved
					+ "} to PlayPosition : Column " + destinationColumn + " { "
					+ desiredParentName + "}";
		case PLAY_POSITION:
			return "Moving PlayPosition : Column " + cardIntitialPlayAreaColumn
					+ " {" + cardBeingMoved + "} to PlayPosition : Column "
					+ destinationColumn + " {" + desiredParentName + "}";
		case SCORING_POSITION:
			return "Moving ScoringPosition {" + cardBeingMoved
					+ "} to PlayPosition : Column " + destinationColumn + " {"
					+ desiredParentName + "}";
		default:
			return "";
		}

	}
}