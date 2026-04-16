package pieces;

import java.util.ArrayList;
import java.util.List;
import board.Board;
import model.Color;
import model.Position;

public class Pawn extends Piece {

    public Pawn(Color color, Position position) {
        super(color, position);
    }

    @Override
    public List<Position> possibleMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int direction = (color == Color.WHITE) ? -1 : 1;

        int newRow = position.getRow() + direction;
        int col = position.getCol();

        if (board.isWithinBounds(newRow, col) &&
                board.getPiece(new Position(newRow, col)) == null) {
            moves.add(new Position(newRow, col));
        }

        return moves;
    }

    @Override
    public String getSymbol() {
        return color == Color.WHITE ? "wp" : "bp";
    }
}