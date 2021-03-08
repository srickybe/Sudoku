/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

/**
 *
 * @author ricky
 */
public abstract class Selection {
    public abstract void setPopulation(Chromosome[] chrs);
    public abstract Chromosome select();
}
