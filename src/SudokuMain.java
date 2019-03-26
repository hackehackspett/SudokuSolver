package Sudoku;

import java.awt.*;
import javax.swing.*;
/**
 *
 * @author Kevin Rasku
 */
public class SudokuMain extends JFrame {

    public static void main(String[] arg) {
        new SudokuMain();
    }

    public SudokuMain() {
        SudokuModel model = new MySudokuModel();
        MySudokuController ctrl = new MySudokuController(model);
        MySudokuView view = new MySudokuView(model, ctrl);
        add(view, BorderLayout.CENTER);
        add(ctrl, BorderLayout.SOUTH);
        setSize(420, 420);
        setLocationRelativeTo(null); // centrera
        setVisible(true);
        setResizable(false);
        setTitle("SudokuSolver");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

}
