/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

import java.util.ArrayList;

/**
 *
 * @author ricky
 */
class TestChromo implements Chromosome {

    private final ArrayList<Integer> coefficients;
    private double fitness;

    public TestChromo(int numberOfCoefficients) {
        this.coefficients = new ArrayList<>(numberOfCoefficients);

        for (int i = 0; i < numberOfCoefficients; ++i) {
            this.coefficients.add(
                    1 + Rand.getInstance().nextInt(numberOfCoefficients)
            );
        }
        
        computeFitness();
    }

    @Override
    public Chromosome copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean mutate(double mutationRate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean crossOver(Chromosome chr, double crossOverRate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getFitness() {
        return fitness;
    }

    @Override
    public final void computeFitness() {
        fitness = 1;

        for (int coefficient : coefficients) {
            fitness *= coefficient;
        }
    }

    @Override
    public boolean isSolution() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int compareTo(Chromosome arg0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return "MyChromo{" + "coefficients=" + coefficients + '}';
    }
}
