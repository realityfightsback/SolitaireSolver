package obj;

import java.io.Serializable;

public class ScoringPosition extends CardPosition implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8266211869013884840L;
	Suit suit;

	public ScoringPosition(Suit s) {
		super();
		this.suit = s;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((this.suit == null) ? 0 : this.suit.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScoringPosition other = (ScoringPosition) obj;
		if (this.suit != other.suit)
			return false;
		return true;
	}

	/**
	 * Returns null rather than throwing IndexOutOfBounds. Null signifies an
	 * empty spot. Maybe I should be doing this for all of them...
	 */
	@Override
	public Card showTopCard() {
		if (size() == 0) {
			return null;
		}
		return super.showTopCard();
	}

}
