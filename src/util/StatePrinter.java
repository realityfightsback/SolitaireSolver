package util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.List;

import javax.swing.JFrame;

import obj.Board;
import obj.Card;
import obj.CardPosition;
import obj.GameState;

@SuppressWarnings("serial")
public class StatePrinter extends JFrame {

	private static StatePrinter theInstance = null;
	Image dbImage;
	Graphics dbg;
	Board board;
	int height = 500;
	int width = 500;
	private GameState gameState;

	public static StatePrinter getInstance() {
		if (theInstance == null) {
			theInstance = new StatePrinter();
		}
		return theInstance;
	}

	private StatePrinter() {
		setSize(height, width);
		setBackground(Color.WHITE);
		setTitle("Solitaire State");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void setGameState(GameState gameState) {
		this.board = gameState.board;
		this.gameState = gameState;
		repaint();
	}

	// Uses double buffering
	@Override
	public void paint(Graphics g) {
		if (board == null)
			return;
		// super.paint(g);

		dbImage = createImage(getWidth(), getHeight());
		dbg = dbImage.getGraphics();

		dbg.setColor(Color.RED);

		// Scoring positions
		dbg.drawRect(width - 150, 50, 20, 20);
		dbg.drawRect(width - 120, 50, 20, 20);
		dbg.drawRect(width - 90, 50, 20, 20);
		dbg.drawRect(width - 60, 50, 20, 20);

		drawScoringPositionValues();

		// Play positions
		int x = 50;
		for (int i = 0; i < board.playPositions.length; i++) {
			drawColumn(board.playPositions[i], x, 350);
			x += 60;

		}

		drawDrawPile();

		dbg.drawString("Game State Number " + gameState.thisGameStateNumber
				+ " Out of " + GameState.getStateNumber(), 220, 110);

		g.drawImage(dbImage, 0, 0, null);

	}

	private void drawDrawPile() {

		dbg.setColor(Color.RED);
		// Pile position outlines
		dbg.drawRect(width - 480, 50, 30, 30);

		dbg.drawRect(width - 420, 50, 20, 20);
		dbg.drawLine(width - 395, 30, width - 395, 90);
		dbg.drawRect(width - 390, 50, 20, 20);
		dbg.drawLine(width - 365, 30, width - 365, 90);
		dbg.drawRect(width - 360, 50, 20, 20);

		// Exposed Cards

		List<Card> c = board.drawPile.peek3();

		// List coming back is 0-3 cards. Accessible card is at the highest
		// index. Example 2,3,4.

		for (int i = 0; i < c.size(); i++) {
			Card t = c.get(i);
			dbg.drawString(t.toShortString(dbg), width - (420 - (30 * i)), 62);
		}

		String[] pileState = board.drawPile.printState();
		for (int i = 0; i < pileState.length; i++) {
			dbg.drawString(pileState[i], 10, 105 + (i * 15));
		}
	}

	private void drawScoringPositionValues() {
		conditionalScoringDraw(0, 150, 62);
		conditionalScoringDraw(1, 120, 62);
		conditionalScoringDraw(2, 90, 62);
		conditionalScoringDraw(3, 60, 62);

	}

	private void conditionalScoringDraw(int index, int widthOffset, int height) {
		if (board.scoringPositions[index] != null) {
			if (board.scoringPositions[index].isEmpty() == false) {

				Card c = board.scoringPositions[index].showTopCard();

				dbg.drawString(c.toShortString(dbg), width - widthOffset,
						height);
			}
		}
	}

	private void drawColumn(CardPosition position, int x, int y) {
		// We want the top most card displayed on the bottom of the printout.
		// Since we push onto the stack, we need to reverse it
		int j = position.size() - 1;

		while (j != -1) {
			Card c = position.get(j);

			setDrawColorBasedOnCard(c);

			if (c.isVisible) {
				dbg.drawString("  " + c.toShortString(dbg) + " ", x, y);
			} else {
				dbg.drawString("_" + c.toShortString(dbg) + "_", x, y);

				// dbg.drawString(iterator, x, y);
			}
			y -= 25;
			j--;
		}

	}

	private void setDrawColorBasedOnCard(Card c) {

	}

	static String fileName = "C:\\Users\\User\\Desktop\\deck3.srl";

	public static void main(String[] args) {

//		Board b = (Board) DeepCopy.readFromFile(fileName);

//		b.scoringPositions[0].addCardToPile(new Card(Suit.CLUBS, Rank.ACE));
		GameState g = new GameState();

		DeepCopy.writeToFile(g.board, fileName);
		
		StatePrinter.getInstance().setGameState(g);

		// for (int i = 0; i < 2; i++) {
		//
		// Deck d = new Deck();
		// Board b = new Board(d);
		//
		// StatePrinter sPrinter = StatePrinter.getInstance();
		// // sPrinter.setBoard(b);
		//
		// // b.scoringPositions[0].addCardToPile(new Card(Suit.CLUBS,
		// // Rank.ACE));
		// // sPrinter.repaint();
		// }
	}
}
