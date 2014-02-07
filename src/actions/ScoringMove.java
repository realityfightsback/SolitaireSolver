package actions;

import obj.Card;
import obj.GameState;
import obj.PlayPosition;

public class ScoringMove extends Move {

	public ScoringMove(int cardIntitialPlayAreaColumn, Card cardBeingMoved,
			int destinationColumn) {
		super(cardIntitialPlayAreaColumn, cardBeingMoved, destinationColumn,
				null);
	}

	/**
	 * DrawPile operations.
	 * 
	 * @param destinationColumn
	 */
	public ScoringMove(int destinationColumn, Card cardBeingMoved) {
		this.cardBeingMoved = cardBeingMoved;
		this.involvesDrawPile = true;
		this.destinationColumn = destinationColumn;
	}

	@Override
	public GameState execute(GameState initial) {
		// Scoring only happens with the top most card
		GameState g = new GameState(initial, this);

		Card c = null;
		if (involvesDrawPile) {
			c = g.board.drawPile.remove();
		} else {
			PlayPosition p = g.board.playPositions[cardIntitialPlayAreaColumn];
			c = p.removeCardFromPile();
		}

		g.board.scoringPositions[destinationColumn].addCardToPile(c);

		return g;
	}

	@Override
	public String toString() {
		return "Moving " + cardBeingMoved + " into scoring area";
	}

}
