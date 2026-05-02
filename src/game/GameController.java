package game;

package game;

import board.Board;
import model.Color;
import model.Position;
import pieces.Piece;

public class GameController {
    private Board board;
    private Color currentTurn;

    public GameController() {
        board = new Board();
        currentTurn = Color.WHITE;
    }

    public boolean makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        Position from = new Position(fromRow, fromCol);
        Position to = new Position(toRow, toCol);

        Piece piece = board.getPiece(from);

        if (piece == null) {
            return false;
        }

        if (piece.getColor() != currentTurn) {
            return false;
        }

        Piece target = board.getPiece(to);

        if (target != null && target.getColor() == currentTurn) {
            return false;
        }

        if (!piece.possibleMoves(board).contains(to)) {
            return false;
        }

        board.movePiece(from, to);
        switchTurn();
        return true;
    }

    private void switchTurn() {
        currentTurn = currentTurn == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    public Board getBoard() {
        return board;
    }

    public Color getCurrentTurn() {
        return currentTurn;
    }

    public void resetGame() {
        board = new Board();
        currentTurn = Color.WHITE;
    }
}