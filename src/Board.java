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
        for (int twice = 0; twice < 2; twice++) {
            for (int i = 1; i <= 13; i++) {
                for (int j = 0; j < 4; j++) {
                    byte newPiece = Piece.makePiece(j, i);
                    pool[((i-1)*4)+j + (twice*52)] = newPiece;
                }
            }
        }

        pool[102] = Piece.makePiece(Piece.BLACK, Piece.JOKER);
        pool[103] = Piece.makePiece(Piece.RED, Piece.JOKER);

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
        Player playerCopy = new Player(player.getName(), player.getRack());
        Board boardCopy = this.copy();

        // -1 because indexes are off by 1 because index 0 is the players rack
        // +1 because you can index the size of the array and that will add a new combo
        // Validate that we are accessing a combo that actually exists
        if (move.getFromIndex() + 1 > (combos.size() + 1) || move.getFromIndex() < 0)  return false;
        if (move.getToIndex() + 1 > (combos.size() + 1) || move.getToIndex() < 0)    return false;

        // Get the combo that we are taking from. If index 0, we are accessing the players rack
        if (move.getFromIndex() == 0) {
            if (!player.getRack().contains(move.getFromPiece())) return false;
            player.removePiece(move.getFromPiece());
        } else {
            ArrayList<Byte> from = combos.get((int)move.getFromIndex()-1);
            if (!from.contains(move.getFromPiece())) return false;
            Combo.removeFromCombo(combos, move.getFromIndex()-1, move.getFromPiece());
        }

        // If moveToIndex is equal to the length, then that means we are adding a new combo (and therefore don't need to do any validation)
        if (move.getToIndex() == Move.NEW) {
            // No need to validate anything here :)
            ArrayList<Byte> toAdd = new ArrayList<>();
            toAdd.add(move.getFromPiece());
            combos.add(toAdd);
            return true;
        }

        // Otherwise, try to add to existing combo
        if (Combo.addToCombo(combos, move.getToIndex()-1, move.getFromPiece(), move.isLeft())) return true;

        // Oh, no! Our move is invalid! Set player and board back to the initial state
        this.combos = boardCopy.combos;
        player.setRack(playerCopy.getRack());
        return false;
    }

    public void restoreToPreviousState(Board previous) {
        combos = previous.combos;
        pool = previous.pool;
        poolIndex = previous.poolIndex;
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
        sb.append("board: ");

        if (combos.isEmpty()) return sb.toString();
        for (int i = 0; i < combos.size(); i++) {
            sb.append(i+1);
            sb.append(": ");
            sb.append("{");

            ArrayList<Byte> combo = combos.get(i);
            for (Byte b : combo) {
                sb.append(Piece.toString(b));
                sb.append(",");
            }
            sb.setCharAt(sb.length()-1, '}');
            sb.append(", ");
        }

        sb.setCharAt(sb.length()-2, ' ');

        return sb.toString();
    }

}
