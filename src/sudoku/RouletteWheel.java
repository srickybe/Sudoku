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
public class RouletteWheel extends Selection {

    private Chromosome[] chrs;
    private double sumFits;
    private double[] cumulFits;
    private final Fitness fitness;

    public RouletteWheel(Fitness fitness) {
        this.fitness = fitness;
    }

    @Override
    public void setPopulation(Chromosome[] chrs) {
        this.chrs = chrs;
        this.cumulFits = new double[chrs.length];
        computeCumulativeFitnesses();
    }

    @Override
    public Chromosome select() {
        double random = Util.randomDouble();

        for (int index = 0; index < cumulFits.length; ++index) {
            if (cumulFits[index] >= random) {
                return chrs[index];
            }
        }

        return chrs[chrs.length - 1];
    }

    private void computeCumulativeFitnesses() {
        computeFitnesses();
        
        if (sumFits == 0.0) {
            throw new UnsupportedOperationException();
        }
        
        cumulFits[0] = chrs[0].getFitness() / sumFits;

        for (int i = 1; i < chrs.length; ++i) {
            cumulFits[i] = cumulFits[i - 1] + chrs[i].getFitness() / sumFits;
        }
    }

    private void computeFitnesses() {
        sumFits = 0.0;

        for (int i = 0; i < chrs.length; ++i) {
            chrs[i].setFitness(fitness.evaluate(chrs[i]));
            sumFits += chrs[i].getFitness();
        }
    }
}
