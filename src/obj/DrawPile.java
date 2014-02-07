package obj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

/**
 * Draw Pile representation.
 * 
 * [2,3,4,5,6,7,8,9,10] ^ Moves through the Stack by multiples of 3. Only one
 * card accessible at a time. '
 * 
 * Starting the above array we would have 4 accessible. We can remove it or peek
 * at it. Removing puts a null entry into the Stack. This is because we do not
 * want to shift all the elements yet. We increment the subIndex. Now 3 is
 * accessible. Removing that 2. Finally, we have no accessible card (indicated
 * by boolean).
 * 
 * "Flip 3" increments drawPileHitCount. Additionally it resets the subIndex.
 * Now 7 is accessible.
 * 
 * When we Flip 3 enough to cover the whole Stack we remove all the Null
 * placeholders and the Stack shifts elements. We begin again at beginning. We
 * can go completely though 3 times before Flip 3 is unavailable.
 * 
 * @author User
 * 
 */
public class DrawPile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3899801440954720206L;
	private Stack<Card> drawingPile = new Stack<Card>();
	private DrawPileProperties drawPileProps = new DrawPileProperties(
			drawingPile);

	public boolean hasAccessibleCard = true;

	public static int drawPileColumnNumber = -1;

	// Related to looking through the pile to determine whether we can get moves
	// from hitting the drawpile. Trades off CPU cycles for Memory consumption
	// (fewer states saved in HashMap)

	private DrawPileProperties lookAheadProps = new DrawPileProperties(null);

	// Do we have a shift coming?
	public boolean cardRemovedOnThisLoop = false;

	public DrawPile() {
		drawPileProps.initialize();
	}

	/**
	 * Sets up the DrawingPile
	 * 
	 * @param indexForDrawingPile
	 *            28, the way we are instantiating decks right now
	 * @param deck
	 */

	public void initialize(int indexForDrawingPile, Deck deck) {
		for (int i = indexForDrawingPile; i < deck.cards.length; i++) {
			drawingPile.push(deck.cards[i]);
		}

	}

	/**
	 * Performs the move equivalent to clicking the pile
	 */

	// TODO not sure this leaves the cards accessible once you've hit the Pile
	// as many times as you can.
	public void flipThree(DrawPileProperties props) {
		if (props.canHit == false) {
			throw new RuntimeException("NO MORE FLIPS LEFT!!!");
		}

		int availableCards = drawingPile.size();

		int remainingCards = availableCards - (3 * props.index);

		if (remainingCards > 0) {
			props.index++;

			switch (remainingCards % 3) {
			case 0:
				props.subIndex = 0;
				break;
			case 1:
				props.subIndex = 2;
				break;
			case 2:
				props.subIndex = 1;
				break;
			default:
				break;
			}
		} else {// Made it through all existing cards
			if ((props.hitCount + 1) == 4) {
				// No more loops through cards.
				// Indices stay where they are
				props.canHit = false;
			} else {
				// Remove null values from stack (shifting cards now)
				props.hitCount++;
				props.index = 1;
				props.subIndex = 0;

				cardRemovedOnThisLoop = false;

				removeNullsShiftStack(props.pileAssociatedWith);

			}

		}
		// Flips come after we've exhausted all possiblities. Can draw all
		// available Cards. Must recheck boolean.
		Card nextCard = peek(props);

		if (nextCard != null) {
			// No more cards
			hasAccessibleCard = true;
		}
	}

	/**
	 * Assumes you want to flip the main (non LookAhead) state 3 cards.
	 */
	public void flipThree() {
		flipThree(drawPileProps);
	}

	/**
	 * Once we've hit the end of an iteration through the pile, if we removed
	 * any cards we now shift the remaining cards to fill that spot
	 * 
	 * @param dPile
	 */

	private void removeNullsShiftStack(Stack<Card> dPile) {
		for (int i = 0; i < dPile.size(); i++) {
			Card z = dPile.get(i);
			if (z == null) {
				dPile.remove(i);
				i--;// When we remove the stack shifts. Will overlook
					// back to back nulls without this.
			}

		}
	}

	/**
	 * Displays top 3 (or less) cards depending on pile availability. Accessible
	 * card is at highest Index.
	 * 
	 * Never looks at LOOK AHEAD
	 * 
	 * @return Card List
	 */
	public List<Card> peek3() {

		int tempIndex = (drawPileProps.index * 3) - drawPileProps.subIndex - 1;

		ArrayList<Card> list = new ArrayList<Card>();
		// Look for available cards until we find 3 or run out of stack
		while (tempIndex != -1 && (list.size() != 3)) {
			Card z = drawPileProps.pileAssociatedWith.get(tempIndex);

			if (z != null) {// Card removed but not cleaned up yet( on next
							// iteration through we shift)
				list.add(z);
			}

			tempIndex--;
		}
		Collections.reverse(list);

		return list;
	}

	/**
	 * Removes the Card from the DrawPile data structure. Null out the drawPile
	 * entry. Increment the subIndex and hasAccessibleCard boolean as necessary.
	 * 
	 * Should ONLY be called if hasAccessibleCard is TRUE
	 * 
	 * @return
	 */
	public Card remove() {
		// Find non-null entry

		Card currentCard = peek(drawPileProps);

		drawingPile.remove(computeIndex(drawPileProps));
		drawingPile.add(computeIndex(drawPileProps), null);

		cardRemovedOnThisLoop = true;

		// Find if there is another item following the removal.
		Card nextCard = peek(drawPileProps);

		if (nextCard == null) {
			// No more cards
			hasAccessibleCard = false;
		}

		return currentCard;
	}

	/**
	 * Is this meant to
	 * 
	 * @param isFutureLookAhead
	 * @return
	 */

	private int computeIndex(DrawPileProperties props) {

		return (props.index * 3) - props.subIndex - 1;
	}

	/**
	 * Does not remove. Increments subIndex until you run out of Stack or find
	 * an element.
	 * 
	 * @return
	 */
	public Card peek(DrawPileProperties props) {
		Card z = null;

		for (int i = computeIndex(props); i >= 0 && z == null; i = computeIndex(props)) {
			z = drawingPile.get(i);

			if (z == null) {
				props.subIndex++;
			}
		}

		return z;
	}

	/**
	 * Assumes you want the main (not lookAhead) top card
	 * 
	 * @return
	 */
	public Card peek() {
		return peek(drawPileProps);
	}

	public int size() {
		return drawingPile.size();
	}

	/**
	 * Do we still have non-null entries in DrawingPile?
	 * 
	 * @return boolean
	 */
	public boolean isEmpty() {
		for (Card c : drawingPile) {
			if (c != null) {
				return false;
			}
		}

		return true;
	}

	public String[] printState() {
		String s = String
				.format("Loops Through: %d \n Hit Index: %d \n Sub-Index: %d \n Can Draw: %b \n Has Accessible Card: %b",
						drawPileProps.hitCount, drawPileProps.index,
						drawPileProps.subIndex, drawPileProps.canHit,
						hasAccessibleCard);
		String[] sP = s.split("\n");
		return sP;
	}

	/**
	 * Helper for determining whether DrawPile operations will be beneficial
	 * 
	 * @return Set of all possible top DrawPile Cards
	 */

	public HashSet<Card> getTopCardsLeft() {
		HashSet<Card> topCards = new HashSet<Card>();

		lookAheadProps.copyState(drawPileProps);

		// Don't want to alter the Stack state if we prune Nulls
		Stack<Card> tempDrawingPile = new Stack<Card>();
		tempDrawingPile.addAll(drawingPile);
		lookAheadProps.pileAssociatedWith = tempDrawingPile;

		// Can be optimized to not go through the pile multiple times. Maybe do
		// later
		while (lookAheadProps.canHit && lookAheadProps.hitCount < 4) {
			flipThree(lookAheadProps);
			Card tempCard = peek(lookAheadProps);
			if (tempCard != null) {
				topCards.add(tempCard);
			} else {
				// Doesn't seem to be the worst thing in the world. But we don't
				// seem to loop...
				System.out.println("GOT A NULL CARD... HOWS THAT HAPPENING?");
			}
		}
		// Don't want to keep this around in Hash Set stored state. Memory waste
		lookAheadProps.pileAssociatedWith = null;

		return topCards;

	}

	/**
	 * Prior to going down a DrawPileHit path (which could conceivably just be
	 * many drawPileHits) we want to see if any of the available topCards would
	 * benefit us.
	 * 
	 * (OPTIMIZATION) If one would go ahead. If not was a card removed from the
	 * drawPile earlier in this loop? If so there will be a shift on the next
	 * loop. So pursue this. Check if this is the last loop. If so don't pursue
	 * 
	 * @param gameState
	 * 
	 * @return boolean
	 */
	public boolean makesSenseToHitDrawPile(PlayPosition[] playPositions,
			Board board) {

		HashSet<Card> drawPileTopCards = getTopCardsLeft();

		for (Card drawPileCard : drawPileTopCards) {
			// Go through all available cards from the Draw Pile
			for (int i = 0; i < playPositions.length; i++) {

				PlayPosition playPosition = playPositions[i];
				if (playPosition.isEmpty() == false) {
					Card playPosCard = playPosition.showTopCard();
					// Can the card be placed on another card on the board?
					if (drawPileCard.matchesPlayPosition(playPosCard)) {
						return true;
					}

				} else {
					// Ok, its an open space. Do we have a King?
					if (drawPileCard.rank.equals(Rank.KING)) {
						return true;
					}
				}
			}
			// Can this card be scored?
			if (drawPileCard.canScore(board)) {
				return true;
			}
		}
		// If none of the available cards can score OR go onto the board, no
		// point in hitting the DrawPile
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.drawPileProps.canHit ? 1231 : 1237);
		result = prime * result + this.drawPileProps.hitCount;
		result = prime * result + this.drawPileProps.index;
		result = prime * result + this.drawPileProps.subIndex;
		result = prime
				* result
				+ ((this.drawingPile == null) ? 0 : this.drawingPile.hashCode());
		result = prime * result + (this.hasAccessibleCard ? 1231 : 1237);
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
		DrawPile other = (DrawPile) obj;
		if (this.drawPileProps.canHit != other.drawPileProps.canHit)
			return false;
		if (this.drawPileProps.hitCount != other.drawPileProps.hitCount)
			return false;
		if (this.drawPileProps.index != other.drawPileProps.index)
			return false;
		if (this.drawPileProps.subIndex != other.drawPileProps.subIndex)
			return false;
		if (this.drawingPile == null) {
			if (other.drawingPile != null)
				return false;
		} else if (!this.drawingPile.equals(other.drawingPile))
			return false;
		if (this.hasAccessibleCard != other.hasAccessibleCard)
			return false;
		return true;
	}

	/**
	 * Is the main (non LookAhead) state hittable?
	 * 
	 * @return boolean
	 */
	public boolean canHit() {
		return drawPileProps.canHit;
	}

	/**
	 * Set the the main (non LookAhead) state hittable variable. Useful for
	 * debugging.
	 * 
	 * @param b
	 */
	public void setCanHit(boolean b) {
		drawPileProps.canHit = b;
	}

}
