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
public class Possibilities {
    private final ArrayList<Integer> possible;

    public Possibilities() {
        possible = new ArrayList<>();
    }
    
    public void add(int poss) {
        possible.add(poss);
    }

    @Override
    public String toString() {
        return "Possibilities{" + "possible=" + possible + '}';
    }
}
