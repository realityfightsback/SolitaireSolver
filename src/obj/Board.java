package obj;

import java.io.Serializable;
import java.util.Arrays;

public class Board implements Serializable {

	private static final long serialVersionUID = -1054515022627734998L;
	// Represents scoring positions at the top right of the board. Initially
	// empty
	public ScoringPosition[] scoringPositions = new ScoringPosition[4];
	// Mid board location where 1,2,3,4,5,6,7 cards (L-R) are placed.
	public PlayPosition[] playPositions = new PlayPosition[7];

	public DrawPile drawPile = new DrawPile();

	/**
	 * Creates the card stacks, scoring positions, and drawing pile
	 * 
	 * @param deck
	 */
	public Board(Deck deck) {
		scoringPositions[0] = new ScoringPosition(Suit.CLUBS);
		scoringPositions[1] = new ScoringPosition(Suit.SPADES);
		scoringPositions[2] = new ScoringPosition(Suit.DIAMONDS);
		scoringPositions[3] = new ScoringPosition(Suit.HEARTS);

		for (int i = 0; i < playPositions.length; i++) {
			playPositions[i] = new PlayPosition();
		}

		int indexForDrawingPile = populateBoard(deck);

		drawPile.initialize(indexForDrawingPile, deck);

	}

	/**
	 * Creates the 7 card stacks
	 * 
	 * @param deck
	 * @return Index into deck where the drawing pile should start
	 */
	private int populateBoard(Deck deck) {
		int cardsToPlace = 1;
		int deckPosition = 0;
		// For each temporaryPosition we want to push an increasing number of
		// cards, growing from Left to Right, by one
		// Left-most should have 1
		// Right-most should have 7
		for (int i = 0; i < playPositions.length; i++) {
			PlayPosition tempPos = playPositions[i];
			int j = 0;
			while (j < cardsToPlace) {
				Card tempCard = deck.getCard(deckPosition);

				deckPosition++;
				j++;

				if(tempCard == null){
				//Ability to just pass in a DrawPile is useful for debugging. No board cards
					continue;
				}
				if (j == cardsToPlace)
					tempCard.setVisible();// last iteration. Make this card face
											// up

				tempPos.addCardToPile(tempCard);
			}
			cardsToPlace++;

		}

		return deckPosition;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.drawPile == null) ? 0 : this.drawPile.hashCode());
		result = prime * result + Arrays.hashCode(this.playPositions);
		result = prime * result + Arrays.hashCode(this.scoringPositions);
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
		Board other = (Board) obj;
		if (this.drawPile == null) {
			if (other.drawPile != null)
				return false;
		} else if (!this.drawPile.equals(other.drawPile))
			return false;
		if (!Arrays.equals(this.playPositions, other.playPositions))
			return false;
		if (!Arrays.equals(this.scoringPositions, other.scoringPositions))
			return false;
		return true;
	}

}
