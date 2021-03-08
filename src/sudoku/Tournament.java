/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author ricky
 */
public class Tournament extends Selection {

    private ArrayList<Chromosome> chrs;
    private final int size;

    public Tournament(int size) {
        chrs = null;
        this.size = size;
    }

    @Override
    public Chromosome select() {
        int nChoices = Math.min(size, chrs.size());
        int choice = Util.randomInt(chrs.size());
        Chromosome bestFit = chrs.get(choice);
        Double bestFitness = chrs.get(choice).getFitness();

        for (int j = 1; j < nChoices; ++j) {
            int index = Util.randomInt(chrs.size());
            Chromosome chr = chrs.get(index);
            Double fitness = chrs.get(index).getFitness();

            if (fitness > bestFitness) {
                bestFitness = fitness;
                bestFit = chr;
                choice = index;
            }
        }

        chrs.remove(choice);

        return bestFit;
    }

    @Override
    public final void setPopulation(
            Chromosome[] chrs
    ) {
        this.chrs = new ArrayList<>(Arrays.asList(chrs));
    }
}
