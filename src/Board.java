import java.util.ArrayList;

public class Board {

    // Runs
    private ArrayList<ArrayList<Byte>> combos;
    private byte[] pool;
    private byte poolIndex = 0;

    public Board(ArrayList<ArrayList<Byte>> combos, byte[] pool, byte poolIndex) {
        this.combos = combos;
        this.pool = pool;
        this.poolIndex = poolIndex;
    }
    public Board() {
        this.combos = new ArrayList<>();
        initializePool();
    }

    private final void initializePool() {
        pool = new byte[104];
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 4; j++) {
                pool[(i*4)+j] = Piece.makePiece(j, i);
            }
        }
        pool[103] = Piece.makePiece(Piece.BLACK, Piece.JOKER);
        pool[104] = Piece.makePiece(Piece.RED, Piece.JOKER);

        // Shuffle pieces
        for (int i = 0; i < 104; i++) {
            int newIndex = (int)(Math.random() * (104-i)) + i;

            // Swap
            byte temp = pool[newIndex];
            pool[newIndex] = pool[i];
            pool[i] = temp;
        }
    }

    public boolean makeMove(Move move, Player player) {
        // Make copy of player so that we can undo any illegal moves
        Player copy = new Player(player.getRack());

        // -1 because indexes are off by 1 because index 0 is the players rack
        // +1 because you can index the size of the array and that will add a new combo
        // Validate that we are accessing a combo that actuall exists
        if (move.getFromIndex() + 1 > (combos.size() + 1) || move.getFromIndex() < 0)  return false;
        if (move.getToIndex() + 1 > (combos.size() + 1) || move.getToIndex() < 0)    return false;

        // Get the combo that we are taking from
        ArrayList<Byte> from;
        if (move.getFromIndex() != 0)   from = combos.get((int)move.getFromIndex()-1);
        else                            from = player.getRack();

        // If this combo doesn't even contain the piece we are looking for, return false.
        if (!from.contains(move.getFromPiece())) return false;

        Combo.removeFromCombo(combos, move.getFromIndex(), move.getFromPiece());

        // If the moveToIndex is equal to the length, then that means we are adding a new combo (and therefore don't need to do any validation lol)
        if (move.getToIndex() == combos.size()) {
            // No need to validate anything here :)
            ArrayList<Byte> toAdd = new ArrayList<>();
            toAdd.add(move.getFromPiece());
            combos.add(toAdd);
            return true;
        }

        // Here we are trying to add a piece to an exisiting combo
        // It must be able to fit in, if it doesn't then this is an invalid move.
        ArrayList<Byte> to = combos.get(move.getToIndex());
        Combo.addToCombo(combos, move.getToIndex(), move.getFromPiece());
    }



    public Board copy() {
        return new Board(combos, pool, poolIndex);
    }

    public byte pullFromPool() {
        return pool[poolIndex++];
    }

    public boolean isLegalPosition() {
        for (ArrayList<Byte> combo : combos) {
            if (combo.size() < 3) return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ArrayList<Byte> combo : combos) {
            sb.append("{");
            for (Byte b : combo) {
                sb.append(b.toString());
                sb.append(",");
            }
            sb.replace(sb.length(), sb.length(), "}, ");
        }

        return sb.toString();
    }

}
