package Sudoku;

import java.awt.GridLayout;
import java.beans.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

public class MySudokuView extends JPanel implements KeyListener, PropertyChangeListener {

    // Knappar, rutor och annat viktigt
    FlowLayout flay = new FlowLayout();
    public static MySudokuModel sud = new MySudokuModel();
    public static Square[][] sqArray = new Square[9][9];
    public static MySudokuController ctrl;
    public JPanel undre;

    //Skapar 9x9 squares med unika koordinater och värdet 0 i varje
    public void createSquares() {
        int dw = 0;
        for (int a = 0; a < 9; a++) {
            for (int b = 0; b < 9; b++) {
                sqArray[a][b] = new Square(a, b);
                dw++;
            }
        }
    }

    // Skapar rutan, layouten och allt annat göttigt
    public MySudokuView(SudokuModel a, MySudokuController b) {

        sud = new MySudokuModel(a);
        ctrl = b;
        sud.addPropertyChangeListener(this);
        createSquares();

        GridLayout trextre = new GridLayout(3, 3);

        //Skapar en undre 3x3 panel som paneler med 3x3 squares kommer sättas i
        undre = new JPanel();
        undre.setLayout(trextre);
        undre.setAlignmentX(JPanel.TOP_ALIGNMENT);

        int row = 0, col = 0, step = 0;

        for (int k = 0; k < 9; k++) {
            JPanel panel = new JPanel();
            panel.setLayout(trextre);
            panel.setAlignmentX(JPanel.TOP_ALIGNMENT);

            /* Här ges alla Squares sina värden och sägs till om de får ändras
             * Skapas i grupper om 3x3
             */
            for (int j = 0; j < 9; j++) {
                Square q = sqArray[row][col];
                q.addKeyListener(this);

                if (sud.getBoard(row, col) != 0) {
                    q.setEnabledd(false);
                    q.setEditablee(false);
                    q.setText("" + (sud.getBoard(row, col)));

                }
                panel.add(q);

                // För att hålla koll på vilken rad och kolumn vi befinner oss i
                // Sista squaren i ruta 3 och 6 (och 9)
                if ((row + 1) % 3 == 0 && (col + 1) % 9 == 0) {
                    row++;
                    col = 0;
                    step = 0;
                } // Sista squaren i en ruta (som inte är ruta 3 eller 6)
                else if (step == 8) {
                    row = row - 2;
                    col++;
                    step = 0;
                } // Square 3 och 6 i en ruta
                else if ((step + 1) % 3 == 0) {
                    row++;
                    col = col - 2;
                    step++;

                    // Annars nästa steg
                } else {
                    col++;
                    step++;
                }

                // Fin svart linje! :D
                panel.setBorder(BorderFactory.createLineBorder(Color.black));
            }

            undre.setPreferredSize(new Dimension(405, 340));
            undre.add(panel);
            undre.setVisible(true);
            add(undre);
        }

        //Placerar in sudokupanelen i rutan
        //Sudoku.setLayout(flay);
        //Sudoku.add(undre);
    }

//     Målar upp sudokut igen med nya värdet/en
    // Vad som händer ifall en knapp släpps
    @Override
    public void keyReleased(KeyEvent e) {
        Square ruta = (Square) e.getSource();
        String kIn = Character.toString(e.getKeyChar());

        //Backspace
        if (kIn.equals("\b")) {
            kIn = "0";
        }

        int iUt = 0;
        int[] ruuta = ruta.getSquare();
        int row = ruuta[0];
        int col = ruuta[1];
        boolean nummer = true;
        try {
            iUt = Integer.parseInt(kIn);

        } catch (NumberFormatException t) {
            nummer = false;
        }

        // Kontrollerar korrekt input. Piper annars
        if (((ctrl.input(row, col, (char) iUt) && nummer)
                && e.getComponent().isEnabled())
                && (iUt + "").length() == 1) {

            sud.setBoard(row, col, iUt);
            ruta.setSquare(kIn);
        } else {
            Toolkit.getDefaultToolkit().beep();
            ruta.setSquare("" + sud.getBoard(row, col));
        }
        //Om sodukot är korrekt ifyllt dyker en ruta upp med text
        if (sud.isSolved()) {
            JOptionPane.showMessageDialog(ruta, "You won!");

        }

    }

    @Override
    public void keyTyped(KeyEvent e) {
        //Just here so we don't get fined  
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //Just here so we don't get fined
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        if ("setCell".equals(evt.getPropertyName())) {

            IndexedPropertyChangeEvent e = (IndexedPropertyChangeEvent) evt;
            int newV = (Integer) e.getNewValue();
            int index = (Integer) e.getIndex();
            int col = index % 9;
            int row = (index - col) / 9;

            sqArray[row][col].setSquare("" + newV);
        }
        if (MySudokuModel.setBoardActive) {

            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {

                    int val = sud.getBoard(row, col);
                    if (val != 0) {

                        sqArray[row][col].setEnabledd(false);
                        sqArray[row][col].setEditablee(false);
                    }

                    String s;
                    s = "" + (val);

                    if (s.equals("0")) {
                        s = "";
                    }
                    MySudokuView.sqArray[row][col].setSquare(s);
                }
            }
        }
        MySudokuModel.setBoardActive = false;
    }
}
