import java.util.ArrayList;
import java.util.Scanner;

public final class Game {

    private ArrayList<Player> players = new ArrayList<>();
    private Board board;
    private int roundCounter;
    private int playerTurnCounter;

    public Game() {
        board = new Board();
        players = new ArrayList<Player>();
    }

    private boolean addPlayer(String name) {
        if (players.size() == 4) return false;

        Player newPlayer = new Player(name);
        for (int i = 0; i < 14; i++) {
            newPlayer.addPiece(board.pullFromPool());
        }

        players.add(newPlayer);
        return true;
    }

    public void startGame() {
        if (!getPlayers()) return;

        // We have players so now let's do the game!
        while (true) {
            roundCounter = 1;
            playerTurnCounter = 0;

            playGame();
            scorePoints();

            for (Player p : players) {
                p.resetRack();
            }

            board = new Board();
            System.out.println("Would you like to play another game or exit? Enter \"again\" or \"end\"");
            while (true) {
                Scanner sc = new Scanner(System.in);
                String input = sc.nextLine();
                if (input.equals("end")) return;
                if (input.equals("again")) break;
                System.out.println("Unrecognized command");
            }
        }

    }

    private void scorePoints() {
        int totalToWinner = 0;
        Player winner = null;
        for (Player p : players) {
            if (p.hasWon()) {
                winner = p;
                continue;
            }
            int playerPoints = p.totalRack();
            p.addScore(-playerPoints);
            totalToWinner += playerPoints;
        }

        if (winner != null) {
            winner.addScore(totalToWinner);
        }
    }

    //
    private void playGame() {

        // You only exit this function at the end of the game
        while (true) {
            Player currentPlayer = players.get(playerTurnCounter);
            Player turnStartPlayer = currentPlayer.deepCopy();
            Board turnStartBoard = board.deepCopy();
            int moveCount = 0;

            Player moveStartPlayer;
            Board moveStartBoard;

            // Let them make their move
            while (true) {
                moveStartPlayer = currentPlayer.deepCopy();
                moveStartBoard = board.deepCopy();

                pollReturn returned = pollPlayerAndMove();

                if (returned == pollReturn.END) {
                    if (moveCount == 0) {
                        byte newPiece = board.pullFromPool();
                        System.out.println(currentPlayer.getName() + " pulled a " + Piece.toString(newPiece) + " from the pool!");
                        currentPlayer.addPiece(newPiece);
                    }

                    // We need to validate that the player left the board in a valid state
                    if (!board.isLegalPosition()) {
                        System.out.println("You did not leave the board in a valid state.");
                        currentPlayer.setRack(moveStartPlayer.getRack());
                        board.restoreToPreviousState(moveStartBoard);
                        continue;
                    }

                    // Legal position, break from while loop

                    if (moveCount == 0) {
                        byte newPiece = board.pullFromPool();
                        System.out.println(currentPlayer.getName() + " pulled a " + Piece.toString(newPiece) + " from the pool!");
                        currentPlayer.addPiece(newPiece);
                    }

                    break;
                }

                if (returned == pollReturn.RESET) {
                    board.restoreToPreviousState(turnStartBoard);
                    currentPlayer.setRack(turnStartPlayer.getRack());
                    moveCount = 0;
                    continue;
                }

                if (returned == pollReturn.SORT_COLOUR) currentPlayer.sortRack(Player.SortType.COLOUR);
                if (returned == pollReturn.SORT_VALUE) currentPlayer.sortRack(Player.SortType.VALUE);

                if (returned == pollReturn.MOVED) {
                    moveCount++;
                }

                // Undo the move
                if (returned == pollReturn.INVALID) {
                    currentPlayer.setRack(moveStartPlayer.getRack());
                }
            }

            // The players turn is complete, so let's check if they have won
            if (currentPlayer.hasWon()) {
                return;
            }

            // No winner, let's just move on to the next player
            changeTurn();
        }
    }

    private boolean getPlayers() {

        while (true) {
            if (!players.isEmpty()) {
                System.out.print("Current players: ");
                for (int i = 0; i < players.size(); i++) {
                    System.out.print(players.get(i).getName());
                    if (i != players.size()-1) System.out.print(", ");
                }
                System.out.print("\n");

            }

            if (players.size() >= 2) {
                System.out.println("Start game with \"start\", add more players with \"add [name]\", exit with \"exit\"");
            } else {
                System.out.println("Add player with \"add [name]\", exit with \"exit\"");
            }

            Scanner sc = new Scanner(System.in);
            String input = sc.nextLine();

            if (input.equals("start")) {
                if (players.size() < 2) {
                    System.out.println("You must have atleast 2 players");
                    continue;
                } else {
                    return true;
                }
            }

            if (input.equals("exit")) {
                return false;
            }

            String[] split = input.split(" ");
            if (split.length < 2) {
                System.out.println("Unrecognized command");
                continue;
            }

            if (!split[0].equals("add")) {
                System.out.println("Unrecognized command");
                continue;
            }

            addPlayer(split[1]);
            System.out.println("Added player " + split[1]);
        }
    }

    // returns true if the player wants end their turn
    private pollReturn pollPlayerAndMove() {

        Player p = players.get(playerTurnCounter);

        System.out.println(p.getName() + " - What would you like to do? (type \"help\" for help)");
        System.out.println(p.getRackString(false));
        System.out.println(board.toString());

        Scanner sc = new Scanner(System.in);
        String userMessage = sc.nextLine();
        if (userMessage.equals("help")) {
            System.out.println("To make a move, type it in this format:");
            System.out.println("move [index] [piece] to [index] [L/R]\nExample: \"move 5 r3 to 9 L\"");
            System.out.println("To access your rack, type \"rack\" in the first index");
            System.out.println("To create a new combo, type \"new\" in the second index");
            System.out.println("L/R is optional, and defaults to left if not specified");
            System.out.println("Type \"reset\" to undo all moves made on your turn");
            System.out.println("Type \"end\" to end your turn");
            System.out.println("Type \"sort [colour/value]\" to sort your rack");
            return pollReturn.NOTHING;
        }

        if (userMessage.equals("reset")) {
            return pollReturn.RESET;
        }

        if (userMessage.equals("end")) {
            return pollReturn.END;
        }

        String[] split = userMessage.split(" ");
        if (split.length == 2 && split[0].equals("sort")) {
            if (split[1].equals("value")) return pollReturn.SORT_VALUE;
            if (split[1].equals("colour")) return pollReturn.SORT_COLOUR;
            return pollReturn.NOTHING;
        }

        Move m = Move.createMove(userMessage);
        if (m == null) {
            System.out.println("Invalid move syntax");
            return pollReturn.NOTHING;
        }

        if (!board.makeMove(m, p)) {
            System.out.println("Invalid move!");
            return pollReturn.INVALID;
        }

        return pollReturn.MOVED;
    }


    private void changeTurn() {
        playerTurnCounter = (playerTurnCounter + 1) % players.size();
        if (playerTurnCounter == 0) roundCounter++;
    }

    enum pollReturn {
        RESET,
        END,
        INVALID,
        MOVED,
        SORT_VALUE,
        SORT_COLOUR,
        NOTHING
    }

}
