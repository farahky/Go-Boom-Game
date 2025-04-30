import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
 
public class GoBoom {
    private static final int NUMOFPLAYER = 4;
    private static final String[] SUITS = {"C", "D", "H", "S"};
    private static final String[] RANKS = {"A", "K", "Q", "J", "10", "9", "8", "7", "6", "5", "4", "3", "2"};
 
    // Class Variable
    boolean exitGame, gameOver;
    int cardsPlayed = 0;
    int trickCount = 1;
    int currentPlayer;
    List<String> deck;
    List<List<String>> players = new ArrayList<>();
    List<Integer> playerScores = new ArrayList<>();
    List<String> centerCard = new ArrayList<>();
 
 
 
    public static void main(String[] args) {
        // Start the game
        new GoBoom().startGame();
    }
 
    /**
      * Display menu navigation on I/O receive
      */
    public void displayHelp() {
        System.out.println("\t++===============================================================++");
        System.out.println("\t||                       Let's play Go Boom!                     ||");
        System.out.println("\t++===============================================================++");
        System.out.println("\t||  Here's a list of playable commands:                          ||");
        System.out.println("\t||  1. n - Start a new game                                      ||");
        System.out.println("\t||  2. s - Save game into File                                   ||");
        System.out.println("\t||  3. l - Load game from File                                   ||");
        System.out.println("\t||  4. x - Exit the game                                         ||");
        System.out.println("\t||  5. d - Draws cards from the deck until a valid card is drawn ||");
        System.out.println("\t||  6. v - View the current player's hand                        ||");
        System.out.println("\t||  7. r - Reset players' score                                  ||");
        System.out.println("\t++===============================================================++");
        System.out.println("\t||                         Let's get started!                    ||");
        System.out.println("\t++===============================================================++");
    }
 
    /**
      * Display recent game updates
      */    
    public void displayGame() {
            // Print the current trick number
            System.out.println("\n");
            System.out.println("Trick #" + trickCount);
 
            // Display the cards for each player
            for (int i = 0; i < players.size(); i++) {
                System.out.println("Player" + (i + 1) + ": " + players.get(i));
            }
            // Display the center card
            System.out.println("Center: " + centerCard);
            // Display the remaining cards in the deck
            System.out.println("Deck: " + deck);
 
            // Player's scores
            System.out.print("Scores: ");
            for (int i = 1; i < playerScores.size(); i++)
                System.out.print("Player" + i + " = " + playerScores.get(i-1) + " | ");
            System.out.println("Player " + playerScores.size() + " = " + playerScores.get(playerScores.size()-1));
    }
 
    /**
      * Reset the game
      */
    private void initializeGame(boolean isResetScore) {
        // Create a new deck of cards
        deck = createDeck();
        // Shuffle the deck
        Collections.shuffle(deck);
 
        // Clear the list [function when 2nd game onwards]
        players.clear();
        centerCard.clear();
 
        // Deal 7 cards to each player
        for (int i = 0; i < NUMOFPLAYER; i++) {
            List<String> playerCards = new ArrayList<>();
            for (int j = 0; j < 7; j++) {
                playerCards.add(deck.remove(deck.size() - 1));
            }
            players.add(playerCards);
        }
    
        if (isResetScore || playerScores.isEmpty())
            resetScore();
 
        // Initialize the center with one card
        centerCard.add(deck.remove(deck.size() - 1));
 
        // Initialize the current player with the first player based on the first lead card
        currentPlayer = determineFirstPlayer();
 
        // initialize trick count
        trickCount = 1;
    }
 
    private void resetScore() {
        playerScores.clear();
        for (int i = 0; i < NUMOFPLAYER; i++)
            playerScores.add(0);
    }
 
