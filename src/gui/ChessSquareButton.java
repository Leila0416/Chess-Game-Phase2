package gui;

import javax.swing.JButton;

public class ChessSquareButton extends JButton {
    private int row;
    private int col;

    public ChessSquareButton(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRowValue() {
        return row;
    }

    public int getColValue() {
        return col;
    }
}
