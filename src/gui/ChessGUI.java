package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Stack;
import game.GameController;

public class ChessGUI extends JFrame implements ActionListener {

    private ChessSquareButton[][] squares = new ChessSquareButton[8][8];
    private String[][] board = new String[8][8];

    private int selectedRow = -1;
    private int selectedCol = -1;

    private DefaultListModel<String> moveHistoryModel;
    private JList<String> moveHistoryList;

    private JTextArea capturedWhiteArea;
    private JTextArea capturedBlackArea;

    private JLabel statusLabel;

    private GameController controller;

    private Stack<GameState> undoStack = new Stack<>();

    public ChessGUI() {
        controller = new GameController();
        setTitle("Chess Game - Phase 2");
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeBoard();
        initializeMenuBar();
        initializeMainPanels();

        setLocationRelativeTo(null);
        setVisible(true);
    }
    private void updateBoardFromBackend() {
        Board board = controller.getBoard();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPiece(new Position(r, c));

                if (piece == null) {
                    squares[r][c].setText("");
                } else {
                    sqaures[r][c].setText(piece.toString());
                }
            }
        }
    }

}
private void initializeMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu gameMenu = new JMenu("Game");

        JMenuItem newGameItem = new JMenuItem("New Game");
        JMenuItem saveGameItem = new JMenuItem("Save Game");
        JMenuItem loadGameItem = new JMenuItem("Load Game");

        newGameItem.addActionListener(e -> newGame());
        saveGameItem.addActionListener(e -> saveGame());
        loadGameItem.addActionListener(e -> loadGame());

        gameMenu.add(newGameItem);
        gameMenu.add(saveGameItem);
        gameMenu.add(loadGameItem);

        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
    }

    private void initializeMainPanels() {
        JPanel boardPanel = new JPanel(new GridLayout(8, 8));

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessSquareButton button = new ChessSquareButton(row, col);
                squares[row][col] = button;

                if ((row + col) % 2 == 0) {
                    button.setBackground(new Color(240, 217, 181));
                } else {
                    button.setBackground(new Color(181, 136, 99));
                }

                button.setOpaque(true);
                button.setBorderPainted(true);
                button.setFont(new Font("Serif", Font.PLAIN, 36));
                button.setFocusPainted(false);
                button.addActionListener(this);

                updateSquare(row, col);
                boardPanel.add(button);
            }
        }

        add(boardPanel, BorderLayout.CENTER);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BorderLayout());
        sidePanel.setPreferredSize(new Dimension(280, 800));

        statusLabel = new JLabel("Select a piece, then select a destination square.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidePanel.add(statusLabel, BorderLayout.NORTH);

        moveHistoryModel = new DefaultListModel<>();
        moveHistoryList = new JList<>(moveHistoryModel);
        JScrollPane historyScrollPane = new JScrollPane(moveHistoryList);
        historyScrollPane.setBorder(BorderFactory.createTitledBorder("Move History"));

        JPanel capturedPanel = new JPanel(new GridLayout(2, 1));

        capturedWhiteArea = new JTextArea();
        capturedWhiteArea.setEditable(false);
        capturedWhiteArea.setLineWrap(true);
        capturedWhiteArea.setWrapStyleWord(true);
        JScrollPane capturedWhiteScroll = new JScrollPane(capturedWhiteArea);
        capturedWhiteScroll.setBorder(BorderFactory.createTitledBorder("White Captured"));

        capturedBlackArea = new JTextArea();
        capturedBlackArea.setEditable(false);
        capturedBlackArea.setLineWrap(true);
        capturedBlackArea.setWrapStyleWord(true);
        JScrollPane capturedBlackScroll = new JScrollPane(capturedBlackArea);
        capturedBlackScroll.setBorder(BorderFactory.createTitledBorder("Black Captured"));

        capturedPanel.add(capturedWhiteScroll);
        capturedPanel.add(capturedBlackScroll);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> undoMove());
        bottomPanel.add(undoButton, BorderLayout.NORTH);
        bottomPanel.add(capturedPanel, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, historyScrollPane, bottomPanel);
        splitPane.setResizeWeight(0.55);

        sidePanel.add(splitPane, BorderLayout.CENTER);

        add(sidePanel, BorderLayout.EAST);
    }

    private void initializeBoard() {
        // Black pieces
        board[0][0] = "bR";
        board[0][1] = "bN";
        board[0][2] = "bB";
        board[0][3] = "bQ";
        board[0][4] = "bK";
        board[0][5] = "bB";
        board[0][6] = "bN";
        board[0][7] = "bR";

        for (int i = 0; i < 8; i++) {
            board[1][i] = "bP";
        }

        // Empty squares
        for (int r = 2; r < 6; r++) {
            for (int c = 0; c < 8; c++) {
                board[r][c] = "";
            }
        }

        // White pieces
        for (int i = 0; i < 8; i++) {
            board[6][i] = "wP";
        }

        board[7][0] = "wR";
        board[7][1] = "wN";
        board[7][2] = "wB";
        board[7][3] = "wQ";
        board[7][4] = "wK";
        board[7][5] = "wB";
        board[7][6] = "wN";
        board[7][7] = "wR";
    }

    private void newGame() {
        selectedRow = -1;
        selectedCol = -1;
        undoStack.clear();
        moveHistoryModel.clear();
        capturedWhiteArea.setText("");
        capturedBlackArea.setText("");

        initializeBoard();
        refreshBoard();
        resetBorders();
        statusLabel.setText("New game started.");
    }

    private void saveGame() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                GameState currentState = createCurrentGameState();
                out.writeObject(currentState);
                JOptionPane.showMessageDialog(this, "Game saved successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving game.");
            }
        }
    }

    private void loadGame() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                GameState loadedState = (GameState) in.readObject();
                applyGameState(loadedState);
                JOptionPane.showMessageDialog(this, "Game loaded successfully.");
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Error loading game.");
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ChessSquareButton clicked = (ChessSquareButton) e.getSource();
        int row = clicked.getRowValue();
        int col = clicked.getColValue();

        if (selectedRow == -1) {
            if (!board[row][col].equals("")) {
                selectedRow = row;
                selectedCol = col;
                resetBorders();
                clicked.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
                statusLabel.setText("Selected " + pieceName(board[row][col]) + " at " + toChessPosition(row, col));
            }
        } else {
            boolean moved = controller.makeMove(selectedRow, selectedCol, row, col);

            if (moved) {
                updateBoardFromBackend();
                statusLabel.setText(controller.getCurrentTurn() + "'s turn");
            } else {
                statusLabel.setText("Invalid move");
            }

            resetBorders();
            selectedRow = -1;
            selectedCol = -1;
        }
    }

    private void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        if (fromRow == -1 || fromCol == -1) {
            return;
        }

        String movingPiece = board[fromRow][fromCol];
        String targetPiece = board[toRow][toCol];

        if (movingPiece.equals("")) {
            return;
        }

        // Save state for undo before moving
        undoStack.push(createCurrentGameState());

        String moveText = pieceName(movingPiece) + ": " + toChessPosition(fromRow, fromCol)
                + " -> " + toChessPosition(toRow, toCol);

        if (!targetPiece.equals("")) {
            moveText += " (captured " + pieceName(targetPiece) + ")";
            updateCapturedPieces(targetPiece);
        }

        // Check for king capture
        if (targetPiece.equals("bK")) {
            board[toRow][toCol] = movingPiece;
            board[fromRow][fromCol] = "";
            moveHistoryModel.addElement(moveText);
            refreshBoard();
            statusLabel.setText("White wins.");
            JOptionPane.showMessageDialog(this, "White wins! Black King captured.");
            System.exit(0);
        } else if (targetPiece.equals("wK")) {
            board[toRow][toCol] = movingPiece;
            board[fromRow][fromCol] = "";
            moveHistoryModel.addElement(moveText);
            refreshBoard();
            statusLabel.setText("Black wins.");
            JOptionPane.showMessageDialog(this, "Black wins! White King captured.");
            System.exit(0);
        }

        board[toRow][toCol] = movingPiece;
        board[fromRow][fromCol] = "";
        moveHistoryModel.addElement(moveText);
        refreshBoard();
        statusLabel.setText("Moved " + pieceName(movingPiece) + " to " + toChessPosition(toRow, toCol));
    }

    private void undoMove() {
        if (!undoStack.isEmpty()) {
            GameState previousState = undoStack.pop();
            applyGameState(previousState);
            statusLabel.setText("Last move undone.");
        } else {
            JOptionPane.showMessageDialog(this, "No moves to undo.");
        }
    }

    private void updateCapturedPieces(String capturedPiece) {
        String symbol = getPieceSymbol(capturedPiece);

        if (capturedPiece.startsWith("w")) {
            capturedWhiteArea.append(symbol + " ");
        } else if (capturedPiece.startsWith("b")) {
            capturedBlackArea.append(symbol + " ");
        }
    }

    private GameState createCurrentGameState() {
        String[][] boardCopy = new String[8][8];
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                boardCopy[r][c] = board[r][c];
            }
        }

        ArrayList<String> historyCopy = new ArrayList<>();
        for (int i = 0; i < moveHistoryModel.size(); i++) {
            historyCopy.add(moveHistoryModel.get(i));
        }

        return new GameState(
                boardCopy,
                historyCopy,
                capturedWhiteArea.getText(),
                capturedBlackArea.getText()
        );
    }

    private void applyGameState(GameState state) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                board[r][c] = state.board[r][c];
            }
        }

        moveHistoryModel.clear();
        for (String move : state.moveHistory) {
            moveHistoryModel.addElement(move);
        }

        capturedWhiteArea.setText(state.capturedWhite);
        capturedBlackArea.setText(state.capturedBlack);

        refreshBoard();
        resetBorders();
        selectedRow = -1;
        selectedCol = -1;
    }

    private void refreshBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                updateSquare(row, col);
            }
        }
    }

    private void updateSquare(int row, int col) {
        squares[row][col].setText(getPieceSymbol(board[row][col]));
    }

    private void resetBorders() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col].setBorder(UIManager.getBorder("Button.border"));
            }
        }
    }

    private String getPieceSymbol(String piece) {
        switch (piece) {
            case "wK":
                return "♔";
            case "wQ":
                return "♕";
            case "wR":
                return "♖";
            case "wB":
                return "♗";
            case "wN":
                return "♘";
            case "wP":
                return "♙";
            case "bK":
                return "♚";
            case "bQ":
                return "♛";
            case "bR":
                return "♜";
            case "bB":
                return "♝";
            case "bN":
                return "♞";
            case "bP":
                return "♟";
            default:
                return "";
        }
    }

    private String pieceName(String piece) {
        switch (piece) {
            case "wK":
            case "bK":
                return "King";
            case "wQ":
            case "bQ":
                return "Queen";
            case "wR":
            case "bR":
                return "Rook";
            case "wB":
            case "bB":
                return "Bishop";
            case "wN":
            case "bN":
                return "Knight";
            case "wP":
            case "bP":
                return "Pawn";
            default:
                return "Piece";
        }
    }

    private String toChessPosition(int row, int col) {
        char file = (char) ('A' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    private static class GameState implements Serializable {
        String[][] board;
        ArrayList<String> moveHistory;
        String capturedWhite;
        String capturedBlack;

        public GameState(String[][] board, ArrayList<String> moveHistory, String capturedWhite, String capturedBlack) {
            this.board = board;
            this.moveHistory = moveHistory;
            this.capturedWhite = capturedWhite;
            this.capturedBlack = capturedBlack;
        }
    }
}