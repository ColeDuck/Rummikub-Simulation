import java.util.ArrayList;

public final class Game {

    private ArrayList<Player> players = new ArrayList<>();
    private Board board = new Board();
    int roundCounter = 0;
    int playerTurnCounter = 0;

    public Game() {
        board = new Board();
        players = new ArrayList<Player>();
    }

    public boolean addPlayer() {
        if (players.size() == 4) return false;

        Player newPlayer = new Player();
        for (int i = 0; i < 14; i++) {
            newPlayer.addPiece(board.pullFromPool());
        }
        players.add(newPlayer);
        return true;
    }

}
