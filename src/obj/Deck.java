package obj;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Deck {
	public Card[] cards = new Card[52];

	public Deck() {
		generateDeck();
		shuffleDeck();
		// displayDeck();
	}

	/**
	 * Takes a file and de-serializes it File is a series of lines generated
	 * from the toString() method of Card. Also has place designators that lead
	 * with an _
	 * 
	 * ACE of SPADES _1Stack
	 * 
	 * @param fName
	 */
	public Deck(String fName) {
		List<String> lines = null;
		try {
			lines = FileUtils.readLines(new File(fName));
		} catch (IOException e) {
			e.printStackTrace();
		}

		int cardPosition = 0;
		for (String string : lines) {
			if (string.trim().equals("") == false) {
				if (string.contains("_") == false) {
					if (string.contains("DRAWPILE")) {
						cardPosition = 28;
						continue;
					}

					String[] rankSuit = string.split("of");

					addCard(new Card(Suit.valueOf(rankSuit[1].trim()),
							Rank.valueOf(rankSuit[0].trim())), cardPosition++);
				}

			}
		}
	}

	public void emptyDeck() {
		cards = new Card[52];
	}

	public void addCard(Card c, int index) {
		cards[index] = c;
	}

	public void shuffleDeck() {

		for (int i = 0; i < cards.length; i++) {

			int x = (int) Math.floor(Math.random() * 52);

			swap(i, x);

		}
	}

	private void swap(int currentIndex, int moveToThisIndex) {

		Card currCard = cards[currentIndex];

		cards[currentIndex] = cards[moveToThisIndex];

		cards[moveToThisIndex] = currCard;

	}

	public void generateDeck() {
		emptyDeck();

		int cardPosition = 0;
		for (Suit suit : Suit.values()) {
			for (Rank rank : Rank.values()) {
				addCard(new Card(suit, rank), cardPosition);
				cardPosition++;
			}
		}
	}

	public void displayDeck() {
		for (int i = 0; i < cards.length; i++) {
			System.out.println(cards[i]);
		}
	}

	public Card getCard(int index) {
		return cards[index];
	}

}
