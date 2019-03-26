package Sudoku;

import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JLabel;

public class Square extends JTextField{

    private Font fnt = new Font("", Font.BOLD, 30);
    private int rownr, colnr;

    
    // Konstruktor
    Square(int row, int col) {
        
        rownr = row;
        colnr = col;
        setColumns(3);
        setFont(fnt);
        setAlignmentX(CENTER);
        setHorizontalAlignment(JLabel.CENTER);
        super.setFocusTraversalKeysEnabled(false);
        }

    public void setEnabledd(boolean b){
        setEnabled(b);
    }

    public void setEditablee(boolean b){
        setEditable(b);
    }
    //retunerar en squares koordinater
    public int[] getSquare() {
        int[] arr = new int[2];
        arr[0] = rownr;
        arr[1] = colnr;
        return arr;
    }

    //Byter ut Stringen i squaren
    public void setSquare(String n) {
        if (n.equals("0")){
            n = "";
        }
        setText(n);
        
    }
}
