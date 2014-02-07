package obj;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

public class Card implements Serializable {
	public Suit suit;
	public Rank rank;
	// Has it been flipped over?
	public boolean isVisible = false;

	// No other cards are below it. Lowest card. Closest to the board
	public boolean isBaseCard = false;

	public Card coversThisCard = null;

	public Card(Suit suit, Rank rank) {
		super();
		this.suit = suit;
		this.rank = rank;
	}

	/**
	 * Creates a deep copy
	 * 
	 * @param c
	 */
	public Card(Card c) {
		super();
		this.suit = c.suit;
		this.rank = c.rank;

	}

	@Override
	public String toString() {
		return rank + " of " + suit;
	}

	/**
	 * For graphical printing of Card
	 * 
	 * @param dbg
	 *            Sets coloring
	 * @return Friendly readable Card String
	 */
	public String toShortString(Graphics dbg) {
		dbg.setColor(cardColor());

		return mapRank(rank) + mapSuit(suit);
	}

	private String mapRank(Rank rank) {
		switch (rank) {
		case TWO:
			return "2";
		case THREE:
			return "3";
		case FOUR:
			return "4";
		case FIVE:
			return "5";
		case SIX:
			return "6";
		case SEVEN:
			return "7";
		case EIGHT:
			return "8";
		case NINE:
			return "9";
		case TEN:
			return "10";
		case JACK:
			return "J";
		case QUEEN:
			return "Q";
		case KING:
			return "K";
		case ACE:
			return "A";
		default:
			return "X";
		}
	}

	private String mapSuit(Suit suit) {
		switch (suit) {
		case CLUBS:
			return "\u2663";
			// return "C";
		case SPADES:
			return "\u2660";
			// return "S";
		case HEARTS:
			return "\u2665";
			// return "H";
		case DIAMONDS:
			return "\u2666";
			// return "D";
		default:
			return "X";
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.isVisible ? 1231 : 1237);
		result = prime * result
				+ ((this.rank == null) ? 0 : this.rank.hashCode());
		result = prime * result
				+ ((this.suit == null) ? 0 : this.suit.hashCode());
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
		Card other = (Card) obj;
		if (this.isVisible != other.isVisible)
			return false;
		if (this.rank != other.rank)
			return false;
		if (this.suit != other.suit)
			return false;
		return true;
	}

	public Color cardColor() {
		if (suit.equals(Suit.CLUBS) || suit.equals(Suit.SPADES))
			return Color.BLACK;
		else
			return Color.RED;

	}

	/**
	 * The card has been flipped over and will examined when finding moves.
	 */
	public void setVisible() {
		isVisible = true;
	}

	/**
	 * If THIS Card is a different color and a Rank less than the card t under
	 * comparison, its a valid move
	 * 
	 * @param boardTopCard
	 * @return boolean
	 */
	public boolean matchesPlayPosition(Card boardTopCard) {
		if (this.cardColor() != boardTopCard.cardColor()) {
			// return mySwitch(this, boardTopCard);

			switch (this.rank) {
			case KING:
				return false;
			case QUEEN:
				if (boardTopCard.rank.equals(Rank.KING))
					return true;
				break;
			case JACK:
				if (boardTopCard.rank.equals(Rank.QUEEN))
					return true;
				break;
			case TEN:
				if (boardTopCard.rank.equals(Rank.JACK))
					return true;
				break;
			case NINE:
				if (boardTopCard.rank.equals(Rank.TEN))
					return true;
				break;
			case EIGHT:
				if (boardTopCard.rank.equals(Rank.NINE))
					return true;
				break;
			case SEVEN:
				if (boardTopCard.rank.equals(Rank.EIGHT))
					return true;
				break;
			case SIX:
				if (boardTopCard.rank.equals(Rank.SEVEN))
					return true;
				break;
			case FIVE:
				if (boardTopCard.rank.equals(Rank.SIX))
					return true;
				break;
			case FOUR:
				if (boardTopCard.rank.equals(Rank.FIVE))
					return true;
				break;
			case THREE:
				if (boardTopCard.rank.equals(Rank.FOUR))
					return true;
				break;
			case TWO:
				if (boardTopCard.rank.equals(Rank.THREE))
					return true;
				break;
			default:
				return false;
			}
		}
		return false;

	}

