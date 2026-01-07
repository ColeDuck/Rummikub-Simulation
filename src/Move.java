public class Move {
    private int fromIndex;
    private byte fromPiece;
    private int toIndex;
    private boolean left;

    //"move [index] [piece] to [index] [L/R]"
    // Example: "move 5 r3 to 9 L"
    // index 0 is the players rack.

    public static Move createMove(String s) {
        String[] split = s.split(" ");
        if (!split[0].equals("move")) return null;
        if (!split[3].equals("to")) return null;

        int fromIndex;
        int toIndex;
        try {
            fromIndex = Integer.parseInt(split[1]);
            toIndex = Integer.parseInt(split[4]);
        } catch (Exception e) {
            return null;
        }

        byte piece;
        piece = Piece.decodeString(split[2]);
        if (piece == Piece.INVALID) return null;

        boolean left;
        if (split[5].equals("L")) {left = true;}
        else if (split[5].equals("R")) {left = false;}
        else return null;

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

