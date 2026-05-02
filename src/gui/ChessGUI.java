package gui;

import board.Board;
import game.GameController;
import model.Position;
import pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ChessGUI extends JFrame implements ActionListener {
    private ChessSquareButton[][] squares = new ChessSquareButton[8][8];

    private int selectedRow = -1;
    private int selectedCol = -1;

    private DefaultListModel<String> moveHistoryModel;
    private JList<String> moveHistoryList;
    private JTextArea capturedWhiteArea;
    private JTextArea capturedBlackArea;
    private JLabel statusLabel;

    private GameController controller;

    public ChessGUI() {
        controller = new GameController();

        setTitle("Chess Game - Phase 3");
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeMenuBar();
        initializeMainPanels();
        updateBoardFromBackend();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu gameMenu = new JMenu("Game");

        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(e -> newGame());

        gameMenu.add(newGameItem);
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

                boardPanel.add(button);
            }
        }

        add(boardPanel, BorderLayout.CENTER);

        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.setPreferredSize(new Dimension(280, 800));

        statusLabel = new JLabel("White's turn. Select a piece.");
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
        JButton resetButton = new JButton("New Game");
        resetButton.addActionListener(e -> newGame());

        bottomPanel.add(resetButton, BorderLayout.NORTH);
        bottomPanel.add(capturedPanel, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, historyScrollPane, bottomPanel);
        splitPane.setResizeWeight(0.55);

        sidePanel.add(splitPane, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);
    }

    private void newGame() {
        controller.resetGame();

        selectedRow = -1;
        selectedCol = -1;

        moveHistoryModel.clear();
        capturedWhiteArea.setText("");
        capturedBlackArea.setText("");

        updateBoardFromBackend();
        resetBorders();

        statusLabel.setText("New game started. White's turn.");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ChessSquareButton clicked = (ChessSquareButton) e.getSource();

        int row = clicked.getRowValue();
        int col = clicked.getColValue();

        Board board = controller.getBoard();
        Piece clickedPiece = board.getPiece(new Position(row, col));

        if (selectedRow == -1) {
            if (clickedPiece != null && clickedPiece.getColor() == controller.getCurrentTurn()) {
                selectedRow = row;
                selectedCol = col;

                resetBorders();
                clicked.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));

                statusLabel.setText("Selected " + pieceName(clickedPiece) + " at " + toChessPosition(row, col));
            } else {
                statusLabel.setText("Select one of your own pieces. " + controller.getCurrentTurn() + "'s turn.");
            }
        } else {
            Piece targetPiece = board.getPiece(new Position(row, col));
            String moveText = buildMoveText(selectedRow, selectedCol, row, col, targetPiece);

            boolean moved = controller.makeMove(selectedRow, selectedCol, row, col);

            if (moved) {
                if (targetPiece != null) {
                    updateCapturedPieces(targetPiece);
                }

                moveHistoryModel.addElement(moveText);
                updateBoardFromBackend();

                statusLabel.setText(controller.getCurrentTurn() + "'s turn.");
            } else {
                statusLabel.setText("Invalid move. Try again.");
            }

            resetBorders();
            selectedRow = -1;
            selectedCol = -1;
        }
    }

    private void updateBoardFromBackend() {
        Board board = controller.getBoard();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(new Position(row, col));

                if (piece == null) {
                    squares[row][col].setText("");
                } else {
                    squares[row][col].setText(piece.getSymbol());
                }
            }
        }
    }

    private String buildMoveText(int fromRow, int fromCol, int toRow, int toCol, Piece targetPiece) {
        Piece movingPiece = controller.getBoard().getPiece(new Position(fromRow, fromCol));

        String moveText = pieceName(movingPiece) + ": "
                + toChessPosition(fromRow, fromCol)
                + " -> "
                + toChessPosition(toRow, toCol);

        if (targetPiece != null) {
            moveText += " captured " + pieceName(targetPiece);
        }

        return moveText;
    }

    private void updateCapturedPieces(Piece capturedPiece) {
        if (capturedPiece.getColor() == model.Color.WHITE) {
            capturedWhiteArea.append(capturedPiece.getSymbol() + " ");
        } else {
            capturedBlackArea.append(capturedPiece.getSymbol() + " ");
        }
    }

    private void resetBorders() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col].setBorder(UIManager.getBorder("Button.border"));
            }
        }
    }

    private String pieceName(Piece piece) {
        if (piece == null) {
            return "Piece";
        }

        String name = piece.getClass().getSimpleName();
        return piece.getColor() + " " + name;
    }

    private String toChessPosition(int row, int col) {
        char file = (char) ('A' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }
}