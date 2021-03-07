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

    private final Element [][] table;
    private final Container[] rows;
    private final Container[] columns;
    private final Container[] blocks;
    private final ArrayList<Coordinates> empty;
    private final Possibilities[][] possible;
    private static final int SIZE = 9;
    //private static final int LENGTH = SIZE * SIZE;
    
    public Sudoku(int [][] vals) {
        empty = new ArrayList<>();
        possible = new Possibilities[SIZE][SIZE];
        table = new Element[SIZE][];
        
        for (int i = 0; i < SIZE; ++i) {
            table[i] = new Element[SIZE];
            
            for (int j = 0; j < SIZE; ++j) {
                table[i][j] = new Element(vals[i][j]);
                
                if (vals[i][j] == 0) {
                    empty.add(new Coordinates(i, j));
                }
            }
        }
        
        rows = new Container[SIZE];
        
        for (int i = 0; i < SIZE; ++i) {
            rows[i] = new Container();
            
            for (int j = 0; j < SIZE; ++j) {
                if(rows[i].add(table[i][j].getValue())) {
                    
                } else {
                    throw new UnsupportedOperationException();
                }
            }
        }
        
        columns = new Container[SIZE];
        
        for (int i = 0; i < SIZE; ++i) {
            columns[i] = new Container();
            
            for (int j = 0; j < SIZE; ++j) {
                if (columns[i].add(table[j][i].getValue())) {
                    
                } else {
                    throw new UnsupportedOperationException();
                }
            }
        }
        
        blocks = new Container[SIZE];
        
        for (int i = 0; i < SIZE; ++i) {
            blocks[i] = new Container();
            
            int r_start = (i / 3) * 3;
            int r_end = r_start + 3;
            int c_start = (i % 3) * 3;
            int c_end = c_start + 3;
            
            for (int m = r_start; m < r_end; ++m) {
                for (int n = c_start; n < c_end; ++n) {
                    if (blocks[i].add(table[m][n].getValue())) {
                        
                    } else {
                        throw new UnsupportedOperationException();
                    }
                }
            }
        }
        
        initializePossible();
    }
    
    public final void initializePossible() {
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                if (table[i][j].getValue() == 0) {
                    for (int k = 1; k < 10; ++k) {
                        if (table[i][j].isPossible(k)) {
                            possible[i][j].add(k);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Sudoku{" + "table=" + table + '}';
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int [][] values = {
            {0, 0, 0,   1, 0, 5,    0, 0, 0},
            {1, 4, 0,   0, 0, 0,    6, 7, 0},
            {0, 8, 0,   0, 0, 2,    4, 0, 0},
            {0, 6, 3,   0, 7, 0,    0, 1, 0},
            {9, 0, 0,   0, 0, 0,    0, 0, 3},
            {0, 1, 0,   0, 9, 0,    5, 2, 0},
            {0, 0, 7,   2, 0, 0,    0, 8, 0},
            {0, 2, 6,   0, 0, 0,    0, 3, 5},
            {0, 0, 0,   4, 0, 9,    0, 0, 0}
        };
        
        Sudoku sdk = new Sudoku(values);
        System.out.println("sdk = " + sdk);
    }
    
}
