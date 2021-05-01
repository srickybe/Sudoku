/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ricky
 */

package sudoku;

import java.util.ArrayList;

public abstract class Selector {
    public abstract void setPopulation(ArrayList<Chromosome> chrs);
    public abstract Chromosome select();
}
