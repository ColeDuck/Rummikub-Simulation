import java.util.ArrayList;
import java.util.Arrays;

public class Player {

    private ArrayList<Byte> rack;
    int score = 0;

    public Player() {
        this.rack = new ArrayList<>();
    }
    public Player(Byte[] rack) {
        this.rack = new ArrayList<>(Arrays.asList(rack));
    }
    public Player(ArrayList<Byte> rack) {
        this.rack = rack;
    }

    ArrayList<Byte> getRack() {
        return rack;
    }

    boolean hasWon() {
        return rack.isEmpty();
    }

    void addPiece(byte newPiece) {
        rack.add(newPiece);
    }

    void removePiece(int index) {
        rack.remove(index);
    }

    void removePiece(byte pieceType) {
        for (int i = 0; i < rack.size(); i++) {
            if (rack.get(i) == pieceType) {
                rack.remove(i);
                return;
            }
        }
    }
}
