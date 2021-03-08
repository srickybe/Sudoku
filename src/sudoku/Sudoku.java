/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

import java.util.ArrayList;

/**
 *
 * @author Ricky
 */
public class Sudoku {

    private final ArrayList<ArrayList<Cell>> grid;
    private final ArrayList<Container> rows;
    private final ArrayList<Container> columns;
    private final ArrayList<Container> blocks;
    private final ArrayList<Coordinates> empty;
    private final ArrayList<ArrayList<Possibilities>> possible;
    private static final int SIZE = 9;

    public Sudoku(int[][] vals) {
        empty = new ArrayList<>();
        possible = new ArrayList<>();
        grid = new ArrayList<>();
        rows = new ArrayList<>();

        for (int i = 0; i < SIZE; ++i) {
            rows.add(new Container());

            for (int j = 0; j < SIZE; ++j) {
                int value = vals[i][j];

                if (value != 0) {
                    if (rows.get(i).add(value)) {

                    } else {
                        throw new UnsupportedOperationException();
                    }
                }
            }
        }

        columns = new ArrayList<>();

        for (int j = 0; j < SIZE; ++j) {
            columns.add(new Container());

            for (int i = 0; i < SIZE; ++i) {
                int value = vals[i][j];

                if (value != 0) {
                    if (columns.get(j).add(value)) {

                    } else {
                        throw new UnsupportedOperationException();
                    }
                }
            }
        }

        blocks = new ArrayList<>();

        for (int i = 0; i < SIZE; ++i) {
            blocks.add(new Container());

            int r_start = (i / 3) * 3;
            int r_end = r_start + 3;
            int c_start = (i % 3) * 3;
            int c_end = c_start + 3;

            for (int m = r_start; m < r_end; ++m) {
                for (int n = c_start; n < c_end; ++n) {
                    int value = vals[m][n];

                    if (value != 0) {
                        if (blocks.get(i).add(value)) {

                        } else {
                            throw new UnsupportedOperationException();
                        }
                    }
                }
            }
        }
        
        for (int i = 0; i < SIZE; ++i) {
            grid.add(new ArrayList<>());

            for (int j = 0; j < SIZE; ++j) {
                Cell elem = new Cell(
                        vals[i][j],
                        vals[i][j] == 0,
                        rows.get(i), 
                        columns.get(j), 
                        blocks.get((i/3) * 3 + j/3)
                );
                
                grid.get(i).add(elem);

                if (vals[i][j] == 0) {
                    empty.add(new Coordinates(i, j));
                }
            }
        }
        
        initializePossible();
    }

    public final void initializePossible() {
        for (int i = 0; i < SIZE; ++i){
            possible.add(new ArrayList<>());
            
            for (int j = 0; j < SIZE; ++j) {
                possible.get(i).add(new Possibilities());
            }
        }
        
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                if (grid.get(i).get(j).getValue() == 0) {
                    for (int k = 1; k < 10; ++k) {
                        if (grid.get(i).get(j).isPossible(k)) {
                            possible.get(i).get(j).add(k);
                        }
                    }
                }
            }
        }
    }
    
    public int numberOfEmptyCells() {
        return empty.size();
    }

    @Override
    public String toString() {
        String res = "Sudoku{" + "grid={\n";

        for (int i = 0; i < SIZE; ++i) {
            res += grid.get(i) + "\n";
        }

        res += "}" + "\nrows={" + rows + "\ncolumns={" + columns + "\nblocks={"
                + blocks + "\nempty={" + empty + "\npossible={" + possible + "\n}";

        return res;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int[][] values = {
            {0, 0, 0, 1, 0, 5, 0, 0, 0},
            {1, 4, 0, 0, 0, 0, 6, 7, 0},
            {0, 8, 0, 0, 0, 2, 4, 0, 0},
            {0, 6, 3, 0, 7, 0, 0, 1, 0},
            {9, 0, 0, 0, 0, 0, 0, 0, 3},
            {0, 1, 0, 0, 9, 0, 5, 2, 0},
            {0, 0, 7, 2, 0, 0, 0, 8, 0},
            {0, 2, 6, 0, 0, 0, 0, 3, 5},
            {0, 0, 0, 4, 0, 9, 0, 0, 0}
        };

        Sudoku sdk = new Sudoku(values);
        System.out.println("sdk = " + sdk);
        System.out.println("number of empty cells = " + 
                sdk.numberOfEmptyCells());
    }
}
