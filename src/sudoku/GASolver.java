/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

/**
 *
 * @author ricky
 */
public class GASolver implements Runnable {

    //private final Grid sdk;
    private ArrayList<Chromosome> population;
    private final int initialPopulationSize;
    private final int maximumNumberOfGenerations;
    private final double crossOverRate;
    private final double mutationRate;
    private final double selectionRate;
    private final int restartThreshold;
    private Chromosome bestFit;
    private final ArrayList<Chromosome> bestFitByGeneration;
    private final ArrayList<Double> averageFitnesses;
    private TreeSet<Chromosome> reserve;
    private Selector selector;
    private final String name;
    private int currentGenerationNumber;
    private int generationForLocalMaximumTest;
    private boolean stopped;
    private boolean verbose;

    public GASolver(
            //Grid sdk,
            ArrayList<Chromosome> chrs,
            Selector selector,
            Settings settings,
            String name
    ) {
        //this.sdk = sdk;
        this.initialPopulationSize = chrs.size();
        this.restartThreshold = settings.getRestartThreshold();
        this.bestFitByGeneration = new ArrayList<>(settings.getMaximumNumberOfGenerations());
        this.maximumNumberOfGenerations = settings.getMaximumNumberOfGenerations();
        this.currentGenerationNumber = 0;
        this.generationForLocalMaximumTest = this.restartThreshold;
        this.crossOverRate = settings.getCrossOverRate();
        this.mutationRate = settings.getMutationRate();
        this.selector = selector;
        this.selectionRate = settings.getSelectionRate();
        this.name = name;
        this.averageFitnesses = new ArrayList<>();
        setPopulation(chrs);
        computeAverageFitness();
        this.reserve = new TreeSet<>();
        this.stopped = false;
        this.verbose = true;
    }

    public void addNBestFitToReservePopulation() {
        int numberOfChromosomes = population.size() / getRestartThreshold();
        addNBestFitFromTo(numberOfChromosomes, population, reserve);

        if (sizeOfReserve() > (int) (5.0 * getInitialPopulationSize())) {
            if (isVerbose()) {
                /*System.out.println("###Thread " + name
                        + ": restartPopulation has a greater size than population");
                System.out.println("size = " + reservePopulation.size());*/
            }

            ArrayList<Chromosome> nBestFit;
            nBestFit = nBestFit(
                    getInitialPopulationSize(),
                    reserve
            );
            clearReserve();
            addToReserve(nBestFit);

            if (isVerbose()) {
                System.out.println("new size = " + sizeOfReserve());
            }
        }
    }

    public int sizeOfReserve() {
        return reserve.size();
    }

    public void clearReserve() {
        reserve.clear();
    }

    public void addToReserve(Collection<Chromosome> chrs) {
        reserve.addAll(chrs);
    }

    public int getInitialPopulationSize() {
        return initialPopulationSize;
    }

    /*
    The TreeSet<Chromosome> source must be ordered in ascending orderof fitness
     */
    public ArrayList<Chromosome> nBestFit(
            int numberOfChromosomes,
            TreeSet<Chromosome> source
    ) {
        ArrayList<Chromosome> tmp = new ArrayList<>(numberOfChromosomes);
        int count = 0;
        var iter = source.descendingIterator();

        for (; count < numberOfChromosomes && iter.hasNext();) {
            tmp.add(iter.next());
            ++count;
        }

        if (isVerbose()) {
            if (count != numberOfChromosomes) {
                System.out.println("Less than " + numberOfChromosomes
                        + " chromosomes retrieved");
            }
        }

        return tmp;
    }

    /*
    The source ArrayList must be ordered in descending order of fitness
     */
    public boolean addNBestFitFromTo(
            int numberOfChromosomes,
            ArrayList<Chromosome> source,
            Collection<Chromosome> destination
    ) {

        int count = 0;

        for (int i = 0; i < source.size() && count < numberOfChromosomes; ++i) {
            if (destination.add(source.get(i))) {
                ++count;
            }
        }

        if (isVerbose()) {
            if (count != numberOfChromosomes) {
                /*System.out.println("Less than " + numberOfChromosomes
                        + " chromosomes added");*/
            }
        }

        return count == numberOfChromosomes;
    }

