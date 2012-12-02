/*
 * File: Yahtzee.java
 * ------------------
 * This program plays the Yahtzee game.
 */

import java.util.Arrays;

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
			while (true) {
				nPlayers = dialog.readInt("Enter number of players (1-4)");
				if (1 <= nPlayers && nPlayers <= 4) break; // error-checking to make sure the number of players is valid
			}
			playerNames = new String[nPlayers];
			for (int i = 1; i <= nPlayers; i++) {
				playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
			}
			display = new YahtzeeDisplay(getGCanvas(), playerNames);
			if (playerNames[0].equals("dirtycheater")) cheatMode = true; // Enable cheat mode!
			else cheatMode = false; // Or not...
			playGame();
			showGameOver();
			if (!playAgain()) break;
			display.printMessage("");
		}
	}

	/** Performs game-over tasks: calculate final scores, award bonus points,
	 * and congratulate the winner by name */
	private void showGameOver() {
		calcUpperAndLower();
		awardBonus();
		calcTotal();
		congratsWinner();
	}

	/** Calculates and displays the upper and lower scores */
	private void calcUpperAndLower() {
		/* Upper score */
		for (int i = 0; i < nPlayers; i++) {
			scoreCard[i][UPPER_SCORE - 1] = 0;
			for (int j = 0; j < UPPER_SCORE - 1; j++) {
				scoreCard[i][UPPER_SCORE - 1] += scoreCard[i][j];
			}
			display.updateScorecard(UPPER_SCORE, i + 1, scoreCard[i][UPPER_SCORE - 1]);
		}
		/* Lower score */
		for (int i = 0; i < nPlayers; i++) {
			scoreCard[i][LOWER_SCORE - 1] = 0;
			for (int j = THREE_OF_A_KIND - 1; j < LOWER_SCORE - 1; j++) { // index starts at THREE_OF_A_KIND -1 because that is the first category in the lower block
				scoreCard[i][LOWER_SCORE - 1] += scoreCard[i][j];
			}
			display.updateScorecard(LOWER_SCORE, i + 1, scoreCard[i][LOWER_SCORE - 1]);
		}
	}

	/** Awards 35 bonus points to each player whose upper score is at least
	 * 63 and updates the display */
	private void awardBonus() {
		for (int i = 0; i < nPlayers; i++) {
			scoreCard[i][UPPER_BONUS - 1] = 0;
			if (scoreCard[i][UPPER_SCORE - 1] >= 63) {
				scoreCard[i][UPPER_BONUS - 1] = 35;
				display.updateScorecard(UPPER_BONUS, i + 1, 35);
			}
		}
	}

	/** Calculates and displays each player's total score */
	private void calcTotal() {
		for (int i = 0; i < nPlayers; i++) {
			scoreCard[i][TOTAL - 1] = scoreCard[i][UPPER_SCORE - 1] + scoreCard[i][UPPER_BONUS - 1] + scoreCard[i][LOWER_SCORE - 1];
			display.updateScorecard(TOTAL, i + 1, scoreCard[i][TOTAL - 1]);
		}
	}
	
	/** Displays an appropriate message at the end of the game, congratulating
	 *  the winner and announcing their score, or announcing that there is a
	 *  tie, who is tied, and what their scores are
	 */
	private void congratsWinner() {
		/* Initialize local variables to reasonable defaults */
		boolean tie = false; // No tie detected yet
		int[] tiedPlayers = new int[nPlayers]; // Create array to hold numbers of players tied for highest score
		Arrays.fill(tiedPlayers, -1); // Fill tiedPlayers array with a number that can never be a player number
		int highScorer = -1; // Normally would use 0, but 0 actually denotes a player. Therefore use a value never used for players.
		int highScore = 0; // Any score at all will trigger the "highest score yet" logic for the first player

		/* For each player, check whether player's total score ties or beats the previous highest score */
		for (int i = 0; i < nPlayers; i++) {
			/* In the event of a tie, set the tie flag to true and add the player's scoreCard number to 
			 * the array of tied players at that score */
			if (scoreCard[i][TOTAL - 1] == highScore) {
				tie = true;
				Arrays.sort(tiedPlayers); // Puts all of the "empty" (i.e., -1) array components at the front
				tiedPlayers[0] = i; // Stores the tied player's number in the array
				/* If the total is the highest yet checked, clear any tie information and set this player and
				 * total score to be the highest */
			} else if (scoreCard[i][TOTAL - 1] > highScore) {
				tie = false; // Set tie flag to show that there is no tie for highest score
				Arrays.fill(tiedPlayers,  -1); // Reset tiedPlayers array to show no one currently tied for highest score
				highScore = scoreCard[i][TOTAL - 1];
				highScorer = i;
			}
		}
		if (tie) {
			/* The message for a tie is complicated, so it is built up piece by piece before being displayed */
			String tieMessage = "There was a tie between " + playerNames[highScorer] + " and";
			Arrays.sort(tiedPlayers);
			for (int i = tiedPlayers.length - 1; i >= 0; i--) { // Loop through tiedPlayers backwards
				if (tiedPlayers[i] < 0) break; // Stop loop when it gets to the part where the array is all -1 to the beginning thanks to the sort
				tieMessage += " " + playerNames[tiedPlayers[i]] + " and";
			}
			int lastAnd = tieMessage.lastIndexOf(" and"); // Find the last " and" to remove it
			tieMessage = tieMessage.substring(0, lastAnd); // Remove last " and"
			tieMessage += "! They each had a score of " + highScore + ".";
			display.printMessage(tieMessage);
		} else { // Display the winning player name and score
			String winningPlayerName = playerNames[highScorer];
			int winningPlayerScore = scoreCard[highScorer][TOTAL - 1];
			display.printMessage("Congratulations, " + winningPlayerName + "! You won with " + winningPlayerScore + " points.");
		}
	}

	/** Asks whether the player would like to start a new game. Will keep
	 *  asking until it gets either a "y" or "n" (ignoring case).
	 *  
	 * @return 	<code>true</code> if "y" is entered or <code>false</code> if
	 * 			"n" is entered
	 */
	private boolean playAgain() {
		IODialog dialog = getDialog();
		while (true) {
			String playAgain = dialog.readLine("Play again? Y/N: ");
			if (playAgain.equalsIgnoreCase("y")) return true;
			else if (playAgain.equalsIgnoreCase("n")) return false;
		}
	}

	/** Controls the flow of the game. Each player gets a turn during each
	 *  round and there are as many rounds as there are scoring categories
	 *  (traditionally 13).
	 */
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
	
	/** Initializes the score card at the beginning of the game */
	/* Initializes the score card array to contain -1 in all categories for all players */	
	private void initScoreCard() {
		scoreCard = new int[nPlayers][N_CATEGORIES];
		for (int i = 0; i < scoreCard.length; i++) {
			for (int j = 0; j < scoreCard[0].length; j++) {
				scoreCard[i][j] = -1;
			}
			scoreCard[i][TOTAL - 1] = 0;
		}
	}

	/** Plays a single turn for the current player. 
	 * 
	 * @param player the player number <code>0 &lt; player &lt;= 4</code>*/
	private void doPlayerTurn(int player) {
		currentPlayer = player;
		/* Set noReroll flag to false to allow player full number of re-roll opportunities */
		if (!cheatMode) {
			noReroll = false;
			display.printMessage(playerNames[player -1] + "'s turn. Click \"Roll Dice\" button to roll the dice.");
			display.waitForPlayerToClickRoll(player);
			rollDice();
		}
		if (cheatMode) {
			noReroll = true;
			dice = new int[N_DICE];
			IODialog dialog = getDialog();
			for (int i = 0; i < N_DICE; i++) {
				dice[i] = dialog.readInt("Enter value for die " + (i + 1));
			}
		}
		display.displayDice(dice);
		/* Begin re-roll sequence */
		/* First re-roll will always take place unless cheatMode is true */
		if (!noReroll) rollAgain();
		/* Only do second re-roll if the noReroll flag is set to false */
		if (!noReroll) rollAgain();
		/* Time to select a category and score the turn */
		int category = selectCategory();
		scoreTurn(category);
	}

	/** Rolls all N_DICE dice and stores the values for later use */
	private void rollDice() {
		dice = new int[N_DICE]; // Use an array to store the dice values
		for (int i = 0; i < N_DICE; i++) {
			dice[i] = rgen.nextInt(1, 6);
		}
	}
	
	/** Prompts the player to select dice to re-roll and checks to see whether
	 *  any were selected when the player clicks the re-roll button. 
	 */ 
	private void rollAgain() {
		display.printMessage("Select the dice you wish to re-roll and click \"Roll Again\".");
		display.waitForPlayerToSelectDice();
		if (areDiceSelected()) {
			rerollSelectedDice();
			display.displayDice(dice);
		} else noReroll = true;
	}

	/** Detects whether the player has selected any dice for re-roll */
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
	 *  Returns the category number as defined in the <code>YahtzeeConstants</code> class.
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
	 *  Values are "allowed" if they could generate a score higher than zero for that category.
	 *  However, the categories <code>ONES</code>, <code>TWOS</code>, etc. and <code>CHANCE</code> are always allowed.
	 *  
	 *   @param  category the category number as given in <code>YahtzeeConstants</code>
	 *   @return <code>true</code> if the dice values are allowed, <code>false</code> otherwise
	 */
	private boolean checkCategory(int category) {
		/* This method monkeys around quite a bit with the dice array, so it uses a copy instead
		 * of the array used elsewhere to track the actual dice. The first change to the array is
		 * to sort it so that it will be easier to iterate over to see what is in there.
		 */
		int[] diceCopy = Arrays.copyOf(dice, N_DICE);
		Arrays.sort(diceCopy);

		if (category == ONES || category == TWOS || category == THREES || category == FOURS || 
				category == FIVES || category == SIXES || category == CHANCE) return true;

		/* Checking for n of a kind is basically same for any n, so only one block is required for
		 * three of a kind, four of a kind, and Yahtzee (five of a kind). The counter variable 
		 * keeps track of how many dice in the array have the value currently stored in the matching
		 * variable. The counter never goes below 1 because there is always at least one die
		 * matching itself stored in matching.
		 */
		else if (category == THREE_OF_A_KIND || category == FOUR_OF_A_KIND || category == YAHTZEE) {
			int counter = 1; // How many dice match
			int matching = diceCopy[0]; // What value of die is currently being counted by counter
			int maxCounter = 0; // The highest value the counter has reached
			for (int i = 1; i < N_DICE; i++) { // Start at 1 because the first die always matches itself
				if (diceCopy[i] == matching) { // Increment the counter if the current index die is the same as the value of matching
					counter++;
				} else { // Set maxCounter to counter, reset the counter to 1 and matching to the current die
					maxCounter = counter;
					counter = 1;
					matching = diceCopy[i];
				}
			}
			/* Check to see whether the counter is at least as high as the number of a kind under consideration */
			if ((category == THREE_OF_A_KIND && maxCounter >= 3) || (category == FOUR_OF_A_KIND && maxCounter >= 4) ||
					(category == YAHTZEE && maxCounter == 5)) return true;
			else return false; 
		}

		/* Checking for a full house is equivalent to checking for two of a kind and three of a kind.
		 * This block accomplishes this by checking the two possible numerically sorted dice 
		 * configurations for a full house: first three match and last two match OR first two match and
		 * last three match.   
		 */
		else if (category == FULL_HOUSE) {
			/* Check XXXYY pattern */
			if (diceCopy[0] == diceCopy[1] && diceCopy[1] == diceCopy[2] && 
					diceCopy[3] == diceCopy[4]) return true;
			/* Check XXYYY pattern */
			else if (diceCopy[0] == diceCopy[1] && 
					diceCopy[2] == diceCopy[3] && diceCopy[3] == diceCopy[4]) return true;
			else return false;
		}

		/* To check for a small straight, the first step is to remove all the duplicates from the dice
		 * array. If there are fewer than 4 dice left, it can't be a small straight. If there are more
		 * than 4 dice left, they can be checked for sequence similarly to a large straight. However, 
		 * once the duplicates are removed, either the lowest four or highest four dice must be a
		 * straight (this will be all the dice both times if there was a duplicate). Therefore, this
		 * block checks all the dice for a straight if there are four remaining and checks both the
		 * lower four and higher four if there were no duplicates and five dice remain. 
		 */
		else if (category == SMALL_STRAIGHT) {
			/* Check for, count, and catalog duplicates */
			int nDupes = 0;
			int dupe = -1;
			int[] noDupes;
			for (int i = 1; i < N_DICE; i++) { // Start at 1 because we are always checking if diceCopy[i] equals diceCopy[i-1]
				if (diceCopy[i] == diceCopy[i - 1]) {
					nDupes++;
					dupe = i;
				}
			}
			
			/* If there is exactly one duplicate, copy the non-duplicate dice into a new array of length 4 */
			if (nDupes == 1) {
				noDupes = new int[4];
				noDupes[0] = diceCopy[0];
				for (int i = 1; i < N_DICE; i++) {
					if (i == dupe) continue;
					else if (i < dupe) noDupes[i] = diceCopy[i];
					else if (i > dupe) noDupes[i - 1] = diceCopy[i]; 
				}
				/* The 4 long array must be a perfect straight, so just check it like a large straight */
				for (int i = 1; i < noDupes.length; i++) {
					if (noDupes[i] != noDupes[i - 1] + 1) return false;
					if (i == noDupes.length - 1) return true;
				}
				return false;
			}
			/* If there are no duplicates, either the first four or the last four (or both) must be a 
			 * straight. Check both of those, and return false if it fails both.
			 */
			else if (nDupes == 0) {
				noDupes = Arrays.copyOf(diceCopy, N_DICE);
				for (int i = 1; i < noDupes.length - 1; i++) {
					if (noDupes[i] != noDupes[i - 1] + 1) break;
					if (i == noDupes.length - 2) return true;
				}
				for (int i = 2; i < noDupes.length; i++) {
					if (noDupes[i] != noDupes[i - 1] + 1) break;
					if (i == noDupes.length - 1) return true;
				}
				return false;
			}
			/* If there is not one duplicate and not zero duplicates, then there cannot be a small straight */
			else return false;
		}

		/* For a large straight, all five sorted dice must be sequential. This block check that by
		 * iterating over the dice values and checking whether each one is one higher than the previous.
		 */
		else if (category == LARGE_STRAIGHT) {
			for (int i = 1; i < N_DICE; i++) {
				if (diceCopy[i] != diceCopy[i - 1] + 1) break;
				if (i == N_DICE - 1) return true;
			}
			return false;
		}
		
		/* This should never happen. Something is badly wrong in another method if it does. */
		else throw new ErrorException("That category doesn't exist!");
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
		scoreCard[currentPlayer - 1][TOTAL - 1] += score;
		display.updateScorecard(TOTAL, currentPlayer, scoreCard[currentPlayer -1][TOTAL - 1]);
	}

	/** Calculates the score for the current turn based on the values of the
	 * and the player-selected category
	 * 
	 * @param category the category number as given by <code>YahtzeeConstants</code>
	 * @return the score in points
	 */
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

	/** Adds up some or all of the values on the dice.
	 *  
	 *  @param pips the <code>int</code> value of dice that should be included
	 *  			in the sum returned. A value of 7 means that all dice
	 *  			should be included.
	 *  @return the sum of the included dice
	 */
	private int sumDice(int pips) {
		int sum = 0;
		if (1 <= pips && pips <= 6) {
			for (int i = 0; i < N_DICE; i++) {
				if (dice[i] == pips) sum += pips;
			}
		} else if (pips == 7) {
			for (int i = 0; i < N_DICE; i++) {
				sum += dice[i];
			}
		} else throw new ErrorException("Invalid input for sumDice");
		return sum;
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
	private boolean cheatMode; // Tracks whether cheat mode (for testing!) is on

}
