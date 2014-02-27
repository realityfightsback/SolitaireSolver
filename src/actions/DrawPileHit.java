package actions;

import obj.GameState;

public class DrawPileHit extends Move {

	public DrawPileHit() {
		super();
	}

	@Override
	public GameState execute(GameState initial) {
		GameState g = new GameState(initial, this);

		g.board.drawPile.flipThree();

		return g;
	}

	@Override
	public String toString() {
		return "***Hitting Draw Button***";
	}

	
	
}
