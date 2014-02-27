package main;

import obj.Board;
import obj.Deck;
import obj.GameState;
import util.DeepCopy;
import util.StatePrinter;

public class GameInitializer {
	static String fileName = "C:\\Users\\User\\Desktop\\deck1.srl";

	public static void main(String[] args) {

		Board b = null;
		GameState g = null;

		boolean writeState = true;

		if (writeState) {
			g = new GameState();
			DeepCopy.writeToFile(g.board, fileName);
		} else {
			b = (Board) DeepCopy.readFromFile(fileName);

			// b = new Board(new
			// Deck("C:\\Users\\User\\Desktop\\Dev\\workspaces\\JEE\\SolitaireSolver\\NoMoves"));
			
			g = new GameState(b);
		}
		GameState.setDrawSolutionOn();

		StatePrinter.getInstance().setGameState(g);

		g.performGameLogic();
		
		if(GameState.solutionWasFound == false){
			System.out.println("No apparent solution");
		}
	}

}
