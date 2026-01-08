public class Move {
    private int fromIndex;
    private byte fromPiece;
    private int toIndex;
    private boolean left;

    public static final int NEW = Integer.MAX_VALUE;

    //"move [index] [piece] to [index] [L/R]"
    // Example: "move 5 r3 to 9 L"
    // index should be "rack" for the players rack.
    // index should be "new" to create new combo
    // L/R is not necessary if new is selected.

    public static Move createMove(String s) {
        String[] split = s.split(" ");
        if (split.length < 5) return null;

        if (!split[0].equals("move")) return null;
        if (!split[3].equals("to")) return null;

        int fromIndex;
        int toIndex;
        try {
            fromIndex = Integer.parseInt(split[1]);
        } catch (Exception e) {
            if (split[1].equals("rack")) fromIndex = 0;
            else return null;
        }

        try {
            toIndex = Integer.parseInt(split[4]);
        } catch (Exception e) {
            if (split[4].equals("new")) toIndex = NEW;
            else return null;
        }

        if (toIndex == NEW && split.length != 5) return null;
        if (toIndex == 0) return null;

        byte piece;
        piece = Piece.decodeString(split[2]);
        if (piece == Piece.INVALID) return null;

        // Default to left is none is provided
        boolean left = true;
        if (toIndex != NEW && split.length == 6) {
            if (split[5].equalsIgnoreCase("L")) {left = true;}
            else if (split[5].equalsIgnoreCase("R")) {left = false;}
            else return null;
        }

        // We have a valid string :)
        return new Move(fromIndex, piece, toIndex, left);
    }

    private Move(int fromIndex, byte fromPiece, int toIndex, boolean left) {
        this.fromPiece = fromPiece;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.left = left;
    }

    public void setFromIndex(int fromIndex) {
        this.fromIndex = fromIndex;
    }

    public void setFromPiece(byte fromPiece) {
        this.fromPiece = fromPiece;
    }

    public void setToIndex(int toIndex) {
        this.toIndex = toIndex;
    }
    public void setLeft(boolean left) {
        this.left = left;
    }

    public int getFromIndex() {
        return fromIndex;
    }

    public byte getFromPiece() {
        return fromPiece;
    }

    public int getToIndex() {
        return toIndex;
    }

    public boolean isLeft() {
        return left;
    }
}

