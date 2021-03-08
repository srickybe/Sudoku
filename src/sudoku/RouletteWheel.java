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
    private double totalFitness;
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
        double r = Util.randomDouble();

        for (int index = 0; index < cumulFits.length; ++index) {
            if (cumulFits[index] >= r) {
                return chrs[index];
            }
        }

        return chrs[chrs.length - 1];
    }

    private void computeCumulativeFitnesses() {
        computeFitnesses();
        cumulFits[0] = chrs[0].getFitness() / totalFitness;

        for (int i = 1; i < chrs.length; ++i) {
            cumulFits[i] = cumulFits[i - 1] + chrs[i].getFitness() / totalFitness;
        }
    }

    private void computeFitnesses() {
        totalFitness = 0.0;

        for (int i = 0; i < chrs.length; ++i) {
            chrs[i].setFitness(fitness.evaluate(chrs[i]));
            totalFitness += chrs[i].getFitness();
        }
    }
}
