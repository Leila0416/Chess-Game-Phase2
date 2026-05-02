package model;

import java.util.Objects;

public class Position {
    private int row;
    private int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public static Position fromChessNotation(String notation) {
        notation = notation.toUpperCase();
        int col = notation.charAt(0) - 'A';
        int row = 8 - Character.getNumericValue(notation.charAt(1));
        return new Position(row, col);
    }

    @Override
    public String toString() {
        char file = (char) ('A' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Position)) {
            return false;
        }

        Position other = (Position) obj;
        return row == other.row && col == other.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}