import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class Player {

    private ArrayList<Byte> rack;
    private String name;
    private int score = 0;
    private SortType lastSort = SortType.NULL;

    public Player(String name) {
        this.name = name;
        this.rack = new ArrayList<>();
    }
    public Player(String name, ArrayList<Byte> rack) {
        this.name = name;
        this.rack = rack;
    }

    ArrayList<Byte> getRack() {
        return rack;
    }

    public void addScore(int toAdd) {
        score += toAdd;
    }

    public boolean hasWon() {
        return rack.isEmpty();
    }

    public void addPiece(byte newPiece) {
        rack.add(newPiece);
        sortRack(lastSort);
    }

    public void removePiece(int index) {
        rack.remove(index);
    }

    public void resetRack() {
        rack = new ArrayList<>();
    }

    void removePiece(byte pieceType) {
        for (int i = 0; i < rack.size(); i++) {
            if (rack.get(i) == pieceType) {
                rack.remove(i);
                return;
            }
        }
    }

    public int totalRack() {
        int sum = 0;
        for (byte b : rack) {
            sum += Piece.getValue(b);
        }
        return sum;
    }

    public String getName() {
        return name;
    }

    public void setRack(ArrayList<Byte> arr) {
        this.rack = arr;
        sortRack(lastSort);
    }

    public void sortRack(SortType sortType) {
        lastSort = sortType;
        Comparator<Byte> colour = (a, b) -> Piece.getColour(a) - Piece.getColour(b);
        Comparator<Byte> value = ((a,b) -> Piece.getValue(a) - Piece.getValue(b));

        if (sortType == SortType.COLOUR) {
            rack = new ArrayList<>(rack.stream()
                    .sorted(colour.thenComparing(value))
                    .collect(Collectors.toList()));

        } else if (sortType == SortType.VALUE) {
            rack = new ArrayList<>(rack.stream()
                    .sorted(value.thenComparing(colour))
                    .collect(Collectors.toList()));

        }
    }

    public Player deepCopy() {
        ArrayList<Byte> cloneRack = new ArrayList<>();
        for (Byte b : rack) {
            cloneRack.add(b);
        }

        return new Player(name, cloneRack);
    }

    public String getRackString(boolean includeName) {
        StringBuilder sb = new StringBuilder();
        if (includeName) {
            sb.append(name);
            sb.append("'s ");
        }

        sb.append("rack: (");
        for (Byte b : rack) {
            sb.append(Piece.toString(b));
            sb.append(",");
        }
        sb.setCharAt(sb.length()-1, ')');
        return sb.toString();
    }

    public enum SortType {
        COLOUR,
        VALUE,
        NULL
    }
}
