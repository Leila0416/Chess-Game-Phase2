package board;

import model.Color;
import model.Position;
import pieces.*;

public class Board {
    private Piece[][] grid;

    public Board() {
        grid = new Piece[8][8];
        initializeBoard();
    }

    public void initializeBoard() {
        for (int col = 0; col < 8; col++) {
            grid[6][col] = new Pawn(Color.WHITE, new Position(6, col));
            grid[1][col] = new Pawn(Color.BLACK, new Position(1, col));
        }

        grid[7][0] = new Rook(Color.WHITE, new Position(7, 0));
        grid[7][7] = new Rook(Color.WHITE, new Position(7, 7));
        grid[0][0] = new Rook(Color.BLACK, new Position(0, 0));
        grid[0][7] = new Rook(Color.BLACK, new Position(0, 7));

        grid[7][1] = new Knight(Color.WHITE, new Position(7, 1));
        grid[7][6] = new Knight(Color.WHITE, new Position(7, 6));
        grid[0][1] = new Knight(Color.BLACK, new Position(0, 1));
        grid[0][6] = new Knight(Color.BLACK, new Position(0, 6));

        grid[7][2] = new Bishop(Color.WHITE, new Position(7, 2));
        grid[7][5] = new Bishop(Color.WHITE, new Position(7, 5));
        grid[0][2] = new Bishop(Color.BLACK, new Position(0, 2));
        grid[0][5] = new Bishop(Color.BLACK, new Position(0, 5));

        grid[7][3] = new Queen(Color.WHITE, new Position(7, 3));
        grid[0][3] = new Queen(Color.BLACK, new Position(0, 3));

        grid[7][4] = new King(Color.WHITE, new Position(7, 4));
        grid[0][4] = new King(Color.BLACK, new Position(0, 4));
    }

    public Piece getPiece(Position position) {
        return grid[position.getRow()][position.getCol()];
    }

    public void movePiece(Position from, Position to) {
        Piece piece = getPiece(from);
        if (piece != null) {
            grid[to.getRow()][to.getCol()] = piece;
            grid[from.getRow()][from.getCol()] = null;
            piece.setPosition(to);
        }
    }

    public boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public void display() {
        System.out.println("   A  B  C  D  E  F  G  H");
        for (int row = 0; row < 8; row++) {
            System.out.print((8 - row) + " ");
            for (int col = 0; col < 8; col++) {
                if (grid[row][col] == null) {
                    System.out.print(" ##");
                } else {
                    System.out.print(" " + grid[row][col].getSymbol());
                }
            }
            System.out.println();
        }
    }
}
