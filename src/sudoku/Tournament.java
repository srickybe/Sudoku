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
public class Tournament extends Selector {

    private ArrayList<Chromosome> population;
    private ArrayList<Chromosome> copy;
    private final int size;

    public Tournament(int size) {
        population = null;
        copy = null;
        this.size = size;
    }

    @Override
    public Chromosome select() {
        int nChoices = Math.min(size, population.size());
        int choice = 0;

        try {
            choice = Rand.getInstance().nextInt(population.size());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(0);
        }

        Chromosome bestFit = population.get(choice);
        Double bestFitness = population.get(choice).getFitness();

        for (int j = 1; j < nChoices; ++j) {
            int index = Rand.getInstance().nextInt(population.size());
            Chromosome chr = population.get(index);
            Double fitness = population.get(index).getFitness();

            if (fitness > bestFitness) {
                bestFitness = fitness;
                bestFit = chr;
                choice = index;
            }
        }

        population.remove(choice);
        replete();

        return bestFit;
    }

    @Override
    public final void setPopulation(ArrayList<Chromosome> chromos) {
        this.population = new ArrayList<>(chromos.size());
        this.copy = new ArrayList<>(chromos.size());

        chromos.forEach(chromo -> {
            this.population.add(chromo);
            this.copy.add(chromo);
        });
    }

    private void replete() {
        if (population.isEmpty()) {
            this.copy.forEach(chromo -> {
                this.population.add(chromo);
            });
        }
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "Tournament{"
                + "population=" + population
                + ", copy=" + copy
                + ", size=" + size
                + '}';
    }
}