	/**
	 * Is lesserCard's rank is less than greaterCard's
	 * 
	 * Note has issue with king (also Ace). Only handles one King and Ace case
	 * 
	 * @param lesserCard
	 * @param greaterCard
	 * @return boolean private boolean mySwitch(Card lesserCard, Card
	 *         greaterCard) { switch (lesserCard.rank) { case KING: return
	 *         false; case QUEEN: if (greaterCard.rank.equals(Rank.KING)) return
	 *         true; break; case JACK: if (greaterCard.rank.equals(Rank.QUEEN))
	 *         return true; break; case TEN: if
	 *         (greaterCard.rank.equals(Rank.JACK)) return true; break; case
	 *         NINE: if (greaterCard.rank.equals(Rank.TEN)) return true; break;
	 *         case EIGHT: if (greaterCard.rank.equals(Rank.NINE)) return true;
	 *         break; case SEVEN: if (greaterCard.rank.equals(Rank.EIGHT))
	 *         return true; break; case SIX: if
	 *         (greaterCard.rank.equals(Rank.SEVEN)) return true; break; case
	 *         FIVE: if (greaterCard.rank.equals(Rank.SIX)) return true; break;
	 *         case FOUR: if (greaterCard.rank.equals(Rank.FIVE)) return true;
	 *         break; case THREE: if (greaterCard.rank.equals(Rank.FOUR)) return
	 *         true; break; case TWO: if (greaterCard.rank.equals(Rank.THREE))
	 *         return true; break; case ACE: if
	 *         (greaterCard.rank.equals(Rank.TWO)) return true; break; } return
	 *         false;
	 * 
	 *         }
	 */

	/**
	 * Compares THIS to parameter. Returns true if THIS is a rank greater than
	 * the parameter.
	 * 
	 * @param cardInScoringPosition
	 *            Top Card from the Suit-appropriate Scoring Position (may be
	 *            NULL)
	 * @return
	 */
	public boolean matchesScoringPosition(Card cardInScoringPosition) {
		if (cardInScoringPosition == null) {
			if (this.rank.equals(Rank.ACE))
				return true;
			else
				return false;
		}

		switch (this.rank) {
		case KING:
			if (cardInScoringPosition.rank.equals(Rank.QUEEN))
				return true;
			break;
		case QUEEN:
			if (cardInScoringPosition.rank.equals(Rank.JACK))
				return true;
			break;
		case JACK:
			if (cardInScoringPosition.rank.equals(Rank.TEN))
				return true;
			break;
		case TEN:
			if (cardInScoringPosition.rank.equals(Rank.NINE))
				return true;
			break;
		case NINE:
			if (cardInScoringPosition.rank.equals(Rank.EIGHT))
				return true;
			break;
		case EIGHT:
			if (cardInScoringPosition.rank.equals(Rank.SEVEN))
				return true;
			break;
		case SEVEN:
			if (cardInScoringPosition.rank.equals(Rank.SIX))
				return true;
			break;
		case SIX:
			if (cardInScoringPosition.rank.equals(Rank.FIVE))
				return true;
			break;
		case FIVE:
			if (cardInScoringPosition.rank.equals(Rank.FOUR))
				return true;
			break;
		case FOUR:
			if (cardInScoringPosition.rank.equals(Rank.THREE))
				return true;
			break;
		case THREE:
			if (cardInScoringPosition.rank.equals(Rank.TWO))
				return true;
			break;
		case TWO:
			if (cardInScoringPosition.rank.equals(Rank.ACE))
				return true;
			break;
		default:
			return false;
		}
		return false;

		// return mySwitch(cardInScoringPosition, this);
	}

	/**
	 * Provides a test of whether a given card can score.
	 * 
	 * @return boolean
	 */
	public boolean canScore(Board board) {
		ScoringPosition scoringPosition = getSuitsScoringPosition(board);

		return this.matchesScoringPosition(scoringPosition.showTopCard());
	}

	/**
	 * Gets ScoringPosition that matches passed in Card (checks the suit)
	 * 
	 * @param card
	 * @return ScoringPosition
	 */

	private ScoringPosition getSuitsScoringPosition(Board board) {

		for (int i = 0; i < board.scoringPositions.length; i++) {
			if (board.scoringPositions[i].suit.equals(this.suit)) {
				return board.scoringPositions[i];
			}
		}
		return null;
	}

	public boolean isVisible() {
		return this.isVisible;
	}

	/**
	 * Cards never lose visibility
	 * 
	 * @param isVisible
	 */
	public void setVisible(boolean isVisible) {
		if (this.isVisible) {
			return;
		}
		this.isVisible = isVisible;
	}

}
