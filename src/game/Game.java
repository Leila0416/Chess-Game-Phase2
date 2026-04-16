package game;

import java.util.Scanner;
import board.Board;
import model.Color;
import model.Player;
import model.Position;
import pieces.Piece;
import utils.ChessUtils;

public class Game {
    private Board board;
    private Player whitePlayer;
    private Player blackPlayer;
    private Color currentTurn;

    public Game() {
        board = new Board();
        whitePlayer = new Player(Color.WHITE);
        blackPlayer = new Player(Color.BLACK);
        currentTurn = Color.WHITE;
    }

    public void play() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            board.display();
            System.out.println(currentTurn + "'s turn.");
            System.out.print("Enter move (E2 E4) or QUIT: ");

            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("QUIT")) {
                running = false;
                System.out.println("Game ended.");
                continue;
            }

            if (!ChessUtils.isValidMoveFormat(input)) {
                System.out.println("Invalid format.");
                continue;
            }

            String[] parts = input.split(" ");
            Position from = Position.fromChessNotation(parts[0]);
            Position to = Position.fromChessNotation(parts[1]);

            Piece piece = board.getPiece(from);

            if (piece == null) {
                System.out.println("No piece there.");
                continue;
            }

            if (piece.getColor() != currentTurn) {
                System.out.println("Not your piece.");
                continue;
            }

            board.movePiece(from, to);

            currentTurn = (currentTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;
        }

        scanner.close();
    }
}
