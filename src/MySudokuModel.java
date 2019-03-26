package Sudoku;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;

public class MySudokuModel implements SudokuModel {

// Instansvariabler
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private static int[][] sudokuArray;
    private static int[][] sudokuClearArray = new int[9][9];
    public static boolean setBoardActive = false;
    public static boolean blankBoard = true;

    public MySudokuModel() {
        clear();
    }

    public MySudokuModel(SudokuModel a) {
        a.setBoard(toString());
    }

    //retunerar den aktiella modellens som en string med enbart siffror
    @Override
    public String toString() {
        return sudString().replace(",", "").replace("\n", "").replace("[", "").replace(" ", "").replace("]", "");
    }

// Retunerar en kopia av den aktuella modellen
    public int[][] clones() {
        int[][] ny = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                ny[i][j] = getBoard(i, j);
            }
        }
        return ny;
    }

// Retunerar den aktuella modellen som en String
    public String sudString() {
        return Arrays.deepToString(clones());
    }

// Sätter det sudokut som modellen ska få när clears() anropas
    public static void setClear() {
        for (int i = 0; i < 9; i++) {
            System.arraycopy(sudokuArray[i], 0, sudokuClearArray[i], 0, 9);
        }
    }

// Ändrar alla siffror till 0. 
    @Override
    public void clear() {
        int[][] oldBoard = sudokuArray;
        sudokuArray = new int[9][9];
        int[][] newBoard = sudokuArray;
        pcs.firePropertyChange("clear", oldBoard, newBoard);
    }

    //Ändrar tillbaka alla ändringsbara siffror till 0. Övriga siffor är oförändrade
    public void clears() {
        int[][] oldBoard = sudokuArray;
        String sudda = Arrays.deepToString(sudokuClearArray);
        sudda = sudda.replace("[", "").replace(" ", "").replace(",", "").replace("]", "");
        setBoard(sudda);
        int[][] newBoard = sudokuArray;
    }

//Stoppar in värdet i val på positionen (row,col)
    @Override
    public void setBoard(int row, int col, int val) {
        int[][] oldBoard = clones();
        int oldVal = getBoard(row, col);
        sudokuArray[row][col] = val;
        int[][] newBoard = clones();
        if (isSolved()) {
            pcs.firePropertyChange("solvedBoard", oldBoard, newBoard);//fulfixad med 0,1. old/newBoard används ändå aldrig
        }
        if (!setBoardActive) {
            pcs.fireIndexedPropertyChange("setCell", (row * 9 + col), oldVal, val);
        }
    }

    /* Sätter hela brädet med en String om 81 tecken (tar bort radbyten och
    gör punkter till nollor) */
    @Override
    public void setBoard(String s) {
        int[][] oldBoard = sudokuArray;
        s = s.replace(".", "0").replace("\n", "");
        int steg = 0;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int n = Integer.parseInt(s.substring(steg, steg + 1));
                setBoard(row, col, n);
                steg++;
            }
        }
        int[][] newBoard = sudokuArray;
        pcs.firePropertyChange("setBoard", oldBoard, newBoard);
        setBoardActive = false;
    }

// Retunerar värdet på position (row,col)
    @Override
    public int getBoard(int row, int col) {
        return sudokuArray[row][col];
    }

// Retunerar hela aktiva sudokut som en String
    @Override
    public String getBoard() {
        String ut = "";
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {

                int n = getBoard(r, c);
                ut = ut + n;
            }
        }
        return ut;
    }

