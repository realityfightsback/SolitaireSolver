package actions;

import obj.Card;
import obj.GameState;
import obj.PlayPosition;

public class ScoringMove extends Move {

	public ScoringMove(MoveOrigin moveOrigin, int cardIntitialPlayAreaColumn,
			Card cardBeingMoved, int destinationColumn) {
		super(moveOrigin, cardIntitialPlayAreaColumn, cardBeingMoved,
				destinationColumn, null);
	}

	/**
	 * DrawPile operations.
	 * 
	 * @param destinationColumn
	 */
	public ScoringMove(int destinationColumn, Card cardBeingMoved) {
		this.cardBeingMoved = cardBeingMoved;
		this.moveOrigin = MoveOrigin.DRAW_PILE;
		this.destinationColumn = destinationColumn;
	}

	@Override
	public GameState execute(GameState initial) {
		// Scoring only happens with the top most card of the drawpile or
		// playPosition
		GameState g = new GameState(initial, this);

		Card c = null;
		switch (moveOrigin) {
		case DRAW_PILE:
			c = g.board.drawPile.remove();
			break;

		case PLAY_POSITION:
			PlayPosition p = g.board.playPositions[cardIntitialPlayAreaColumn];
			c = p.removeCardFromPile();
			break;

		case SCORING_POSITION:
			throw new RuntimeException(
					"Non-sensical to have a scoring move from a scoring position");
		}

		g.board.scoringPositions[destinationColumn].addCardToPile(c);

		return g;
	}

	@Override
	public String toString() {
		switch (moveOrigin) {
		case DRAW_PILE:
			return "+++ Scoring DrawPile{" + cardBeingMoved + "}";
		case PLAY_POSITION:
			return "+++ Scoring PlayPosition{" + cardBeingMoved + "}";
		default:
			return "Nonsensical Move";
		}
	}

}