    public void addToBestFits(Chromosome chr) {
        bestFitByGeneration.add(chr);
    }

    public final void computeAverageFitness() {
        double sum = 0.0;

        for (Chromosome chr : population) {
            sum += chr.getFitness();
        }

        double averageFitness = sum / populationSize();
        addToAverageFitnesses(averageFitness);
    }

    public void addToAverageFitnesses(double averageFitness) {
        averageFitnesses.add(averageFitness);
    }

    public int populationSize() {
        return population.size();
    }

    public void computeFitnesses() {
        population.forEach(chr -> {
            chr.computeFitness();
        });
    }

    public double getCurrentAverageFitness() {
        return averageFitnesses.get(averageFitnesses.size() - 1);
    }

    public Chromosome getBestFit() {
        return bestFit;
    }

    public Chromosome getBestFitOfGeneration(int generation) {
        return bestFitByGeneration.get(generation);
    }

    public int getCurrentGenerationNumber() {
        return currentGenerationNumber;
    }

    public int getMaximumNumberOfGenerations() {
        return maximumNumberOfGenerations;
    }

    public int getGenerationForLocalMaximumTest() {
        return generationForLocalMaximumTest;
    }

    public ArrayList<Chromosome> getPopulation() {
        return population;
    }

    public int getRestartThreshold() {
        return restartThreshold;
    }

    public boolean hasEnded() {
        return getCurrentGenerationNumber() == getMaximumNumberOfGenerations();
    }

    public boolean hasSolution() {
        if (bestFit != null) {
            return bestFit.isSolution();
        } else {
            return false;
        }
    }

    public void incrementCurrentGeneration() {
        currentGenerationNumber += 1;
    }

    public final void setPopulation(ArrayList<Chromosome> chrs) {
        this.population = chrs;
    }

    public boolean isLocalMinimum() {
        if (getCurrentGenerationNumber() >= getGenerationForLocalMaximumTest()) {
            int gen = numberOfBestFits() - 1;
            int lastGen = getCurrentGenerationNumber() - getRestartThreshold() + 1;

            while (gen >= lastGen) {
                Chromosome chr1 = getBestFitOfGeneration(gen);
                Chromosome chr2 = getBestFitOfGeneration(gen - 1);

                if (Math.abs(chr1.getFitness() - chr2.getFitness()) > 1.0e-6) {
                    setGenerationForLocalMaximumTest(gen + getRestartThreshold());

                    return false;
                } else {
                    gen -= 1;
                }
            }

            setGenerationForLocalMaximumTest(
                    getCurrentGenerationNumber() + getRestartThreshold());

            return true;
        } else {
            return false;
        }
    }

    /*public boolean isValidPopulation(SDK sdk) {
        for (Chromosome chr : population) {
            if (!sdk.sameValuesAtGivens((SDK) chr)) {
                return false;
            }
        }

        return true;
    }*/
    public boolean isVerbose() {
        return verbose;
    }

