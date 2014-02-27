package actions;

import obj.Card;
import obj.GameState;

/**
 * Represents a possible move
 * 
 * @author User
 * 
 */
public abstract class Move implements Comparable<Move> {
	int cardIntitialPlayAreaColumn;
	Card cardBeingMoved;
	int destinationColumn;
	Card desiredParentCard;

	GameState parentState;
	int parentsMoveNum = -1;

	MoveOrigin moveOrigin;

	public Move() {

	}

	public Move(MoveOrigin moveOrigin, int cardIntitialPlayAreaColumn,
			Card cardBeingMoved, int destinationColumn, Card desiredParentCard) {
		super();
		this.cardIntitialPlayAreaColumn = cardIntitialPlayAreaColumn;

		this.moveOrigin = moveOrigin;

		this.cardBeingMoved = cardBeingMoved;
		this.destinationColumn = destinationColumn;
		this.desiredParentCard = desiredParentCard;
	}

	/**
	 * Convenience. Can help see how we got to this point.
	 * 
	 * @param moveNumber
	 * @param parentState
	 */
	public void associateWithState(int moveNumber, GameState parentState) {
		this.parentsMoveNum = moveNumber;
		this.parentState = parentState;

	}

	/**
	 * Performs the move.
	 * 
	 * @param initial
	 * @return New Distinct GameState. One move past the parameter passed in.
	 */
	public abstract GameState execute(GameState initial);

	public void printMove() {
		System.out.println(this.toString());
	}

	// Prioritize scoring. Make Drawing hits lowest priority
	@Override
	public int compareTo(Move o) {
		if (o instanceof ScoringMove) {
			return -1;
		}
		if (o instanceof DrawPileHit) {
			return 1;
		}
		return 0;
	}

}
