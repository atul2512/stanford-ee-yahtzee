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
	/* Initializes the score card array to contain -1 in all categories for all players */	
	private void initScoreCard() {
		scoreCard = new int[nPlayers][N_CATEGORIES];
		for (int i = 0; i < scoreCard.length; i++) {
			for (int j = 0; j < scoreCard[0].length; j++) {
				scoreCard[i][j] = -1;
			}
		}
	}

	/** Plays a single turn for the current player. 
	 * 
	 * @param player the player number <code>0 &lt; player &lt;= 4</code>*/
	private void doPlayerTurn(int player) {
		currentPlayer = player;
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
		scoreTurn(category);
	}

	/** Rolls all N_DICE dice and stores the values in a new instance of the array dice */
	private void rollDice() {
		dice = new int[N_DICE];
		for (int i = 0; i < N_DICE; i++) {
			dice[i] = rgen.nextInt(1, 6);
		}
	}
	/** Prompts the player to select dice to re-roll and checks to see whether any were selected when the player clicks the re-roll button. */ 
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
	
	/** Re-rolls only the selected dice. */
	private void rerollSelectedDice() {
		for (int i = 0; i < N_DICE; i++) {
			if (display.isDieSelected(i)) {
				dice[i] = rgen.nextInt(1, 6);
			}
		}
	}

	/** Prompts the player to select a scoring category. Reprompts if the selected category is not valid.
	 *  Returns the category number as defined in the YahtzeeConstants class.
	 *  
	 *   @return the category number as given in <code>YahtzeeConstants</code> 
	 */
	private int selectCategory() {
		display.printMessage("Pick a category to score this turn.");
		while (true) {
			int category = display.waitForPlayerToSelectCategory();
			if (isCategoryEmpty(category)) {
				return category;
			} else display.printMessage("That is not a valid category. Please pick another.");
		}
	}

	/** Checks whether the player-selected category has already been used.
	 * 
	 * @param  category category number as given in <code>YahtzeeConstants</code>
	 * @return <code>true</code> if the category is available and <code>false</code> if it is not 
 	 */
	private boolean isCategoryEmpty(int category) {
		if (scoreCard[currentPlayer - 1][category - 1] < 0) return true;
		return false;
	}

	/** Checks whether the dice values are "allowed" for the selected category.
	 *  Values are "allowed" if they would generate a score higher than zero for that category.
	 *  
	 *   @param  category the category number as given in <code>YahtzeeConstants</code>
	 *   @return <code>true</code> if the dice values are allowed, <code>false</code> otherwise
	 */
	private boolean checkCategory(int category) {
		return YahtzeeMagicStub.checkCategory(dice, category);
		// TODO Using Stanford-provided pre-compiled magic stub. Need to implement my own solution.
		
	}
	
	/** Calculates the score for the turn given the dice values and selected category. 
	 *  Assumes the category is previously unscored. Does not return anything as it updates the displayed
	 *  score card and scoreCard array directly.
	 *  
	 *  @param category the category number as given in <code>YahtzeeConstants</code>
	 */
	private void scoreTurn(int category) {
		int score = calculateScore(category);
		scoreCard[currentPlayer - 1][category - 1] = score;
		display.updateScorecard(category, currentPlayer, score);
	}

	private int calculateScore(int category) {
		boolean scorable = checkCategory(category);
		if (scorable) {
			
			switch (category) {
			case ONES:
				return sumDice(1);
			case TWOS:
				return sumDice(2);
			case THREES:
				return sumDice(3);
			case FOURS:
				return sumDice(4);
			case FIVES:
				return sumDice(5);
			case SIXES:
				return sumDice(6);
			case THREE_OF_A_KIND:
				return sumDice(7);
			case FOUR_OF_A_KIND:
				return sumDice(7);
			case FULL_HOUSE:
				return 25;
			case SMALL_STRAIGHT:
				return 30;
			case LARGE_STRAIGHT:
				return 40;
			case YAHTZEE:
				return 50;
			case CHANCE:
				return sumDice(7);
			default:
				throw new ErrorException("The selected category doesn't exist!");
			}
		} else {
			return 0;
		}
	}
	
	
	private int sumDice(int pips) {
		
	}

	/* Private instance variables */
	private int nPlayers; // The number of players in this game
	private String[] playerNames; // An array of Strings containing the names for each of the players
	private YahtzeeDisplay display; // The object that displays the game for the players
	private RandomGenerator rgen = new RandomGenerator(); // Random generator
	private int[] dice; // An array holding the int values of the dice
	private int[][] scoreCard; // A 2 dimensional array representing the entire score card
	private boolean noReroll; // A flag to alert the player turn routine that a second re-roll has been declined by the player
	private int currentPlayer; // The number of the player whose turn it is

}
