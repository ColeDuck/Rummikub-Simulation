public class Piece {

    // xx 0000 00
    // The first 2 LSB represent the colour
    // The next 4 bits represent the value
    // The final 2 bits are useless
    public static final int BLACK = 0;
    public static final int RED = 1;
    public static final int GREEN = 2;
    public static final int YELLOW = 3;
    public static final int JOKER = 14;
    public static final byte INVALID = (byte)0xFF;

    public static int getColour(byte piece) {
        return piece & 3; // extract final 2 bits
    }

    public static int getValue(byte piece) {
        return piece >> 2; // extract 4 middle bits
    }

    public static boolean isJoker(byte piece) {
        return (getValue(piece) == JOKER);
    }
    public static byte makePiece(int colour, int value) {
        return (byte)(colour + (value << 2));
    }
    public static byte decodeString(String s) {
        if (s.length() < 2 || s.length() > 4) return INVALID;
        char colour = s.charAt(0);
        if (!(colour == 'b' || colour == 'y' || colour == 'r' || colour == 'g')) return INVALID;

        String valueString = s.substring(1);
        int value;
        try {
            value = Integer.parseInt(valueString);
        } catch (Exception e) {
            if (valueString.equals("J") || valueString.equals("j")) value = Piece.JOKER;
            else return INVALID;
        }

        if (value < 1 || value > 14) return INVALID;

        int colourInt = 0;
        if (colour == 'b') colourInt = Piece.BLACK;
        if (colour == 'r') colourInt = Piece.RED;
        if (colour == 'g') colourInt = Piece.GREEN;
        if (colour == 'y') colourInt = Piece.YELLOW;

        // We have a valid thing :D
        return makePiece(colourInt, value);
    }

    public static String toString(byte piece) {
        String ending = "\u001B[0m";

        int colour = getColour(piece);
        int value = getValue(piece);

        // This colour code thing is very weird!
        // Luckily, it just so happens that the first four codes perfectly match with the colours of rummikub, so no if statements needed!
        // 30 = black, 31 = red, 32 = green, 33 = yellow
        String start = "\u001B[" + (30 + colour) + "m";

        String toReturn;

        if (value == JOKER) {
            toReturn = start + "J" + ending;
        } else {
            toReturn = start + value + ending;
        }

        return toReturn;
    }
}
