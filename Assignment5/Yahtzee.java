/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 */

import acm.io.*;
import acm.program.*;
import acm.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {
	
	public static void main(String[] args) {
		new Yahtzee().start(args);
	}
	
	public void run() {
		while (true) {
			IODialog dialog = getDialog();
			nPlayers = dialog.readInt("Enter number of players");
			playerNames = new String[nPlayers];
			for (int i = 1; i <= nPlayers; i++) {
				playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
			}
			display = new YahtzeeDisplay(getGCanvas(), playerNames);
			playGame();
			showGameOver();
			if (!playAgain()) break;
		}
	}

	private void showGameOver() {
		// TODO Auto-generated method stub
		
	}
	
	private boolean playAgain() {
		// TODO Auto-generated method stub
		return false;
	}

	private void playGame() {
		initScoreCard();
		/* Use the number of scoring categories to determine how many rounds the game should go */
		for (int round = 0; round < N_SCORING_CATEGORIES; round++) {
			/* Each round needs to let each player have a turn */
			for (int i = 0; i < nPlayers; i++) {
				doPlayerTurn(i + 1);
			}
		}
	}
		
	private void initScoreCard() {
		scoreCard = new int[nPlayers][N_CATEGORIES];
		for (int i = 0; i < scoreCard.length; i++) {
			for (int j = 0; j < scoreCard[0].length; j++) {
				scoreCard[i][j] = 0;
			}
		}
	}

	private void doPlayerTurn(int player) {
		/* Set noReroll flag to false to allow player full number of re-roll opportunities */
		noReroll = false;
		display.printMessage(playerNames[player -1] + "'s turn. Click \"Roll Dice\" button to roll the dice.");
		display.waitForPlayerToClickRoll(player);
		rollDice();
		display.displayDice(dice);
		/* Begin re-roll sequence */
		rollAgain();
		/* Only do second re-roll if the noReroll flag is set to false */
		if (!noReroll) {
			rollAgain();
		}
		/* Time to select a category and score the turn */
		int category = selectCategory();
		scoreTurn(category, player);
	}

	/** Rolls all N_DICE dice and stores the values in a new instance of the array dice */
	private void rollDice() {
		dice = new int[N_DICE];
		for (int i = 0; i < N_DICE; i++) {
			dice[i] = rgen.nextInt(1, 6);
		}
	}
	
	private void rollAgain() {
		display.printMessage("Select the dice you wish to re-roll and click \"Roll Again\".");
		display.waitForPlayerToSelectDice();
		if (areDiceSelected()) {
			rerollSelectedDice();
			display.displayDice(dice);
		} else noReroll = true;
	}
	
	private boolean areDiceSelected() {
		for (int i = 0; i < N_DICE; i++) {
			if (display.isDieSelected(i)) return true;
		}
		return false;
	}
	
	private void rerollSelectedDice() {
		for (int i = 0; i < N_DICE; i++) {
			if (display.isDieSelected(i)) {
				dice[i] = rgen.nextInt(1, 6);
			}
		}
	}

	/* Private instance variables */
	private int nPlayers; // The number of players in this game
	private String[] playerNames; // An array of Strings containing the names for each of the players
	private YahtzeeDisplay display; // The object that displays the game for the players
	private RandomGenerator rgen = new RandomGenerator(); // Random generator
	private int[] dice; // An array holding the int values of the dice
	private int[][] scoreCard; // A 2 dimensional array representing the entire score card
	private boolean noReroll; // A flag to alert the player turn routine that a second re-roll has been declined by the player

}