    public double getCrossOverRate() {
        return crossOverRate;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public Selector getSelector() {
        return selector;
    }

    public Chromosome selectParent() {
        return getSelector().select();
    }

    public ArrayList<Chromosome> nextGeneration() {
        ArrayList<Chromosome> children = new ArrayList<>(population.size());

        while (children.size() < getInitialPopulationSize()) {
            Chromosome parent1 = selectParent();
            Chromosome parent2 = selectParent();
            Chromosome child1 = parent1.copy();
            Chromosome child2 = parent2.copy();
            child1.crossOver(child2, getCrossOverRate());
            child1.mutate(getMutationRate());
            child2.mutate(getMutationRate());
            children.add(child1);
            children.add(child2);
        }

        return children;
    }

    public int numberOfBestFits() {
        return bestFitByGeneration.size();
    }

    public void restart() {
        clearPopulation();
        addToPopulation(reserve);
    }

    public void clearPopulation() {
        population.clear();
    }

    public void addToPopulation(Collection<Chromosome> c) {
        population.addAll(c);
    }

    public void retainFractionOfPopulation() {
        if (getSelectionRate() < 1.0) {
            int nChrs = (int) (getInitialPopulationSize() * getSelectionRate());
            ArrayList<Chromosome> nBestFit = new ArrayList<>(nChrs);
            addNBestFitFromTo(nChrs, population, nBestFit);
            setPopulation(nBestFit);
        }
    }

    public double getSelectionRate() {
        return selectionRate;
    }

    @Override
    public void run() {
        while (getCurrentGenerationNumber() < getMaximumNumberOfGenerations()
                && !isStopped()) {

            if (isVerbose()) {
                if (getCurrentGenerationNumber() % 200 == 0) {
                    System.out.println("###Thread-" + name
                            + "...generation-" + getCurrentGenerationNumber()
                            + "...best_fitness = " + bestFitness()
                            + "...average_fitness = " + currentAverageFitness());
                }
            }

            if (isLocalMinimum()) {
                if (isVerbose()) {
                    System.out.println("@@@Thread-" + name
                            + "...generation-" + getCurrentGenerationNumber()
                            + "...RESTARTING"
                            + "...average_fitness = " + currentAverageFitness());
                }
                restart();
            }

            sortPopulation();
            computeAverageFitness();
            updateBestFit();
            addToBestFits(getBestFit());
            addNBestFitToReservePopulation();

            if (hasSolution()) {
                //System.out.println("Thread " + name + "\tgeneration " + getCurrentGeneration());
                return;
            }

            retainFractionOfPopulation();
            resetSelector(population);
            ArrayList<Chromosome> nextGen = nextGeneration();

            if (isVerbose()) {
                if (nextGen.size() < getInitialPopulationSize()) {
                    System.out.println("----------Population size has decreased !!!");
                }
            }

            setNextGeneration(nextGen);
            computeFitnesses();
            incrementCurrentGeneration();
        }
    }

    public boolean isStopped() {
        return stopped;
    }

    public Double bestFitness() {
        if (bestFit != null) {
            return bestFit.getFitness();
        } else {
            return null;
        }
    }

    public double currentAverageFitness() {
        return averageFitnesses.get(averageFitnesses.size() - 1);
    }

    public void resetSelector(ArrayList<Chromosome> chromos) {
        selector.setPopulation(chromos);
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public void setGenerationForLocalMaximumTest(int value) {
        generationForLocalMaximumTest = value;
    }

    public void setNextGeneration(ArrayList<Chromosome> children) {
        this.population = children;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public void setVerbose(boolean isVerbose) {
        this.verbose = isVerbose;
    }

    public void shufflePopulation() {
        for (int i = 0; i < population.size(); ++i) {
            int j = Rand.getInstance().nextInt(population.size());
            Chromosome tmp = population.get(i);
            population.set(i, population.get(j));
            population.set(j, tmp);
        }
    }

    public void sortPopulation() {
        Collections.sort(population, Collections.reverseOrder());
    }

    public void stop() {
        this.stopped = true;
    }

    @Override
    public String toString() {
        return "GASolver{"
                + "population =" + population
                + ", maxGenerations=" + maximumNumberOfGenerations
                + ", crossOverRate=" + crossOverRate
                + ", mutationRate=" + mutationRate
                + ", bestFit=" + bestFit
                + ", selector=" + selector
                + '}';
    }

    public void setBestFit(Chromosome chr) {
        bestFit = chr;
    }

    public void updateBestFit() {
        if (getBestFit() != null) {
            for (Chromosome chr : population) {
                if (chr.getFitness() > getBestFit().getFitness()) {
                    setBestFit(chr);
                }
            }
        } else {
            if (!population.isEmpty()) {
                setBestFit(population.get(0));
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    public static void test(String pathName) throws FileNotFoundException {
        System.out.println("Test");
        Chronometer chrono = new Chronometer(5000);
        Thread chronoThread = new Thread(chrono);
        chronoThread.start();
        ArrayList<Integer> solutionSeeds = new ArrayList<>();
        ArrayList<Integer> noSolutionSeeds = new ArrayList<>();
        Settings settings = new Settings(pathName);
        System.out.println("settings = " + settings);
        int nSeeds = 1;
        Sudoku sdk = new Sudoku(settings.getGridPath());
        System.out.println("initial grid =\n" + sdk.printGrid());
        System.out.println("initial fitness = " + sdk.getFitness());

        if (sdk.isSolution()) {
            System.exit(0);
        }

        int nThreads = settings.getNumberOfThreads();
        Thread[] threads = new Thread[nThreads];
        GASolver[] solvers = new GASolver[nThreads];

        for (int i = 0; i < nSeeds; ++i) {
            System.out.println("*****************************seed = " + i);
            Rand.getInstance().setSeed(i);
            System.out.println("Rand.getInstance().getSeed() = "
                    + Rand.getInstance().getSeed());

            for (int j = 0; j < nThreads; ++j) {
                //System.out.println("initializing threads");
                ArrayList<Chromosome> chrs = sdk.randomChromosomes(settings.getPopulationSize());
                int min = 10000;
                Sudoku best = null;

                for (int k = 0; k < chrs.size(); ++k) {
                    Sudoku tmp = (Sudoku) chrs.get(k);
                    int nErrors = tmp.numberOfErrors();

                    if (nErrors < min) {
                        min = nErrors;
                        best = tmp;
                    }
                }

                solvers[j] = new GASolver(
                        //sdk,
                        chrs,
                        settings.getSelector(j),
                        new Settings(pathName),
                        Integer.toString(j)
                );
                solvers[j].setVerbose(true);
                //solvers[j].setSelector(settings.getSelection()[j]);
                threads[j] = new Thread(solvers[j]);
            }

            for (int k = 0; k < nThreads; ++k) {
                threads[k].start();
            }

            String threadName = "";
            Chromosome bestFit = null;
            double bestFitness = 0;
            boolean on = true;

            while (on) {
                for (GASolver solver : solvers) {
                    if (solver.hasSolution()) {
                        solutionSeeds.add(i);
                        Sudoku solution = ((Sudoku) solver.getBestFit());
                        System.out.println("solution found");
                        System.out.println("soludion =\n" + solution.printGrid());

                        for (GASolver s : solvers) {
                            s.setStopped(true);
                        }

                        on = false;
                        break;
                    } else {
                        if (!solver.hasEnded()) {
                            if (solver.getBestFit() != null) {
                                if (bestFit != null) {
                                    if (solver.getBestFit().getFitness() > bestFit.getFitness()) {
                                        bestFit = solver.getBestFit();
                                        bestFitness = solver.getBestFit().getFitness();
                                        threadName = "Thread-" + solver.name;
                                        System.out.println(threadName
                                                + ": gen = "
                                                + solver.getCurrentGenerationNumber()
                                                + "...bestFitness = " + bestFitness
                                                + "...average fitness = "
                                                + solver.getCurrentAverageFitness()
                                                + "\n...bestFit =\n" + ((Sudoku) bestFit).printGrid());
                                    }
                                } else {
                                    bestFit = solver.getBestFit();
                                    bestFitness = solver.getBestFit().getFitness();
                                    threadName = "Thread-" + solver.name;
                                    System.out.println(threadName
                                            + ": gen = "
                                            + solver.getCurrentGenerationNumber()
                                            + "...bestFitness = " + bestFitness
                                            + "...average fitness = "
                                            + solver.getCurrentAverageFitness()
                                            + "\n...bestFit =\n" + ((Sudoku) bestFit).printGrid());
                                }
                            }
                        }
                    }
                }

                if (on) {
                    boolean allHaveEnded = true;

                    for (GASolver s : solvers) {
                        if (!s.hasEnded()) {
                            allHaveEnded = false;
                        }
                    }

                    if (allHaveEnded) {
                        noSolutionSeeds.add(i);
                        on = false;
                        break;
                    }
                }
            }
        }

        System.out.println("seeds that have a solution");
        solutionSeeds.forEach(i -> {
            System.out.println("seed-" + i);
        });

        System.out.println("seeds that don't have a solution");
        noSolutionSeeds.forEach(i -> {
            System.out.println("seed-" + i);
        });

        chrono.setStopped(true);
        System.exit(0);
    }

    public static void main(String[] args) throws FileNotFoundException {
        test("/home/john/NetBeansProjects/Sudoku/src/sudoku/settings_test.txt");
    }
}
