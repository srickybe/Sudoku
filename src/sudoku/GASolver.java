/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author ricky
 */
public class GASolver implements Runnable {

    private Population population;
    private final int initialPopulationSize;
    private final int criticalSizeOfReserve;
    private final int maximumNumberOfGenerations;
    private final double crossOverRate;
    private final double mutationRate;
    private final double selectionRate;
    private final int restartThreshold;
    private Chromosome bestFit;
    private final ArrayList<Chromosome> bestFitByGeneration;
    private final ArrayList<Double> averageFitnesses;
    private final Reserve reserve;
    private Selector selector;
    private final String name;
    private int currentGenerationNumber;
    private int generationForLocalMaximumTest;
    private boolean stopped;
    private boolean verbose;

    public GASolver(
            ArrayList<Chromosome> chrs,
            Selector selector,
            Settings settings,
            String name
    ) {
        this.initialPopulationSize = chrs.size();
        this.criticalSizeOfReserve = settings.getCriticalSizeOfReserve();
        this.restartThreshold = settings.getRestartThreshold();
        this.bestFitByGeneration
                = new ArrayList<>(settings.getMaximumNumberOfGenerations());
        this.maximumNumberOfGenerations
                = settings.getMaximumNumberOfGenerations();
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
        this.reserve = new Reserve();
        this.stopped = false;
        this.verbose = true;
    }

    /*
    population must be sorted in descending order
     */
    public boolean addNBestFitFromPopulationToReserve(int nChrs) {
        if (nChrs <= 0) {
            throw new IllegalArgumentException();
        }

        int count = 0;

        for (Chromosome chr : population) {
            if (reserve.add(chr)) {
                ++count;
            }

            if (count == nChrs) {
                break;
            }
        }

        if (isVerbose()) {
            if (count != nChrs) {
                /*System.out.println("Less than " + nChrs
                        + " chromosomes added");*/
            }
        }

        return count == nChrs;
    }

    public void addNBestFitToReserve() {
        int numberOfChromosomes = populationSize() / getRestartThreshold();
        addNBestFitFromPopulationToReserve(numberOfChromosomes);

        if (sizeOfReserve() > getCriticalSizeOfReserve()) {
            if (isVerbose()) {
                System.out.println("###Thread-" + name
                        + "...reserve has passed the maximum");
                System.out.println("...reserve size = " + reserve.getNumberOfChromosomes());
            }

            ArrayList<Chromosome> nBestFit;
            nBestFit = getNBestFitFromReserve(getInitialPopulationSize());
            clearReserve();
            addToReserve(nBestFit);

            if (isVerbose()) {
                System.out.println("new size = " + sizeOfReserve());
            }
        }
    }

    public void addToAverageFitnesses(double averageFitness) {
        averageFitnesses.add(averageFitness);
    }

    public void addToReserve(Collection<Chromosome> chrs) {
        for (Chromosome chr : chrs) {
            reserve.add(chr);
        }
    }

    public void addToBestFits(Chromosome chr) {
        bestFitByGeneration.add(chr);
    }

    public void clearPopulation() {
        population.clear();
    }

    public Double bestFitness() {
        if (bestFit != null) {
            return bestFit.getFitness();
        } else {
            return null;
        }
    }

    public void clearReserve() {
        reserve.clear();
    }

    public final void computeAverageFitness() {
        addToAverageFitnesses(population.averageFitness());
    }

    public void computeFitnesses() {
        population.computeFitnesses();
    }

    public double currentAverageFitness() {
        return averageFitnesses.get(averageFitnesses.size() - 1);
    }

    public Chromosome getBestFit() {
        return bestFit;
    }

    public Chromosome getBestFitOfGeneration(int generation) {
        return bestFitByGeneration.get(generation);
    }

    public int getCriticalSizeOfReserve() {
        return criticalSizeOfReserve;
    }

    public double getCrossOverRate() {
        return crossOverRate;
    }

    public double getCurrentAverageFitness() {
        return averageFitnesses.get(averageFitnesses.size() - 1);
    }

    public int getCurrentGenerationNumber() {
        return currentGenerationNumber;
    }

    public int getInitialPopulationSize() {
        return initialPopulationSize;
    }

    public int getGenerationForLocalMaximumTest() {
        return generationForLocalMaximumTest;
    }

