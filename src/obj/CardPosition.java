package obj;

import java.io.Serializable;
import java.util.List;
import java.util.Stack;

public class CardPosition implements Serializable {
	private Stack<Card> cardStack = new Stack<Card>();

	public void addCardToPile(Card tempCard) {
		if (cardStack.isEmpty()) {
			tempCard.isBaseCard = true;
			tempCard.coversThisCard = null;
		} else {
			tempCard.coversThisCard = showTopCard();
		}

		cardStack.push(tempCard);
	}

	/**
	 * Should only be called after a check of isEmpty()
	 * 
	 * @return
	 */
	public Card showTopCard() {
		return cardStack.get(cardStack.size() - 1);
	}

	/**
	 * Retrieves the top card. If it reveals a new card, it makes that card
	 * visible.
	 * 
	 * @return
	 */
	public Card removeCardFromPile() {
		Card c = cardStack.pop();

		c.coversThisCard = null;

		if (size() > 0) {
			showTopCard().isVisible = true;
		} else {
			// May end up on the bottom, but that's for push to figure out
			c.isBaseCard = false;
		}
		return c;
	}

	/**
	 * Provides arbitrary access to Card Stack elements
	 * 
	 * @param index
	 * @return Card
	 */
	public Card get(int index) {
		return cardStack.get(index);
	}

	public int size() {
		return cardStack.size();
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * For debugging purposes only. Prefer not to use
	 * 
	 * @return
	 */
	public Stack<Card> getCardStack() {

		return cardStack;
	}

	public List<Card> getListOfCards() {
		return cardStack.subList(0, size());
	}
	
	
	public List<Card> getListOfVisibleCards() {
		return cardStack.subList(determineIndexOfVisibility(), size());
	}
	
	// Stack 0 index is a single card on the table. Grows "down" as viewed by
	// player. Meaning 1 Index is on top of 0

	private int determineIndexOfVisibility() {
		int lastVisibleCard = 0;
		for (Card z : cardStack) {
			if (z.isVisible == false) {
				lastVisibleCard++;
			}
		}

		return lastVisibleCard;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.cardStack == null) ? 0 : this.cardStack.hashCode());
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
		CardPosition other = (CardPosition) obj;
		if (this.cardStack == null) {
			if (other.cardStack != null)
				return false;
		} else if (!this.cardStack.equals(other.cardStack))
			return false;
		return true;
	}

}
