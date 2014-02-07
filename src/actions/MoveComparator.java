package actions;

import java.util.Comparator;

public class MoveComparator implements Comparator<Move> {

	@Override
	public int compare(Move o1, Move o2) {
		if (o1 instanceof ScoringMove) {
			return -1;
		}
		if (o1 instanceof DrawPileHit) {
			return 1;
		}
		return 0;
	}

}
