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
public interface Chromosome extends Comparable<Chromosome> {

    public Chromosome copy();

    public boolean mutate(double mutationRate);

    public boolean crossOver(Chromosome chr, double crossOverRate);

    public double getFitness();
    
    public void computeFitness();
    
    public boolean isSolution();
}