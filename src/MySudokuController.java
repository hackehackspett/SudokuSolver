package Sudoku;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class MySudokuController extends JPanel implements SudokuController {
    

    private JButton nnew; 
    private JButton clear;
    private JButton solve;
    private SudokuModel mod;
    
    
    public MySudokuController(SudokuModel a){
        createPanel();
        mod = a;
    }
    
    public void createPanel(){
        solve  = new JButton("Solve");
        nnew   = new JButton("New");
        clear  = new JButton("Clear");
        

        nnew = nnew();
        solve = solve();
        clear = clear();
        
        add(nnew);
        add(clear);
        add(solve);
    }
    
    
        public JButton nnew(){
        // Ger properties till knappen "new" och placerar de i rutan
        nnew.setPreferredSize(new Dimension(131, 40));
        nnew.addActionListener(new ActionListener() {

            @Override
            
            //Filväljare
            public void actionPerformed(ActionEvent e) {

                JFileChooser ny = new JFileChooser();
                int nyint = ny.showOpenDialog(nnew);
                if (nyint == JFileChooser.APPROVE_OPTION) {
                    String path = ny.getSelectedFile().getAbsolutePath();
                    BufferedReader infil;
                    try {
                        infil = new BufferedReader(new FileReader(path));
                        Scanner sc = new Scanner(infil);
                        String namnba = "";
                        while (sc.hasNext()) {
                            namnba = namnba + sc.next();

                        }
                        
                        //Fulfix för att se till att inputet är 81 tecken
                        namnba = namnba.replace("\n", "");
                        if (namnba.length() != 81){
                            throw new FileNotFoundException();
                        }
                        
                        //Ser till att rätt squares blir editerbara samt att clear/solve
                        MySudokuView.sud.blankBoard = false;
                        MySudokuView.sud.setBoard(namnba);
                        MySudokuView.sud.setClear();
                        for(int row = 0; row < 9; row ++){
                            for (int col = 0; col < 9; col++){
                                if(MySudokuView.sud.getBoard(row, col) != 0){
                                    MySudokuView.sqArray[row][col].setEditablee(false);
                                    MySudokuView.sqArray[row][col].setEnabledd(false);
                                }
                            }
                        }
                        
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(nnew, "Kan inte öppna");
                    }
                   

                    
                }          

            }
        });
        return nnew;
}
                
        
        public JButton clear(){
        // Ger properties till knappen "Clear" och retunerar den
        clear.setPreferredSize(new Dimension(131, 40));
        clear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                MySudokuView.sud.clears();
                for (int i = 0; i < 9; i++){
                    for (int j = 0; j < 9; j++){
                        if(MySudokuView.sud.getBoard(i, j) != 0){
                        MySudokuView.sqArray[i][j].setEditablee(false);
                        MySudokuView.sqArray[i][j].setEnabledd(false);
                        }
                        else{
                        MySudokuView.sqArray[i][j].setEditablee(true);
                        MySudokuView.sqArray[i][j].setEnabledd(true);
                        }
                    }
                }
            }
        });
        return clear;
        }
        
        // Ger properies till knappen "Solve" och retunerar den
        
        public JButton solve(){
        solve.setPreferredSize(new Dimension(131, 40));
        solve.addActionListener(new ActionListener() {

            
            @Override
            public void actionPerformed(ActionEvent e) {

                MySudokuModel.setBoardActive = true;
                
                //Om blank från början ta allt som är inskrivet, annars ta bort allt som inte var vid new
                if(!MySudokuView.sud.blankBoard){
                MySudokuView.sud.clears();    
                }
                if (!MySudokuView.sud.solve()){
                    JOptionPane.showMessageDialog(solve, "This sudoku is completly unsolvable!");
                }
                for (int i = 0; i < 9; i++){
                    for (int j = 0; j < 9; j++){
                        if(MySudokuView.sud.getBoard(i, j) != 0){
                        MySudokuView.sqArray[i][j].setEditablee(false);
                        MySudokuView.sqArray[i][j].setEnabledd(false);
                        }
                    }
                }
            }
        });
        return solve;
        }


    @Override
    public  boolean input(int row, int col, char value) {
        if (MySudokuView.sud.isLegal(row, col, (int)value)){
                return true;
                
        
    }
    return false;
    
    }}
        

