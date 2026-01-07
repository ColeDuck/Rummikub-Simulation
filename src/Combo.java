import java.util.ArrayList;

public class Combo {
    static private final int SET = 0;
    static private final int RUN = 1;
    static private final int INVALID = 2;
    static private final int UNCLEAR = 3;

    public static boolean addToCombo(ArrayList<ArrayList<Byte>> combos, int thisIndex, byte piece, boolean left) {
        ArrayList<Byte> copyToTest = new ArrayList<>(combos.get(thisIndex));
        if (left) {
            copyToTest.addFirst(piece);
        } else {
            copyToTest.addLast(piece);
        }

        if (validateComboAndGetType(copyToTest) == INVALID) return false;
        combos.set(thisIndex, copyToTest);
        return true;
    }

    public static void removeFromCombo(ArrayList<ArrayList<Byte>> combos, int thisIndex, byte piece) {
        ArrayList<Byte> combo = combos.get(thisIndex);
        if (combo.size() == 1) {
            combos.remove(thisIndex);
            return;
        }

        int comboType = validateComboAndGetType(combo);
        if (comboType == INVALID) System.out.println("ERROR: Found invalid combo when it should be impossible");
        if (combo.size() == 2 || comboType == SET) {
            // If we are a double combo or a set, then we can just remove the piece with no fuss
            combo.remove(piece);
            return;
        }

        // If we are here then our set is a run, and we therefore need to split the set into two pieces
        ArrayList<Byte> start = new ArrayList<>(combo.subList(0, combo.indexOf(piece)));
        ArrayList<Byte> end = new ArrayList<>(combo.subList(combo.indexOf(piece)+1, combo.size()));

        // Depending on where in the array we are grabbing from, there are three cases we need to handle
        if (!start.isEmpty() && !end.isEmpty()) {
            combos.set(thisIndex, start);
            combos.add(thisIndex+1, end);
        } else if (start.isEmpty()) {
            combos.set(thisIndex, end);
        } else {
            combos.set(thisIndex, start);
        }
    }

    public static int validateComboAndGetType(ArrayList<Byte> combo) {
        if (combo.size() == 1) return INVALID;

        byte first = 0;
        boolean foundFirst = false;
        int nextPieceInRun = 0;
        int[] coloursInSet = new int[4];
        int coloursInSetTracker = 0;
        int toReturn = UNCLEAR;

        int preJokerCount = 0;

        // Basically, we find the first non joker piece.
        // Then, on the second non-joker piece, we can determine what type of combo this is
        // From there, we just keep moving on, and if a single piece doesn't fit in then we return invalid
        for (int i = 0; i < combo.size(); i++) {
            byte curr = combo.get(i);
            if (!foundFirst) {
                if (Piece.isJoker(curr)) {preJokerCount++; continue;}
                foundFirst = true;
                first = curr;
                nextPieceInRun = Piece.getValue(first) + 1;
                coloursInSet[coloursInSetTracker++] = Piece.getColour(first);
                continue;
            }

            if (Piece.isJoker(curr)) {nextPieceInRun++; continue;}

            // Check if this is a run
            if (Piece.getValue(curr) == nextPieceInRun && Piece.getColour(curr) == Piece.getColour(first)) {
                if (toReturn == SET) return INVALID;
                if (toReturn == UNCLEAR) toReturn = RUN;
                nextPieceInRun++;
                continue;
            }

            // Check if it is a set
            if (Piece.getValue(curr) == Piece.getValue(first) && !contains(coloursInSet, Piece.getColour(curr))) {
                if (toReturn == RUN) return INVALID;
                if (toReturn == UNCLEAR) toReturn = SET;
                coloursInSet[coloursInSetTracker++] = Piece.getColour(curr);
                continue;
            }

            // If it is neither than return invalid
            return INVALID;
        }

        // Two final edge cases regarding jokers on runs
        if (toReturn == RUN && (Piece.getValue(first) - preJokerCount < 0)) return INVALID;
        if (toReturn == RUN && Piece.isJoker(combo.getLast()) && nextPieceInRun >= 15) return INVALID;

        return toReturn;
    }

    static boolean contains(int[] arr, int test) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == test) return true;
        }
        return false;
    }
}
