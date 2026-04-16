package pieces;

import java.util.ArrayList;
import java.util.List;
import board.Board;
import model.Color;
import model.Position;

public class Queen extends Piece {

    public Queen(Color color, Position position) {
        super(color, position);
    }

    @Override
    public List<Position> possibleMoves(Board board) {
        return new ArrayList<>();
    }

    @Override
    public String getSymbol() {
        return color == Color.WHITE ? "wQ" : "bQ";
    }
}