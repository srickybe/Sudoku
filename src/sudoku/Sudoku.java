/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

/**
 *
 * @author Ricky
 */
public class Sudoku {

    private final Element [][] table;
    private final Container[] rows;
    private final Container[] columns;
    private final Container[] blocks;
    private static final int SIZE = 9;
    private static final int LENGTH = SIZE * SIZE;
    
    public Sudoku(int [][] vals) {
        table = new Element[LENGTH][];
        
        for (int i = 0; i < LENGTH; ++i) {
            table[i] = new Element[SIZE];
            
            for (int j = 0; j < SIZE; ++j) {
                table[i][j] = new Element(vals[i][j]);
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
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        class Value {
            public int i;
            
            Value (int i) {
                this.i = i;
            }
            
            void set(int i) {
                this.i = i;
            }

            @Override
            public String toString() {
                return "Value{" + "i=" + i + '}';
            }
        }
        
        Value i1 = new Value(9);
        Value i2 = i1;
        System.out.println("i1 = " + i1);
        System.out.println("i2 = " + i2);
        i1.set(10);
        System.out.println("i1 = " + i1);
        System.out.println("i2 = " + i2);
    }
    
}