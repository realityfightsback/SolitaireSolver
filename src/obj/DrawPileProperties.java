package obj;

import java.io.Serializable;
import java.util.Stack;

/**
 * Holds the state associated with a given DrawPile. We use one for the current
 * state, another for the future LookAhead state (to make more intelligent
 * actions)
 * 
 * @author User
 * 
 */
public class DrawPileProperties implements Serializable {

	private static final long serialVersionUID = 1L;
	public int hitCount = -1;
	public int index = -1;
	public int subIndex = -1;

	// Eventually we are not allowed to draw anymore (3 times through)
	public boolean canHit = false;

	public Stack<Card> pileAssociatedWith;

	/**
	 * Takes null if look ahead
	 * 
	 * @param drawingPile
	 */
	public DrawPileProperties(Stack<Card> drawingPile) {

		pileAssociatedWith = drawingPile;
	}

	public void initialize() {
		hitCount = 1;
		index = 1;
		subIndex = 0;
		canHit = true;
	}

	public void copyState(DrawPileProperties props) {
		this.canHit = props.canHit;
		this.hitCount = props.hitCount;
		this.index = props.index;
		this.subIndex = props.subIndex;

	}
}