    /**
      * Save a game to a file
      * @param filename - file to save
      */
    private void saveFile(String filename) {
        String result = "";
        result += currentPlayer + ",";
        result += cardsPlayed + ",";
        result += trickCount + ",";
        result += "?\n";
 
        for (List<String> data : players) {
            for (String card : data) {
                result += card + "->";
            }
            result += ";\n";
        }
        result += "?\n";
 
        for (String card : deck) {
            result += card + "->";
        }
        result += "?\n";
 
        for (String card : centerCard) {
            result += card + "->";
        }
        result += "?\n";
 
        for (int score : playerScores) {
            result += score + ">";
        }
        result += "\n";
 
        try {
            File file;
            file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }
 
            StringBuilder fileContent = new StringBuilder();
            fileContent.append(result);
 
            try (FileWriter writer = new FileWriter(file)) {
                    String data = fileContent.toString();
                    writer.write(data);
            }
        } catch (Exception e) {
            System.out.println("Something wrong, file can't be save");
        }
 
    }
 
    /**
      * Load a game from a file
      * @param filename - file to load
      */
    private void loadFile(String filename) {
        try {
            File file;
            file = new File(filename);
            if (!file.exists()) {
                throw new IOException("File does not exist");
            }
 
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                String data = "";
 
                while ((line = reader.readLine()) != null)
                    data += line;
                String[] lines = data.split("\\?");
 
                String[] theFirst3Data = lines[0].split(",");
                currentPlayer = Integer.parseInt(theFirst3Data[0]);
                cardsPlayed = Integer.parseInt(theFirst3Data[1]);
                trickCount = Integer.parseInt(theFirst3Data[2]);
 
                String[] playersCard = lines[1].split(";");
                players.clear();
 
                System.out.println("Testing here");
                for (String playerCard : playersCard) {
                    String[] cards = playerCard.split("->");
                    ArrayList<String> hand = new ArrayList<>(Arrays.asList(cards));
                    players.add(hand);
                }
                 
                String[] deckCards = lines[2].split("->");
                deck.clear();
                deck = new ArrayList<>(Arrays.asList(deckCards));
 
                String[] centerCards = lines[3].split("->");
                centerCard.clear();
                centerCard = new ArrayList<>(Arrays.asList(centerCards));
 
                String[] scores = lines[4].split(">");
                playerScores.clear();
                for (String score : scores)
                    playerScores.add(Integer.parseInt(score));
 
            }
        } catch (Exception e) {
            System.out.println("Something wrong, file can't be load");
        }
    }
 
    /**
      * Game Logic Algorithm
      */
    public void startGame() {
        Scanner scanner = new Scanner(System.in);
 
        // the whole game loop, including restart
        while (!exitGame) {
 
            // Start the game
            gameOver = false;
            initializeGame(false);
 
            // display the game ui for start
            displayHelp();
 
            // the current game loop (1 at a time)
            while (!gameOver) {
 
                // Play a card for each player in the trick
                cardsPlayed = 0;
 
                // display game before loop
                displayGame();
 
                // 1 trick game loop
                while (!gameOver && cardsPlayed < players.size()) {
                    // Print the current player's hand
                    System.out.println("Turn: Player" + (currentPlayer + 1));
 
                    // Prompt the player for a card to play
                    System.out.print("> ");
                    String input = scanner.nextLine();
 
                    switch (input) {
                        case "n":
                        case "N":
                            System.out.println("Starting a new game...\n");
                            gameOver = true;
                            resetScore();
                            break;
                        case "x":
                        case "X":
                            System.out.println("Exiting the game. Goodbye!");
                            gameOver = true;
                            exitGame = true;
                            resetScore();
                            break;
                        case "s":
                        case "S":
                            System.out.print("Enter a filename abc.txt> ");
                            String savefilename = scanner.nextLine();
                            saveFile(savefilename);
                            break;
 
                        case "l":
                        case "L":
                            System.out.print("Enter a filename abc.txt> ");
                            String loadfilename = scanner.nextLine();
                            loadFile(loadfilename);
                            break;
 
                        case "d":
                        case "D":
                            boolean found = false;
 
                            // Draw cards until a playable card is achieved
                            while (!centerCard.isEmpty() && deck.size()>0) {
                                String drawnCard = deck.remove(deck.size() - 1);
                                players.get(currentPlayer).add(drawnCard);
                                System.out.println("You drew a card: " + drawnCard);
                                if (isValidPlay(drawnCard, cardsPlayed)) {
                                    // Play the playable card
                                    players.get(currentPlayer).remove(drawnCard);
                                    centerCard.add(drawnCard);
                                    found = true;
                                    break;
                                }
                            }
 
                            if (!found && centerCard.isEmpty()) {
                                System.out.println("There is no card at the center, there is no reason to draw a card.");
                            }
 
                            // skips if deck have no card left
                            else if (found || deck.isEmpty()) {
                                cardsPlayed++;
                                // Move to the next player
                                currentPlayer = (currentPlayer + 1) % players.size();
                            }
 
                            break;
 
                        case "v":
                        case "V":
                            System.out.println("Player" + (currentPlayer + 1) + " Cards: " + players.get(currentPlayer));
                            break;
 
                        case "r":
                        case "R":
                            resetScore();
                            break;
 
 
                        default:
                            if (input.length()>1 && cardInHand(input, players.get(currentPlayer)) && isValidPlay(input, cardsPlayed)) {
                                // Play the card
                                players.get(currentPlayer).remove(input.toUpperCase());
                                centerCard.add(input.toUpperCase());
                                cardsPlayed++;
                                // Move to the next player
                                currentPlayer = (currentPlayer + 1) % players.size();
                            }
                            else {
                                System.out.println("Invalid card or play! Try again.");
                                continue;
                            }
                            break;
                    }
 
                    // display updated game
                    displayGame();
                }
                     
                // Ends the game if player chooses to exit
                if (gameOver || exitGame) break;
 
                // Determine the winner of the trick
                int trickWinner = (determineTrickWinner(centerCard) + currentPlayer) % players.size();
 
                // Determine the winner of the game
                int gameWinner = -1;
 
                // Move to the next trick
                trickCount++;
 
                // Reset the center card and currentPlayer for the next trick
                centerCard.clear();
                currentPlayer = trickWinner;
                System.out.println("\n** Player" + (trickWinner + 1) + " wins trick #" + (trickCount - 1)+ " **\n");
 
                // Check if the game is over (all cards have been played)
                if (players.get(0).isEmpty() || players.get(1).isEmpty()
                        || players.get(2).isEmpty() || players.get(3).isEmpty()) {
                    gameOver = true;
                    for (int i=0; i<playerScores.size(); i++) {
                        for (String card : players.get(i))
                                playerScores.set(i, playerScores.get(i)+getCardScore(card));
                        if (playerScores.get(i) == 0)
                            gameWinner = i;
                    }
                }
 
 
                if (gameOver) {
                    System.out.println("\n** Player" + (gameWinner + 1) + " wins this game." + " **\n");
                    System.out.println("Scores:");
                    for (int i=0; i<playerScores.size(); i++)
                        System.out.println("Player" + (i+1) + " has scored " + playerScores.get(i));
                    System.out.println("\nGame Over... Press Enter Key to Continue");
                    scanner.nextLine();
                }
            }
        }
 
        scanner.close();
    }
 
    /**
      * Check the first card suit
      * @return the first card suit
      */
    private String determineLeadSuit() {
        if (centerCard.isEmpty()) {
            return ""; // Return an empty string when the list is empty
        }
        String leadCard = centerCard.get(0);
        return leadCard.substring(0, 1);
    }
 
    /**
      * Determine the trick's winner of each trick
      * @param cards - the center list of card to compared
      * @return - index of the winner
      */
    private int determineTrickWinner(List<String> cards) {
        String leadSuit = determineLeadSuit();
        int highestRankIndex = -1;
        int highestRank = -1;
 
        for (int i = 0; i < cards.size(); i++) {
            String card = cards.get(i);
            String suit = card.substring(0, 1);
            String rank = card.substring(1);
 
            if (suit.equals(leadSuit)) {
                int rankIndex = getRankIndex(rank);
                if (rankIndex > highestRank) {
                    highestRank = rankIndex;
                    highestRankIndex = i;
                }
            }
        }
        int numPlayers = cards.size();
        int trickWinner = (highestRankIndex + numPlayers - ((trickCount==1)?1:0)) % numPlayers;
        return trickWinner;
    }
 
    private int getCardScore(String card) {
        switch (card.substring(1)) {
            case "a":
            case "A":
                return 1;
            case "j":
            case "J":
            case "q":
            case "Q":
            case "k":
            case "K":
                return 10;
            default:
                return Integer.parseInt(card.substring(1));
        }
    }
 
    /**
      * Mapping Rank of each suit
      * @param rank - the rank string [display]
      * @return the rank value
      */
    private int getRankIndex(String rank) {
        String[] ranks = {"A", "K", "Q", "J", "10", "9", "8", "7", "6", "5", "4", "3", "2"};
        for (int i = 0; i < ranks.length; i++) {
            if (rank.equals(ranks[i])) {
                return ranks.length-i;
            }
        }
        return -1; // Invalid rank
    }
 
    /**
      * Check a card is in player hand
      * @param inputCard - card to check
      * @param player - player to check
      * @return the card is in the player's hand
      */
    private boolean cardInHand(String inputCard, List<String> player) {
        for (String card : player)
            if (inputCard.equalsIgnoreCase(card))
                return true;
        return false;
    }
 
    /**
      * Check if the player can make a valid play
      * @param card - card to check
      * @param playerIndex - current cycle starter player
      * @return the card is valid to dealt
      */
    private boolean isValidPlay(String card, int playerIndex) {
        if (trickCount != 1 && playerIndex == 0) {
            // Player 1 (lead player) can play any card
            // Card Played 0 indicates start of the player of the current tricks
            return true;
        } else {
            String leadCard = centerCard.get(0);
            String leadSuit = leadCard.substring(0, 1);
            String leadRank = leadCard.substring(1);
 
            String suit = card.substring(0, 1);
            String rank = card.substring(1);
 
 
            // Check if the card has the same suit or the same rank as the lead card
            return suit.equalsIgnoreCase(leadSuit) || rank.equalsIgnoreCase(leadRank);
        }
    }
 
    /**
      * Create a new deck of cards
      * @return new deck of card
      */
    private List<String> createDeck() {
        List<String> deck = new ArrayList<>();
        for (String suit : SUITS) {
            for (String rank : RANKS) {
                deck.add(suit + rank);
            }
        }
        return deck;
    }
 
    /**
      * Determine the first player based on the first lead card
      * @return index of the first player
      */
    private int determineFirstPlayer() {
        if (centerCard.isEmpty()) {
            return 0; // Return 0 (Player 1) when the list is empty
        }
        String firstCard = centerCard.get(0);
        String rank = firstCard.substring(1);
        if (rank.equals("A") || rank.equals("5") || rank.equals("9") || rank.equals("K")) {
            return 0; // Player 1
        } else if (rank.equals("2") || rank.equals("6") || rank.equals("10")) {
            return 1; // Player 2
        } else if (rank.equals("3") || rank.equals("7") || rank.equals("J")) {
            return 2; // Player 3
        } else if (rank.equals("4") || rank.equals("8") || rank.equals("Q")) {
            return 3; // Player 4
        } else {
            return 0; // Player 1 (default)
        }
    }
}