// Kontrollerar att ett värde val får placeras på position (row,col)
    @Override
    public boolean isLegal(int row, int col, int val) {
        if (val == 0 || getBoard(row, col) == val) {

            return true;
        }

        // Arrayer som motsvarar positionens rad, kolumn och ruta
        int[] testRow = getRow(row);
        int[] testCol = getCol(col);
        int[] testBlock = getBlock(row, col);


        /* Testar varje element ur rad-, col- och rut-arrayerna med invärdet
         * Är värdet redan existerande i någon av arrayerna är det inte legalt
         * Existerar inte värdet är det legalt
         */
        for (int i = 0; i < 9; i++) {
            if (testRow[i] == val) {
                return false;
            }
            if (testBlock[i] == val) {
                return false;
            }
            if (testCol[i] == val) {
                return false;
            }
        }
        return true;

    }


    /* Kontrollerar ifall brädet är löst genom att kolla om alla siffror är legala
     * Samt att ingen av dem är 0
     */
    public boolean isSolved() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!isLegal(i, j, getBoard(i, j)) || (getBoard(i, j) == 0)) {
                    return false;
                }
            }
        }
        return true;
    }

    /* Löser sudokut rekursivt genom att först plocka ut ett index
     *  där det finns så få nollor som möjligt och stoppar sedan in ett så lågt värde
     *  som det går. Ifall något senare blir fel hoppar det tillbaka ett steg och 
     *  prövar nästa legala värde. Finner den ingen lösning returnerar den false,
     *  annars retuneras true
     */
    @Override
    public boolean solve() {

        int[] pos = getSolveIndex();
        int row = pos[0];
        int col = pos[1];

        if ((pos[0] == -1 || pos[1] == -1) && isSolved()) {

            return true;
        }

        for (int i = 1; i <= 9; i++) {

            if (isLegal(row, col, i)) {
                setBoard(row, col, i);
                if (solve()) {

                    return true;
                }
                setBoard(row, col, 0);
            }
        }
        return false;
    }

    /* Använder solve(), men sparar en kopia av den första lösningen solve()
     * finner. Körs sedan igen men godtar inte samma lösning som den första.
     * Finns det en annan lösning än den första retuneras true, 
     * annars var lösningen unik (löser 2 sudokun, därav solve2)
     */
    public boolean solve2() {

        int[] pos;
        int[][] original;
        int[][] copy;
        original = clones();
        solve();
        copy = clones();
        sudokuArray = original;

        pos = getSolveIndex();
        int row = pos[0];
        int col = pos[1];

        if (pos[0] == -1 || pos[1] == -1) {
            return true;
        }

        for (int i = 1; i <= 9; i++) {

            if (isLegal(row, col, i)) {
                setBoard(row, col, i);
                if (solve() && sudokuArray != copy) {
                    return true;
                }
            }
        }

        return false;
    }

    // Kontrollerar om sudokut bara har en lösning. Om ja retuneras true
    @Override
    public boolean isUnique() {
        int[][] copy = new int[9][9];

        if (solve2()) {
            sudokuArray = copy;
            return false;
        }
        sudokuArray = copy;
        return true;
    }

    // Kontrollerar om sudokut går att lösa på något vis. Om ja retuneras true
    @Override
    public boolean isSolvable() {

        int[][] copy;
        copy = clones();

        if (solve()) {
            sudokuArray = copy;
            return true;
        }
        sudokuArray = copy;
        return false;
    }

    /* Plockar ut den koordinat som har minst antal nollor 
     * i sin rad, kolomn. Finner den ingen sådan kooridnat 
     * retuneras [-1, -1]
     */
    public int[] getSolveIndex() {

        int[][] scopy = clones();
        int minZ = 10;
        int[] trow = new int[9];
        int rowIndex = -1;

        for (int y = 0; y < 9; y++) {
            int[] nrow = getRow(y);
            int x = countZeros(nrow);

            if (x < minZ && x != 0) {
                minZ = x;
                trow = nrow;
                rowIndex = y;
            }
        }
        int colIndex = -1;
        int minZ2 = 10;
        for (int i = 0; i < 9; i++) {
            if (trow[i] == 0) {
                int cz = countZeros(getCol(i));

                if (cz < minZ2 && cz != 0) {
                    minZ2 = cz;
                    colIndex = i;
                }
            }
        }
        int[] temp = new int[2];
        temp[0] = rowIndex;
        temp[1] = colIndex;
        return temp;
    }

    //Räknar antalet nollor i en array
    public static int countZeros(int[] in) {
        int nollor = 0;
        for (int y = 0; y < in.length; y++) {
            if (in[y] == 0) {
                nollor++;
            }
        }
        return nollor;
    }

    // Retunerar den rad som koordinaten (row,(col)) befinner sig i
    public int[] getRow(int row) {
        int[] testRow = new int[9];
        for (int a = 0; a < 9; a++) {
            testRow[a] = getBoard(row, a);
        }
        return testRow;
    }
    // Retunerar den kolomn som koordinaten ((row),col) befinner sig i

    public int[] getCol(int col) {
        int[] testCol = new int[9];
        for (int a = 0; a < 9; a++) {
            testCol[a] = getBoard(a, col);
        }
        return testCol;
    }

    // Retunerar det block som koortinaten (row,col) befinner sig i
    public int[] getBlock(int row, int col) {
        int[] testBlock = new int[9];
        int c = 0;

        //Ändrar invärdet så for-looparna får rätt startvärden
        if (row < 3) {
            row = 0;
        } else if (row > 5) {
            row = 6;
        } else {
            row = 3;
        }

        if (col < 3) {
            col = 0;
        } else if (col > 5) {
            col = 6;
        } else {
            col = 3;
        }

        for (int a = row; a < row + 3; a++) {

            for (int b = col; b < col + 3; b++) {

                testBlock[c] = getBoard(a, b);
                c++;
            }
        }
        return testBlock;
    }

    // Lyssnare för ändringar
    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

}