    public int getMaximumNumberOfGenerations() {
        return maximumNumberOfGenerations;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public ArrayList<Chromosome> getNBestFitFromPopulation(int nChrs) {
        Pair<ArrayList<Chromosome>, Boolean> res;
        res = population.getNBestFit(nChrs);

        return res.getFirst();
    }

    public ArrayList<Chromosome> getNBestFitFromReserve(int nChrs) {
        Pair<ArrayList<Chromosome>, Boolean> res;
        res = reserve.getNBestFit(nChrs);

        return res.getFirst();
    }

    public ArrayList<Chromosome> getPopulation() {
        return population.toArrayList();
    }

    public int getRestartThreshold() {
        return restartThreshold;
    }

    public double getSelectionRate() {
        return selectionRate;
    }

    public Selector getSelector() {
        return selector;
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

    public boolean isStopped() {
        return stopped;
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

    public ArrayList<Chromosome> nextGeneration() {
        ArrayList<Chromosome> children = new ArrayList<>(populationSize());

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

    public int populationSize() {
        return population.size();
    }

    public void resetSelector() {
        selector.setPopulation(population.toArrayList());
    }

    public void restart() {
        clearPopulation();
        setPopulation(reserve.toArrayList());
    }

    public void retainFractionOfPopulation() {
        if (getSelectionRate() < 1.0) {
            int nChrs = (int) (getInitialPopulationSize() * getSelectionRate());
            ArrayList<Chromosome> nBestFit = getNBestFitFromPopulation(nChrs);
            setPopulation(nBestFit);
        }
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
                            + "...average_fitness = " + currentAverageFitness()
                            + "...reserve size = " + reserve.getNumberOfChromosomes());
                }
            }

            if (isLocalMinimum()) {
                if (isVerbose()) {
                    System.out.println("@@@Thread-" + name
                            + "...generation-" + getCurrentGenerationNumber()
                            + "...RESTARTING"
                            + "...average_fitness = " + currentAverageFitness()
                            + "...reserve size = " + sizeOfReserve());
                }
                restart();
            }

            sortPopulation();
            computeAverageFitness();
            updateBestFit();
            addToBestFits(getBestFit());
            addNBestFitToReserve();

            if (hasSolution()) {
                //System.out.println("Thread " + name + "\tgeneration " + getCurrentGeneration());
                return;
            }

            retainFractionOfPopulation();
            resetSelector();
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

    public void setBestFit(Chromosome chr) {
        bestFit = chr;
    }

    public final void setPopulation(ArrayList<Chromosome> chrs) {
        this.population = new Population(chrs);
    }

    public Chromosome selectParent() {
        return getSelector().select();
    }

    public void setGenerationForLocalMaximumTest(int value) {
        generationForLocalMaximumTest = value;
    }

    public void setNextGeneration(ArrayList<Chromosome> children) {
        this.population = new Population(children);
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public void setVerbose(boolean isVerbose) {
        this.verbose = isVerbose;
    }

    public void shufflePopulation() {
        population.shuffle();
    }

    public int sizeOfReserve() {
        return reserve.getNumberOfChromosomes();
    }

    public void sortPopulation() {
        population.sortInDescendingOrder();
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

    public void updateBestFit() {
        if (getBestFit() != null) {
            for (Chromosome chr : population) {
                if (chr.getFitness() > getBestFit().getFitness()) {
                    setBestFit(chr);
                }
            }
        } else {
            if (!population.isEmpty()) {
                setBestFit(population.first());
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    public static void test(String pathName) throws FileNotFoundException {
        System.out.println("Test");
        ArrayList<Integer> solutionSeeds = new ArrayList<>();
        ArrayList<Integer> noSolutionSeeds = new ArrayList<>();
        Settings settings = new Settings(pathName);
        System.out.println("settings = " + settings);
        int startSeed = 2;
        int endSeed = 3;
        Sudoku sdk = new Sudoku(settings.getGridPath());
        System.out.println("initial grid =\n" + sdk.printGrid());
        System.out.println("initial fitness = " + sdk.getFitness());

        if (sdk.isSolution()) {
            System.exit(0);
        }

        int nThreads = settings.getNumberOfThreads();
        Thread[] threads = new Thread[nThreads];
        GASolver[] solvers = new GASolver[nThreads];
        ArrayList<Pair<Sudoku, Double>> bestFits = new ArrayList<>();

        for (; startSeed < endSeed; ++startSeed) {
            Chronometer chrono = new Chronometer(1000);
            Thread chronoThread = new Thread(chrono);
            chronoThread.start();

            System.out.println("*****************************seed = " + startSeed);
            Rand.getInstance().setSeed(startSeed);
            System.out.println("Rand.getInstance().getSeed() = "
                    + Rand.getInstance().getSeed());

            for (int j = 0; j < nThreads; ++j) {
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
                        chrs,
                        settings.getSelector(j),
                        new Settings(pathName),
                        Integer.toString(j)
                );
                solvers[j].setVerbose(true);
                threads[j] = new Thread(solvers[j]);
            }

            for (int k = 0; k < nThreads; ++k) {
                threads[k].start();
            }

            String threadName;
            Chromosome bestFit = null;
            double bestFitness;
            boolean on = true;

            while (on) {
                for (GASolver solver : solvers) {
                    if (solver.hasSolution()) {
                        solutionSeeds.add(startSeed);
                        Sudoku solution = ((Sudoku) solver.getBestFit());
                        System.out.println("solution found");
                        System.out.println("soludion =\n" + solution.printGrid());
                        bestFits.add(new Pair<>(solution, solution.getFitness()));

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
                        noSolutionSeeds.add(startSeed);
                        bestFits.add(new Pair<>((Sudoku) bestFit, bestFit.getFitness()));
                        on = false;
                        break;
                    }
                }
            }

            chrono.setStopped(true);
        }

        System.out.println("seeds that have a solution");
        solutionSeeds.forEach(i -> {
            System.out.println("seed-" + i);
        });

        System.out.println("seeds that don't have a solution");
        noSolutionSeeds.forEach(i -> {
            System.out.println("seed-" + i);
        });

        for (int j = 0; j < bestFits.size(); ++j) {
            var pair = bestFits.get(j);
            System.out.println(
                    "+++++best solution =\n" + pair.getFirst().printGrid()
                    + "\nfitness = " + pair.getSecond()
                    + "+++++\n\n");
        }

        System.exit(0);
    }

    public static void main(String[] args) throws FileNotFoundException {
        test("/home/john/NetBeansProjects/Sudoku/src/sudoku/settings_test.txt");
    }
}
