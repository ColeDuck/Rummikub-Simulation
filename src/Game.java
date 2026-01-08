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
        roundCounter = 1;
        playerTurnCounter = 0;
        if (!getPlayers()) return;

        // We have players so now let's do the game!
        while (true) {
            playRound();
            scorePoints();

            for (Player p : players) {
                p.resetRack();
            }

            board = new Board();
            System.out.println("Would you like to play another round or exit? Enter \"again\" or \"end\"");
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

    private void playRound() {
        while (true) {
            Player currentPlayer = players.get(playerTurnCounter);
            Player copyPlayer = new Player(currentPlayer.getName(), currentPlayer.getRack());
            Board copyBoard = board.copy();
            int moveCount = 0;

            // Let them make their move
            while (true) {
                pollReturn returned = pollPlayerAndMove();

                if (returned == pollReturn.END) {
                    if (moveCount == 0) {
                        currentPlayer.addPiece(board.pullFromPool());
                    }
                    break;
                }

                if (returned == pollReturn.RESET) {
                    board.restoreToPreviousState(copyBoard);
                    currentPlayer = copyPlayer;
                    moveCount = 0;
                    continue;
                }

                if (returned == pollReturn.MOVED) {
                    moveCount++;
                }
            }

            // We need to validate that the player left the board in a valid state
            if (!copyBoard.isLegalPosition()) {
                System.out.println("You did not leave the board in a valid state.");
                currentPlayer.setRack(copyPlayer.getRack());
                board.restoreToPreviousState(copyBoard);
                continue;
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
            System.out.println("move [index] [piece] to [index] [L/R]\nExample: \"move 5 r3 to 9 L\"\nIndex 0 is your rack");
            System.out.println("Type \"reset\" to undo all moves made on your turn");
            System.out.println("Type \"end\" to end your turn");
            return pollReturn.INVALID;
        }

        if (userMessage.equals("reset")) {
            return pollReturn.RESET;
        }

        if (userMessage.equals("end")) {
            return pollReturn.END;
        }

        Move m = Move.createMove(userMessage);
        if (m == null) {
            System.out.println("Invalid move syntax");
            return pollReturn.INVALID;
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
        MOVED
    }

}
