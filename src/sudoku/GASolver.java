/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

import java.util.Arrays;

/**
 *
 * @author ricky
 */
public class GASolver {

    private Chromosome[] chrs;
    private final int maximumNumberOfGenerations;
    private final double crossOverRate;
    private final double mutationRate;
    private Chromosome bestFit;
    private Double maxFitness;
    private final Selection selector;

    public GASolver(
            Chromosome[] chrs,
            Fitness fitness,
            int maxGenerations,
            double crossOverRate,
            double mutationRate,
            Selection selector
    ) {
        this.maximumNumberOfGenerations = maxGenerations;
        this.crossOverRate = crossOverRate;
        this.mutationRate = mutationRate;
        this.selector = selector;
        initializePopulationWith(chrs);
    }

    private void initializePopulationWith(Chromosome[] chrs) {
        this.chrs = new Chromosome[chrs.length];

        for (int i = 0; i < chrs.length; ++i) {
            this.chrs[i] = new Chromosome(chrs[i]);
        }
    }

    public Chromosome[] getPopulation() {
        return chrs;
    }

    public Chromosome getBestFit() {
        return bestFit;
    }

    public boolean hasSolution() {
        if (bestFit != null) {
            return Double.isInfinite((double) bestFit.getFitness());
        } else {
            return false;
        }
    }

    public void evolve() {
        for (int i = 0; i < maximumNumberOfGenerations; ++i) {
            if (hasSolution()) {
                break;
            }

            selector.setPopulation(chrs);
            Chromosome[] nextGen = nextGeneration();
            setNextGeneration(nextGen);
            updateBestFit();
        }
    }

    private Chromosome[] nextGeneration() {
        if (chrs.length % 2 != 0) {
            throw new UnsupportedOperationException();
        }

        Chromosome[] children = new Chromosome[chrs.length];

        for (int i = 0; i < chrs.length / 2; ++i) {
            Chromosome parent1 = selector.select();
            Chromosome parent2 = selector.select();
            Chromosome child1 = new Chromosome(parent1);
            Chromosome child2 = new Chromosome(parent2);
            child1.crossOver(child2, crossOverRate);
            child1.mutate(mutationRate);
            child2.mutate(mutationRate);
            children[2 * i] = (child1);
            children[2 * i + 1] = (child2);
        }

        return children;
    }

    private void updateBestFit() {
        for (int i = 0; i < chrs.length; ++i) {
            if (bestFit != null) {
                if (chrs[i].getFitness() > maxFitness) {
                    maxFitness = chrs[i].getFitness();
                    bestFit = chrs[i];
                }
            } else {
                maxFitness = chrs[i].getFitness();
                bestFit = chrs[i];
            }
        }
    }

    public void sortPopulation() {
        Arrays.sort(chrs);
    }

    private void setNextGeneration(Chromosome[] children) {
        this.chrs = children;
    }

    @Override
    public String toString() {
        return "GASolver{"
                + "pairs=" + Arrays.toString(chrs)
                + ", maxGenerations=" + maximumNumberOfGenerations
                + ", crossOverRate=" + crossOverRate
                + ", mutationRate=" + mutationRate
                + ", bestFit=" + bestFit
                + ", maxFitness=" + maxFitness
                + ", selector=" + selector
                + '}';
    }

    public static void main(String[] args) {
        System.out.println("Test");
        int nChrs = 2;
        int nBases = 5;
        int maxGenerations = 1;
        double crossOverRate = 0.08;
        double mutationRate = 0.01;
        Chromosome[] chrs = new Chromosome[nChrs];

        for (int i = 0; i < chrs.length; ++i) {
            chrs[i] = Chromosome.randomChromosome(nBases);
            chrs[i].setFitness((double) i);
        }

        Tournament tournament = new Tournament(10);

        GASolver solver = new GASolver(
                chrs,
                new Fitness() {
                    @Override
                    public double evaluate(Chromosome chr) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                },
                maxGenerations,
                crossOverRate,
                mutationRate,
                tournament
        );

        System.out.println("solver = " + solver);

        if (solver.hasSolution()) {
            System.out.println("solution = " + solver.getBestFit());
        }

        solver.evolve();
    }
}
